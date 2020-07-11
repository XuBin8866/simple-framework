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
     * 为避免出现扩容情况，并且考虑浮点乘除计算结果转int型时精度缺失的问题
     * （无论乘除只要不是明确的数计算转int都会直接去除小数取整）
     * 使得会出现这样的情况：
     * capacity=77，将他作为扩容阈值计算得出实际容器大小：realCapacity=77/0.75=102.66667，转int型得102，
     * 然而通过102计算扩容阈值threshold=102*0.75=76.5,(int)76.5=76,哦豁，扩容阈值小于我们的缓存容量了，会触发扩容机制。
     * 于是map的真实容器大小满足以下关系
     * capacity+1=realCapacity*DEFAULT_LOAD_FACTORY
     * 即realCapacity=(capacity+1)/DEFAULT_LOAD_FACTORY，
     * 这样能保证HashMap在计算阈值时至少有：realCapacity*DEFAULT_LOAD_FACTORY>=capacity的情况，
     *
     * 也可以不加一直接将结果向上取整realCapacity=Math.cell(capacity/DEFAULT_LOAD_FACTORY),
     * 这样能保证HashMap在计算阈值时一定有realCapacity*DEFAULT_LOAD_FACTORY=capacity情况
     * 这里我选择前者，capacity+1的算法，避免使用Math的方法。
     *
     *
     * <p:回顾当时思路>
     * 之前这里的HashMap实际容量计算向上取整即realCapacity=Math.cell((capacity+1)/DEFAULT_LOAD_FACTORY)，
     * 这样能保证计算得到的实际HashMap容器大小的扩容阈值一定会比缓存可用容量大1，保证不触发扩容
     * （当需要缓存容器大小>=6291456时，需要的大小和扩容阈值会相等）
     * 因为之前认为HashMap当容器元素个数等于扩容阈值就会触发扩容，实际上是要大于扩容阈值才会触发
     *
     *
     * <p:回顾当时思路>
     * 同时实际上HashMap是先存储元素，再进行扩容阈值判断的（之前没认真看HashMap,把他和List搞混了），
     * 且需要当前元素个数大于扩容阈值才触发
     * 所以这里即使HashMap的扩容阈值和我们的缓存容量大小相等也是没有关系的，
     * 因为当容器内元素个数和缓存容器大小相等时会触发尾部元素的淘汰，容器元素个数不会大于扩容阈值
     */
    private int realCapacity;

    public ResultCache(int initCapacity) {
        if (initCapacity > 0) {
            this.currentSize = 0;
            this.capacity = initCapacity;
            this.realCapacity= (int) ((capacity+1)/DEFAULT_LOAD_FACTORY);
            this.caches = new HashMap<>(realCapacity);
        } else {
            throw new RuntimeException("init ResponseCaches failed: initCapacity<=0" + initCapacity);
        }

    }

    public ResultCache() {
        this.currentSize = 0;
        this.capacity = DEFAULT_INITIAL_CAPACITY;
        this.realCapacity= (int) ((capacity+1)/DEFAULT_LOAD_FACTORY);
        this.caches = new HashMap<>(realCapacity);
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
        currentSize = 0;
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

            Node<K, V> node = caches.get(key);

            if (node == null) {
                log.debug("该请求的响应缓存不存在，调用线程执行任务");
                try {
                    if(task==null){
                        throw new RuntimeException("Callable task is null");
                    }
                    Future<V> future=pool.submit(task);
                    //循环等待线程执行完成
                    boolean flag=true;
                    while(flag){
                        if(future.isDone()&&!future.isCancelled()){
                            node = new Node<>(key, future.get());
                            put(key, node);
                            flag=false;
                        }
                    }
                } catch (ExecutionException | InterruptedException e) {
                    log.error(e.getMessage());
                    remove(key);
                    //之前将while循环放在了整个方法上，只要没获取到结果就一直循环获取，这时一定要抛出异常，不然死循环
                    //现在while只用于等待线程结果
                    throw new RuntimeException(e);
                }
            } else {
                log.debug("该请求的响应缓存存在：{}", node.value);
            }

            moveToHead(node);
            //获取出错则删除缓存，避免污染
            try {
                return node.value;
            } catch (Exception e) {
                remove(key);
                log.error(e.getMessage());
            }
        } finally {
            this.task=null;
            lock.unlock();
        }
        return null;
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
            Node<K, V> remove = caches.remove(last.key);
            log.debug("缓存已满，删除最近最少使用的缓存元素：{}", remove);
            currentSize--;
            //将节点从链表中移除
            removeLast();
        }
        //不使用putIfAbsent的原因是无法判断是否向缓存中添加了元素
        caches.put(key, value);

        currentSize++;

        //将节点移至首部的操作放在get方法里了


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

    public int getHashMapThreshold(){
        return (int)(realCapacity*DEFAULT_LOAD_FACTORY);
    }

}

