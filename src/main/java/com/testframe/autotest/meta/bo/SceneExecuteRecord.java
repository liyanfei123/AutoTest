package com.testframe.autotest.meta.bo;

import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/10/21 20:49
 * @author: lyf
 */
@Data
public class SceneExecuteRecord {

    private Long recordId;

    private Long sceneId;

    private String sceneName;

    private String url;

    private Integer waitType;

    private Integer waitTime;

    private Integer status;

    private Integer type;

    private Long executeTime;

    private String extInfo;

    // 根据步骤执行顺序来存放每个步骤的执行状态
    private List<Long> stepOrderList;

    public static SceneExecuteRecord build(Scene scene) {
        SceneExecuteRecord sceneExecuteRecord = new SceneExecuteRecord();
        sceneExecuteRecord.setRecordId(null);
        sceneExecuteRecord.setSceneId(scene.getId());
        sceneExecuteRecord.setSceneName(scene.getTitle());
        sceneExecuteRecord.setUrl(scene.getUrl());
        sceneExecuteRecord.setWaitType(scene.getWaitType());
        sceneExecuteRecord.setWaitTime(scene.getWaitTime());
        sceneExecuteRecord.setStatus(null);
        sceneExecuteRecord.setExecuteTime(null);
        sceneExecuteRecord.setExtInfo(null);
        return sceneExecuteRecord;
    }
}
