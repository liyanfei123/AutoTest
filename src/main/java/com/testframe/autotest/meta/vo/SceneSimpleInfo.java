package com.testframe.autotest.meta.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneSimpleInfo {

    private Long sceneId;

    private String sceneName;

    // 步骤数
    private Integer stepNum;

    // 最近执行状态
    private Integer status;

    // 最近执行时间
    private Long executeTime;
}
