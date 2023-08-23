package com.testframe.autotest.core.meta.channel;

import com.testframe.autotest.core.enums.SceneExecuteEnum;
import com.testframe.autotest.handler.base.Channel;
import com.testframe.autotest.meta.bo.SceneSetBo;
import com.testframe.autotest.meta.command.ExecuteCmd;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.record.SetExecuteRecordDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import com.testframe.autotest.meta.dto.step.StepDetailDto;
import com.testframe.autotest.meta.model.SceneSetConfigModel;
import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class ExecuteChannel extends Channel {

    /**
     当前执行事件的类型
     1. 场景   2. 作为子场景执行  3. 执行集执行
     @see SceneExecuteEnum
     **/
    private int type;

    // 浏览器类型
    private Integer browserType;

    private ExecuteCmd executeCmd;

    // 执行集下的可执行场景，以及单独执行的场景
    private List<SceneDetailDto> sceneDetailDtos;

    // sceneId -> List<StepDetailDto>
    private HashMap<Long, List<StepDetailDto>> stepDetailDtoHashMap;

    private HashMap<Long, SceneExecuteRecordDto> sceneExecuteRecordDtoHashMap;

    private SetExecuteRecordDto setExecuteRecordDto;

    // 执行集下的每个场景被添加的额外配置
    // sceneId -> SceneSetConfigModel
    private HashMap<Long, SceneSetConfigModel> sceneSetConfigModelHashMap;

    // 执行事件
    private List<SeleniumRunEvent> seleniumRunEvents;

}
