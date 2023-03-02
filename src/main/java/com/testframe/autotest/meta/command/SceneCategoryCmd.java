package com.testframe.autotest.meta.command;

import com.testframe.autotest.meta.dto.category.CategoryDto;
import com.testframe.autotest.meta.dto.category.CategorySceneDto;
import lombok.Data;

@Data
public class SceneCategoryCmd extends CategoryDto {

    // 场景与类目绑定
    private Long sceneId;

}
