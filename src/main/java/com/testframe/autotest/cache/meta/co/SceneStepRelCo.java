package com.testframe.autotest.cache.meta.co;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SceneStepRelCo {

    private Long stepId;

    // 步骤状态
    private Integer status;

    // 1:单步骤,2:子场景
    private Integer type;

}
