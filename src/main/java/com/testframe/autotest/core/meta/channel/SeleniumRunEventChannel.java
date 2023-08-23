package com.testframe.autotest.core.meta.channel;

import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.handler.base.Channel;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.sceneSet.ExeSetDto;
import com.testframe.autotest.meta.model.SceneSetConfigModel;
import com.testframe.autotest.ui.meta.*;
import lombok.Data;

import java.util.List;

/**
 *
 * 由验证器在验证的时候进行包装
 */
@Data
public class SeleniumRunEventChannel extends Channel {

    /**
    当前执行事件的类型
    1. 场景   2. 作为子场景执行  3. 执行集执行
    @see SceneExecuteEnum
     **/
    private int type;

    // 浏览器类型
    private Integer browserType;

    // 执行集信息
    private ExeSetDto exeSetDto;

    // 场景信息
    private SceneDetailDto sceneDetailDto;

    // 执行集执行记录
    private SetRunRecordInfo setRunRecordInfo;

    // 场景和执行集绑定时的执行相关配置参数
    SceneSetConfigModel sceneSetConfigModel;

    // 场景执行记录信息
    private SceneRunRecordInfo sceneRunRecordInfo;

    // 场景执行信息
    private SceneRunInfo sceneRunInfo;



    // 全局等待方式
    private WaitInfo waitInfo;

    private List<StepExe> stepExes;
}
