package com.testframe.autotest.meta.dto.execute;


import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import lombok.Data;

@Data
public class SceneExeInfoDto {

    // 执行id，调试用
    private Long recordId;

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

    public static void build(SceneExeInfoDto sceneExeInfoDto, SceneExecuteRecord sceneExecuteRecord) {
        sceneExeInfoDto.setRecordId(sceneExecuteRecord.getRecordId());
        sceneExeInfoDto.setSceneName(sceneExecuteRecord.getSceneName());
        sceneExeInfoDto.setTestUrl(sceneExecuteRecord.getUrl());
        sceneExeInfoDto.setWaitType(sceneExecuteRecord.getWaitType());
        sceneExeInfoDto.setWaitTime(sceneExecuteRecord.getWaitTime());
        sceneExeInfoDto.setStatus(sceneExecuteRecord.getStatus());
        sceneExeInfoDto.setSceneExeTime(sceneExecuteRecord.getExecuteTime());
    }
}
