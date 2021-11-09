package com.leelen.cloud.timerwheel;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.leelen.cloud.annotations.TimerWheelMethod;
import com.leelen.cloud.entity.Subcriber;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @version: 1.00.00
 * @description: 订阅者
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-10-28 14:29
 */
public final class SubcriberRegister {
    private static final SubcriberRegister SUBCRIBER_REGISTER = new SubcriberRegister();

    private static final Map<Class<?>, CopyOnWriteArraySet<Subcriber>> TASK_MAP = Maps.newConcurrentMap();


    private SubcriberRegister() {

    }

    public static SubcriberRegister getInstance() {
        return SUBCRIBER_REGISTER;
    }

    /**
     * 通过类类型获取任务消费对象
     *
     * @param className
     * @return
     */
    public CopyOnWriteArraySet<Subcriber> getSubcribers(Class<?> className) {
        return TASK_MAP.get(className);
    }

    /**
     * 注册订阅者
     *
     * @param object
     */
    public void register(Object object) {
        Multimap<Class<?>, Subcriber> multimap = this.findAllSubscribers(object);
        Iterator<Map.Entry<Class<?>, Collection<Subcriber>>> it = multimap.asMap().entrySet().iterator();
        CopyOnWriteArraySet eventSubscribers;
        while (it.hasNext()) {
            Map.Entry<Class<?>, Collection<Subcriber>> ele = it.next();
            eventSubscribers = this.getSubcribers(ele.getKey());
            if (eventSubscribers == null) {
                eventSubscribers = new CopyOnWriteArraySet();
                TASK_MAP.putIfAbsent(ele.getKey(), eventSubscribers);
            }
            eventSubscribers.addAll(ele.getValue());
        }
    }

    /**
     * 取消注册
     *
     * @param object
     */
    public void unregister(Object object) {
        Multimap<Class<?>, Subcriber> multimap = this.findAllSubscribers(object);
        Iterator<Map.Entry<Class<?>, Collection<Subcriber>>> it = multimap.asMap().entrySet().iterator();
        CopyOnWriteArraySet eventSubscribers;
        while (it.hasNext()) {
            Map.Entry<Class<?>, Collection<Subcriber>> ele = it.next();
            eventSubscribers = this.getSubcribers(ele.getKey());
            if (!CollectionUtils.isEmpty(eventSubscribers)) {
                eventSubscribers.removeAll(ele.getValue());
            }
        }
    }

    /**
     * 获取所有订阅者
     *
     * @param object
     */
    private Multimap<Class<?>, Subcriber> findAllSubscribers(Object object) {
        Multimap<Class<?>, Subcriber> methodsInListener = HashMultimap.create();
        Class<?> subcriberClass = object.getClass();
        List<Method> methodList = this.getMethodsByClass(subcriberClass);
        for (Method method : methodList) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            methodsInListener.put(parameterTypes[0], Subcriber.create(method, object));
        }
        return methodsInListener;
    }

    /**
     * 获取当前类的所有订阅方法
     *
     * @param subcriberClass
     * @return
     */
    private List<Method> getMethodsByClass(Class<?> subcriberClass) {
        List<Class<?>> classList = getAllSupperClass(subcriberClass);
        Map<SubcriberRegister.Identify, Method> identifyMap = Maps.newHashMap();
        for (Class<?> cl : classList) {
            Method[] methods = ReflectionUtils.getDeclaredMethods(cl);
            for (Method method : methods) {
                if (method.isAnnotationPresent(TimerWheelMethod.class) && !method.isSynthetic()) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Preconditions.checkArgument(parameterTypes.length == 1, "Method %s has @Subscribe annotation but has "
                                    + "%s parameters.Subscriber methods must have exactly 1 parameter.",
                            method, parameterTypes.length);
                    SubcriberRegister.Identify identify = new SubcriberRegister.Identify(method.getName(), Arrays.asList(parameterTypes));
                    if (!identifyMap.containsKey(method.getName())) {
                        identifyMap.put(identify, method);
                    }
                }
            }
        }
        return Lists.newArrayList(identifyMap.values());
    }

    /**
     * 获取当前类的所有父类
     *
     * @param subcriberClass
     * @return
     */
    private List<Class<?>> getAllSupperClass(Class<?> subcriberClass) {
        List<Class<?>> classList = Lists.newArrayList();
        classList.add(subcriberClass);
        Class<?> supperClass = subcriberClass.getSuperclass();
        while (supperClass != Object.class) {
            classList.add(subcriberClass);
            subcriberClass = supperClass.getSuperclass();
        }
        return classList;
    }

    public static class Identify {

        public Identify(String methodName, List<Class<?>> parameterTypes) {
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
        }

        /**
         * 方法名称
         */
        private String methodName;
        /**
         * 参数类型
         */
        private List<Class<?>> parameterTypes;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Identify identify = (Identify) o;
            return methodName.equals(identify.methodName) &&
                    parameterTypes.equals(identify.parameterTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(methodName, parameterTypes);
        }
    }
}
