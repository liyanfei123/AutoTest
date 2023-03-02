package com.testframe.autotest.meta.dto.step;

import lombok.Data;

import java.util.List;

@Data
public class StepCopyDto {

    private Long sceneId;

    private StepDetailDto stepDetailDto;

    private List<Long> stepOrderList;
}
