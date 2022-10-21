package com.testframe.autotest.meta.dto;

import lombok.Data;

@Data
public class SceneInfoDto {

    private Long sceneId;

    // 场景名称
    private String name;

    // 场景描述
    private String desc;

    // 等待方式
    private Integer waitType;

    // 等待时间
    private Integer waitTime;
}
