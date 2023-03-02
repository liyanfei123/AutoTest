package com.testframe.autotest.meta.dto.step;

import com.testframe.autotest.core.meta.Do.StepDetailDo;
import lombok.Data;

import java.util.List;

@Data
public class StepsDto {

    private Long sceneId;

    // 每个步骤的信息
    private List<StepDetailDto> stepDetailDtos;

}
