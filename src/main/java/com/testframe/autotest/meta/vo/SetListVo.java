package com.testframe.autotest.meta.vo;

import com.testframe.autotest.core.meta.vo.common.PageVO;
import com.testframe.autotest.meta.bo.SceneSetRelSceneBo;
import com.testframe.autotest.meta.bo.SceneSetRelStepBo;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import lombok.Data;

import java.util.List;

@Data
public class SetListVo {

    private List<ExeSetDto> sets;

    private PageVO pageVO;
}
