package com.testframe.autotest.meta.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SceneStepRel {

    private Long id;

    private Long sceneId;

    private Long stepId;

    private Integer status;

    private Integer isDelete;

    public static SceneStepRel build(Long sceneId, Step step) {
        return SceneStepRel.builder().
                sceneId(sceneId).
                stepId(step.getStepId()).
                status(step.getStatus()).
                isDelete(0).build();
    }

}
