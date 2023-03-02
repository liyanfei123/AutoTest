package com.testframe.autotest.meta.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class StepUpdateCmd {

    // 步骤id，当新增步骤时为0
    private Long stepId;

    private Long sonSceneId = 0L;

    private String name;

    private String stepInfo;

    // 步骤执行状态 1:开启 2:关闭
    private Integer status;


}
