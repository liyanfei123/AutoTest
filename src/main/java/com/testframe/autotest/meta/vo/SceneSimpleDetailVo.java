package com.testframe.autotest.meta.vo;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class SceneSimpleDetailVo {

    private Long sceneId;

    private String sceneName;

    // 步骤数
    private Integer stepNum;

    // 最近执行状态
    private Integer status;

    // 最近执行时间
    private Long executeTime;

    private Integer type;

}
