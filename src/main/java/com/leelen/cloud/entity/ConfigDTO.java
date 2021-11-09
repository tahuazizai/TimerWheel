package com.leelen.cloud.entity;

/**
 * @version: 1.00.00
 * @description: 配置实体
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-11-06 18:25
 */
public class ConfigDTO {
    public String getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String basePackages) {
        this.basePackages = basePackages;
    }

    /**
     * 包路径
     */
    private String basePackages;
}
