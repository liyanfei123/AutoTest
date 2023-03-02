package com.testframe.autotest.meta.vo;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * 测试场景执行记录，最多20条记录
 * @date:2022/11/04 22:41
 * @author: lyf
 */
@Data
public class SceneRecordListVo {

    private Long sceneId;

    private List<SceneExeRecordVo> sceneExeRecordVos;
}
