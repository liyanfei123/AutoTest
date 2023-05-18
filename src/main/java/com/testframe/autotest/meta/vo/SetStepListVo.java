package com.testframe.autotest.meta.vo;

import com.testframe.autotest.core.meta.vo.common.PageVO;
import com.testframe.autotest.meta.bo.SceneSetRelSceneBo;
import com.testframe.autotest.meta.dto.sceneSet.SceneSetRelStepDto;
import lombok.Data;

import java.util.List;

@Data
public class SetStepListVo {

    private List<SceneSetRelStepDto> steps;

    private PageVO pageVO;
}
