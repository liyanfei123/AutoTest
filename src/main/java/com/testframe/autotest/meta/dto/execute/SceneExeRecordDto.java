package com.testframe.autotest.meta.dto.execute;

import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * 场景执行时的单个记录
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

    public static void build(SceneExeRecordDto sceneExeRecordDto, SceneExecuteRecord sceneExecuteRecord) {
        sceneExeRecordDto.setSceneName(sceneExecuteRecord.getSceneName());
        sceneExeRecordDto.setTestUrl(sceneExecuteRecord.getUrl());
        sceneExeRecordDto.setWaitType(sceneExecuteRecord.getWaitType());
        sceneExeRecordDto.setWaitTime(sceneExecuteRecord.getWaitTime());
        sceneExeRecordDto.setSceneExeTime(sceneExecuteRecord.getExecuteTime());
    }
}
