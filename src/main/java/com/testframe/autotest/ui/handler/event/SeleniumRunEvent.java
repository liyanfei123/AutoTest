package com.testframe.autotest.ui.handler.event;

import com.testframe.autotest.ui.meta.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/11/08 21:58
 * @author: lyf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeleniumRunEvent {

    // 浏览器类型
    private Integer browserType;

    private SceneRunInfo sceneRunInfo;

    // 场景执行记录信息
    private SceneRunRecordInfo sceneRunRecordInfo;

    // 全局等待方式
    private WaitInfo waitInfo;

    private List<StepExe> stepExes;

}
