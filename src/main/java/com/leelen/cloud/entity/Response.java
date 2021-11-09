package com.leelen.cloud.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @version: 1.00.00
 * @description: 应答
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-10-28 19:43
 */
@Data
@Builder
public class Response implements Serializable {
    /**
     * 插槽下标
     */
    private Integer tick;
    /**
     * 执行时间
     */
    private Long executeTime;
}
