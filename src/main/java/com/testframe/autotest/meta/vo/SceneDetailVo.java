package com.testframe.autotest.meta.vo;

import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import lombok.Data;

import java.util.List;

@Data
public class SceneDetailVo {

    private SceneDetailDto sceneInfo;

    // 按执行顺序编排好
    private List<StepInfoVo> steps;

}
