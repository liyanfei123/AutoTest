package com.testframe.autotest.ui.meta;

import lombok.Data;

/**
 * Description:
 *
 * @date:2022/11/19 14:46
 * @author: lyf
 */
@Data
public class SceneRunRecordInfo {

    // 场景执行记录id
    private Long recordId;

    // 场景执行时间(初次保存时间creatTime)
    private Long sceneExecuteTime;

}
