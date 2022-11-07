package com.testframe.autotest.meta.dto;

import com.testframe.autotest.meta.bo.Scene;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SceneInfoDto {

    private Long sceneId;

    // 场景名称
    private String name;

    // 场景描述
    private String desc;

    // 等待方式
    private Integer waitType;

    // 等待时间
    private Integer waitTime;

    public static SceneInfoDto build(Scene scene) {
        SceneInfoDto sceneInfoDto = new SceneInfoDto();
        BeanUtils.copyProperties(sceneInfoDto, scene);
//        sceneInfoDto.setSceneId(scene.getId());
//        sceneInfoDto.setName(scene.getTitle());
//        sceneInfoDto.setDesc(scene.getDesc());
//        sceneInfoDto.setWaitTime(sceneInfoDto.getWaitTime());
//        sceneInfoDto.setWaitType(sceneInfoDto.getWaitType());
        return sceneInfoDto;
    }

}
