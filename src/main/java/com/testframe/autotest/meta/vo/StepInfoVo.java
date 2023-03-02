package com.testframe.autotest.meta.vo;

import com.testframe.autotest.meta.dto.step.StepDetailDto;
import lombok.Data;

import java.util.List;

@Data
public class StepInfoVo extends StepDetailDto {

    private List<StepInfoVo> sonSteps;

}
