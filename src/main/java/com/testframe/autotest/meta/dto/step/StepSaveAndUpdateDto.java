package com.testframe.autotest.meta.dto.step;

import lombok.Data;

import java.util.List;

@Data
public class StepSaveAndUpdateDto {

    private Long sceneId;

    private List<StepDetailDto> saveStepDetailDtos;

    private List<StepDetailDto> updateStepDetailDtos;

    private List<Long> nowStepOrder;

}
