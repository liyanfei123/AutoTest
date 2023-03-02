package com.testframe.autotest.meta.dto.scene;

import lombok.Data;

import java.util.List;

@Data
public class SceneSearchListDto {

    List<SceneDetailDto> sceneDetailDtos;

    private Long total;

    private int totalPage;

    private Long lastId;
}
