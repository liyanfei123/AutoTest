package com.testframe.autotest.core.meta.Do;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneDetailDo {

    private Long sceneId;

    private String sceneName;

    private String sceneDesc;

    private Integer type;

    private String url;

    private Integer waitType;

    private Integer waitTime;

    private Integer isDelete;

    private Long createBy;

}
