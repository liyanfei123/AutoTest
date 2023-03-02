package com.testframe.autotest.meta.dto.scene;


import com.testframe.autotest.meta.dto.step.StepDetailDto;
import lombok.Data;

import java.util.List;

@Data
public class SceneInfoDto extends SceneDetailDto {

    private List<StepDetailDto> stepDetailDtos;

}
