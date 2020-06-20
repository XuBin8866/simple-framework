package com.xxbb.framework.simplespring.mvc.cache;


import com.xxbb.framework.simplespring.util.LogUtil;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xxbb
 */
public class ResultCache<K, V> {
    /**
     * 双向节点链表
     */
    static class Node<K, V> {
        K key;
        V value;
        Node<K, V> pre;
        Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * 默认初始容量16
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    /**
     * 负载因子
     */
    private static final float DEFAULT_LOAD_FACTORY = 0.75f;

    private final Logger log = LogUtil.getLogger();
    /**
     * 缓存
     */
    private final Map<K, Node<K, V>> caches;
    /**
     * 线程池
     */
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    /**
     * 需要执行的任务
     */
    private Callable<V> task;
    /**
     * 可重入锁
     */
    private final Lock lock = new ReentrantLock();
    /**
     * 头节点
     */
    private Node<K, V> first;
    /**
     * 尾节点
     */
    private Node<K, V> last;
    /**
     * 当前存放节点个数
     */
    private int currentSize;
    /**
     * 缓存需要的容器的大小,
     */
    private int capacity;
    /**
     * 为避免出现扩容情况，map的真实容器大小满足以下关系
     * capacity+1=realCapacity*DEFAULT_LOAD_FACTORY
     * 即realCapacity=(capacity+1)/DEFAULT_LOAD_FACTORY
     * 但是由于浮点数转整型数精度丢失的问题，有可能（在0到50内就出现了很多次）
     * 会出现realCapacity*DEFAULT_LOAD_FACTORY=capacity的情况，
     * 所以这里的计算要向上取整Math.cell()
     * <p>
     * 当需要缓存容器大小 大于等于6291456时，需要的大小和扩容阈值会相等
     * 但是我们的容器不可能取六百万这么大，所以可以忽略这种情况
     */
    private int realCapacity;

    public ResultCache(int initCapacity) {
        if (initCapacity > 0) {
            this.currentSize = 0;
            this.capacity = initCapacity;
            this.realCapacity = (int) Math.ceil((capacity + 1) / DEFAULT_LOAD_FACTORY);
            this.caches = new HashMap<>(realCapacity);
        } else {
            throw new RuntimeException("init ResponseCaches failed: initCapacity<=0" + initCapacity);
        }

    }

    public ResultCache() {
        this.currentSize = 0;
        this.capacity = DEFAULT_INITIAL_CAPACITY;
        this.realCapacity = (int) Math.ceil((capacity + 1) / DEFAULT_LOAD_FACTORY);
        this.caches = new ConcurrentHashMap<>(realCapacity);
    }

    /**
     * 设置任务
     *
     * @param task 任务
     */
    public void setTask(Callable<V> task) {
        this.task = task;
    }

    /**
     * 清除所有节点
     */
    public void clear() {
        first = null;
        last = null;
        caches.clear();
    }

    /**
     * 移除节点
     *
     * @param key key
     * @return value
     */
    public Object remove(K key) {
        Node<K, V> node = caches.get(key);
        if (null != node) {
            if (null != node.pre) {
                node.pre.next = node.next;
            }
            if (null != node.next) {
                node.next.pre = node.pre;
            }
            if (node == first) {
                first = node.next;
            }
            if (node == last) {
                last = node.pre;
            }
            currentSize--;
        }
        return caches.remove(key);
    }

    /**
     * 获取缓存内容
     *
     * @param key key
     * @return value
     */
    public V get(K key) {
        lock.lock();
        try {
            while (true) {
                Node<K, V> node = caches.get(key);

                if (node == null) {
                    log.info("该请求的响应缓存不存在，调用线程执行任务");
                    try {
                        Future<V> future = pool.submit(task);
                        node = new Node<>(key, future.get());
                        put(key, node);
                    } catch (ExecutionException | InterruptedException e) {
                        log.error(e.getMessage());
                    }
                } else {
                    log.info("该请求的响应缓存存在：{}", node.value);
                }

                moveToHead(node);
                //获取出错则删除缓存，避免污染
                try {
                    assert node != null;
                    return node.value;
                } catch (Exception e) {
                    remove(key);
                    log.error(e.getMessage());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 添加节点
     *
     * @param key   key
     * @param value value
     */
    private void put(K key, Node<K, V> value) {
        if (currentSize >= capacity) {
            //移除map中节点
            caches.remove(last.key);
            //将节点从链表中移除
            removeLast();
        }
        caches.putIfAbsent(key, value);
        //将节点移至首部的操作放在get方法里了
        currentSize++;

    }

    /**
     * 新节点添加到链表头
     *
     * @param node node
     */
    private void moveToHead(Node<K, V> node) {
        if (first == node) {
            return;
        }
        //节点为已存在节点的情况
        // 将节点先取出
        if (node.next != null) {
            node.next.pre = node.pre;
        }
        if (node.pre != null) {
            node.pre.next = node.next;
        }
        //如果node为最后一个节点
        if (last == node) {
            last = last.pre;
        }
        if (first == null || last == null) {
            first = last = node;
            return;
        }
        //节点置于链表头
        node.next = first;
        first.pre = node;
        first = node;
        first.pre = null;
    }

    /**
     * 移除链表最后一个节点
     */
    public void removeLast() {
        if (last != null) {
            last = last.pre;
            //还为空说明容器内没有节点
            if (last == null) {
                first = null;
            } else {
                last.next = null;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node<K, V> node = first;
        while (node != null) {
            try {
                sb.append(String.format("%s=%s", node.key, node.value));
                sb.append(" --->");
                node = node.next;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return sb.toString();
    }

    public int size() {
        return currentSize;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRealCapacity() {
        return realCapacity;
    }

}

