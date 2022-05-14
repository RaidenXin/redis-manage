package com.raiden.redis.ui.controller;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 8:39 2022/5/14
 * @Modified By:
 */
public final class BeanContext {

    private static final ConcurrentHashMap<String, Object> CONTROLLER_CACHE = new ConcurrentHashMap<>();

    public static final <T> T getBean(String beanName){
        return (T) CONTROLLER_CACHE.get(beanName);
    }

    public static final void setBean(String beanName,Object bean){
        CONTROLLER_CACHE.put(beanName, bean);
    }
}
