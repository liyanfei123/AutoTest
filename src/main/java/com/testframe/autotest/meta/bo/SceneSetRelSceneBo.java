package com.testframe.autotest.meta.bo;

import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelSceneDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneSetRelSceneBo extends SceneSetRelSceneDto {

    private Long relId;

    // 场景名称
    private String sceneName;

    // 场景中的步骤数
    private Integer stepNum;
}
