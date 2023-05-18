package com.testframe.autotest.meta.vo;

import com.testframe.autotest.core.meta.vo.common.PageVO;
import com.testframe.autotest.meta.bo.SceneSetRelSceneBo;
import com.testframe.autotest.meta.bo.SceneSetRelStepBo;
import lombok.Data;

import java.util.List;

@Data
public class SetRelListVo {

    private List<SceneSetRelSceneBo> scenes;

    private List<SceneSetRelStepBo> steps;

    private PageVO pageVO;
}
