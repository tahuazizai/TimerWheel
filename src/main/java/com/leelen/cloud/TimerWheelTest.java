package com.leelen.cloud;

import com.alibaba.fastjson.JSON;
import com.leelen.cloud.annotations.TimerWheel;
import com.leelen.cloud.annotations.TimerWheelMethod;
import com.leelen.cloud.entity.TestDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * @version: 1.00.00
 * @description: 时间轮测试
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-11-06 17:48
 */
@TimerWheel
@Slf4j
public class TimerWheelTest {

    @TimerWheelMethod
    public void test(TestDTO testDTO) {
        log.info("testDTo={}", JSON.toJSONString(testDTO));
    }
}
