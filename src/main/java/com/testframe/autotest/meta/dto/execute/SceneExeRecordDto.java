package com.testframe.autotest.meta.dto.execute;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * 场景执行时的记录
 * @date:2022/11/04 22:48
 * @author: lyf
 */
@Data
public class SceneExeRecordDto {

    // 场景名称
    private String sceneName;

    // 访问地址
    private String testUrl;

    // 等待方式
    private Integer waitType;

    // 等待时间
    private Integer waitTime;

    // 执行结果
    private Integer status;

    private Long sceneExeTime;

    private List<StepExeRecordDto> stepExeRecordDtos;
}
