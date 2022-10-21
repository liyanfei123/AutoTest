package com.testframe.autotest.meta.bo;


import lombok.Data;

@Data
public class SceneStepOrder {

    private Long id;

    private Long sceneId;

    private String orderList;

    // 调用接口保存时判断type类型进行赋值
    private Integer type;

}
