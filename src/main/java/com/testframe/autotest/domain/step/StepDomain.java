package com.testframe.autotest.domain.step;

import com.testframe.autotest.core.meta.Do.SceneStepRelDo;
import com.testframe.autotest.meta.bo.SceneStepRel;
import com.testframe.autotest.meta.dto.category.CategoryDto;
import com.testframe.autotest.meta.dto.step.StepCopyDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import com.testframe.autotest.meta.dto.step.StepSaveAndUpdateDto;
import com.testframe.autotest.meta.dto.step.StepsDto;

import java.util.List;

public interface StepDomain {

    public List<StepDetailDto> listStepInfo(Long sceneId);

    public Boolean updateSteps(StepsDto stepsDto);

    // 批量保存步骤
    // 保存的步骤都只可添加到最后
    public List<Long> saveSteps(StepsDto stepsDto);

    Boolean deleteSteps(Long sceneId, List<Long> stepIds);

    public Long copyStep(Long sceneId, Long stepId, List<Long> stepOrderList);

    public Boolean updateAndSaveSteps(StepSaveAndUpdateDto stepSaveAndUpdateDto);

    public Boolean needUpdate(Long stepId, StepDetailDto stepDetailDto);


}
