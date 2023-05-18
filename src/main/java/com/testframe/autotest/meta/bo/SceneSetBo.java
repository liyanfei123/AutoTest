package com.testframe.autotest.meta.bo;

import com.testframe.autotest.meta.bo.SceneSetRelSceneBo;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneSetBo {

    private ExeSetDto sceneSetDto;

    private List<SceneSetRelSceneBo> sceneSetRelSceneBos;

    private List<SceneSetRelStepBo> sceneSetRelStepBos;

//    // 是否有下一页置顶或置后的场景/步骤
//    private Boolean hasNextSort;

}
