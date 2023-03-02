package com.testframe.autotest.meta.dto.scene;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneDetailDto {

    private Long sceneId;

    private String sceneName;

    private String sceneDesc;

    private Integer type;

    private String url;

    private Integer waitType;

    private Integer waitTime;

    private Long createBy;

    private Integer categoryId;

    private Integer stepNum;

}
