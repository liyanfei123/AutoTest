package com.testframe.autotest.meta.vo;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * 场景执行时的单个记录
 * @date:2022/11/04 22:48
 * @author: lyf
 */
@Data
public class SceneExeRecordVo {

    private Long sceneId;

    private Integer stepNum;

    private Integer status;

    private SceneExeInfoVo sceneExeInfo;

    private List<StepExeRecordInfo> stepExeInfos;

}
