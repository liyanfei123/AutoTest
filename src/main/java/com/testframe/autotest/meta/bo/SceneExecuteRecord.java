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

    private Long sceneId;

    private String sceneName;

    private String url;

    private Integer waitType;

    private Integer waitTime;

//    private String extInfo;

    // 根据步骤执行顺序来存放每个步骤的执行状态
    private List<StepExecuteRecord> steSpRecords;

}