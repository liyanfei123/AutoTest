package com.testframe.autotest.ui.meta;

import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import lombok.Data;

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

    public static SceneRunInfo build(SceneDetailDto sceneDetailDto, List<Long> runOrderList) {
        SceneRunInfo sceneRunInfo = new SceneRunInfo();
        sceneRunInfo.setSceneId(sceneDetailDto.getSceneId());
        sceneRunInfo.setUrl(sceneDetailDto.getUrl());
        sceneRunInfo.setRunOrderList(runOrderList);
        return sceneRunInfo;
    }
}
