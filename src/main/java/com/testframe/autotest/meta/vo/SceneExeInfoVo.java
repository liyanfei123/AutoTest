package com.testframe.autotest.meta.vo;

import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import lombok.Data;

@Data
public class SceneExeInfoVo {

    // 执行id，调试用
//    private Long recordId;

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

    public static SceneExeInfoVo build(SceneExecuteRecordDto sceneExecuteRecordDto) {
        SceneExeInfoVo sceneExeInfoVo = new SceneExeInfoVo();
//        sceneExeInfoVo.setRecordId(sceneExecuteRecordDto.getRecordId());
        sceneExeInfoVo.setSceneName(sceneExecuteRecordDto.getSceneName());
        sceneExeInfoVo.setTestUrl(sceneExecuteRecordDto.getUrl());
        sceneExeInfoVo.setWaitType(sceneExecuteRecordDto.getWaitType());
        sceneExeInfoVo.setWaitTime(sceneExecuteRecordDto.getWaitTime());
        sceneExeInfoVo.setStatus(sceneExecuteRecordDto.getStatus());
        sceneExeInfoVo.setSceneExeTime(sceneExecuteRecordDto.getExecuteTime());
        return sceneExeInfoVo;
    }
}
