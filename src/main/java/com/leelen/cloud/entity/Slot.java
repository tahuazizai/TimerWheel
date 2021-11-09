package com.leelen.cloud.entity;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @version: 1.00.00
 * @description: 插槽
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-10-27 13:32
 */
@Data
public class Slot implements Serializable {

    private static final long serialVersionUID = -1729808687997773580L;

    public Slot() {
    }

    public Slot(Integer id) {
        this.id = id;
    }

    /**
     * 插槽id
     */
    private Integer id;

    /**
     * 元素集合
     */
    private ConcurrentMap<Long, CopyOnWriteArraySet> slotMap = Maps.newConcurrentMap();

    /**
     * 添加元素
     *
     * @param t
     * @param time
     */
    public <T> void add(T t, long time) {
        Multimap<Long, Object> multimap = HashMultimap.create();
        multimap.put(time, t);
        Collection copyOnWriteArraySet = multimap.asMap().get(time);
        CopyOnWriteArraySet copyOnWriteArraySet1 = slotMap.get(time);
        if (copyOnWriteArraySet1 == null) {
            copyOnWriteArraySet1 = Sets.newCopyOnWriteArraySet();
            slotMap.putIfAbsent(time, copyOnWriteArraySet1);
        }
        copyOnWriteArraySet1.addAll(copyOnWriteArraySet);
    }

    /**
     * 移除元素
     *
     * @param time
     */
    public <T> void remove(long time, T t) {
        CopyOnWriteArraySet copyOnWriteArraySet = slotMap.get(time);
        if (CollectionUtils.isEmpty(copyOnWriteArraySet)) {
            return;
        }
        if (t != null) {
            copyOnWriteArraySet.remove(t);
            return;
        }
        copyOnWriteArraySet.clear();
    }

    /**
     * 获取元素
     *
     * @param time
     * @return
     */
    public CopyOnWriteArraySet get(Long time) {
        CopyOnWriteArraySet copyOnWriteArraySet = Sets.newCopyOnWriteArraySet();
        if (time == null) {
            Iterator<Map.Entry<Long, CopyOnWriteArraySet>> it = slotMap.entrySet().iterator();
            while (it.hasNext()) {
                copyOnWriteArraySet.addAll(it.next().getValue());
            }
            return copyOnWriteArraySet;
        }
        if (CollectionUtils.isEmpty(slotMap.get(time))) {
            return new CopyOnWriteArraySet();
        }
        return new CopyOnWriteArraySet(slotMap.get(time));
    }

}
