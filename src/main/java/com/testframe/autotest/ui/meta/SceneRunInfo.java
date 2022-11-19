package com.testframe.autotest.ui.meta;

import com.testframe.autotest.meta.dto.SceneInfoDto;
import lombok.Data;

/**
 * Description:
 *
 * @date:2022/11/19 14:46
 * @author: lyf
 */
@Data
public class SceneRunInfo {

    private Long sceneId;

    private String url;

    public static SceneRunInfo build(SceneInfoDto sceneInfoDto) {
        SceneRunInfo sceneRunInfo = new SceneRunInfo();
        sceneRunInfo.setSceneId(sceneInfoDto.getSceneId());
        sceneRunInfo.setUrl(sceneInfoDto.getUrl());
        return sceneRunInfo;
    }
}
