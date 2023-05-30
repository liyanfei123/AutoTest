package com.testframe.autotest.meta.dto.category;


import lombok.Data;

// stepId、sceneId、setId三者不可同时存在
@Data
public class CategorySceneDto {

    private Integer categoryId;

    private Long stepId;

    private Long sceneId;

    private Long setId;
}
