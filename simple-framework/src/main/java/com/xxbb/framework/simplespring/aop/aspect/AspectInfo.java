package com.xxbb.framework.simplespring.aop.aspect;


import com.xxbb.framework.simplespring.aop.PointcutLocator;

/**
 * @author xxbb
 */
public class AspectInfo {
    private int orderIndex;
    private DefaultAspect aspectObject;
    private PointcutLocator pointcutLocator;

    public AspectInfo() {
    }

    public AspectInfo(int orderIndex, DefaultAspect aspectObject, PointcutLocator pointcutLocator) {
        this.orderIndex = orderIndex;
        this.aspectObject = aspectObject;
        this.pointcutLocator = pointcutLocator;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public DefaultAspect getAspectObject() {
        return aspectObject;
    }

    public PointcutLocator getPointcutLocator() {
        return pointcutLocator;
    }
}
