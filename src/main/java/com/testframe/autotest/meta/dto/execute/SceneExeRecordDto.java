package com.testframe.autotest.meta.dto.execute;

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

    private Integer stepNum;

    private Integer status;

    private SceneExeInfoDto sceneExeInfoDto;

    private List<StepExeRecordInfo> stepExeInfos;

}
