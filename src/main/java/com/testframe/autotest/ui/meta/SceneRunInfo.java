package com.testframe.autotest.ui.meta;

import com.testframe.autotest.meta.dto.SceneInfoDto;
import lombok.Data;

import java.util.Collections;
import java.util.List;

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

    // 步骤执行时的顺序，用于保证落库顺序
    private List<Long> runOrderList;

    public static SceneRunInfo build(SceneInfoDto sceneInfoDto, List<Long> runOrderList) {
        SceneRunInfo sceneRunInfo = new SceneRunInfo();
        sceneRunInfo.setSceneId(sceneInfoDto.getSceneId());
        sceneRunInfo.setUrl(sceneInfoDto.getUrl());
        sceneRunInfo.setRunOrderList(runOrderList);
        return sceneRunInfo;
    }
}
