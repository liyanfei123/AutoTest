package com.testframe.autotest.core.meta.Do;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StepDetailDo {

    public static final StepDetailDo NULL;

    static {
        NULL = new StepDetailDo();
    }

    private Long stepId;

    private String stepName;

    // 子场景id
    private Long sonSceneId;

    private String StepInfo;

}
