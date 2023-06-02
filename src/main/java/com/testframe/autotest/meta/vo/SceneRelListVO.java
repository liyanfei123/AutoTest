package com.testframe.autotest.meta.vo;

import lombok.Data;

import java.util.List;

@Data
public class SceneRelListVO {

    private Long sceneId;

    private List<RelSceneVO> scenes;

    private List<RelSetVO> sets;

}
