package com.leelen.cloud.controller;

import com.leelen.cloud.entity.TestDTO;
import com.leelen.cloud.utils.TimingWheelUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @version: 1.00.00
 * @description: 时间轮测试类
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-11-08 13:03
 */
@RestController
@RequestMapping("/test")
public class TimingWheelTestController {
    @PostMapping("/addTask")
    public void addTask() {
        TestDTO testDTO = new TestDTO("12", "xiaowang");
        TimingWheelUtil.taskAdd(testDTO, 10, TimeUnit.SECONDS);
    }
}
