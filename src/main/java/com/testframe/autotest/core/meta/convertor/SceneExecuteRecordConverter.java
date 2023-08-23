package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.Do.SceneExecuteRecordDo;
import com.testframe.autotest.core.meta.po.SceneRecord;
import com.testframe.autotest.meta.dto.record.SceneExecuteRecordDto;
import com.testframe.autotest.meta.dto.scene.SceneDetailDto;
import org.springframework.stereotype.Component;


import static com.testframe.autotest.util.StringUtils.orderToList;

/**
 * Description:
 *
 * @date:2022/10/21 20:57
 * @author: lyf
 */
@Component
public class SceneExecuteRecordConverter {

    public SceneRecord DoToPo(SceneExecuteRecordDo sceneExecuteRecordDo) {
        SceneRecord sceneRecord = new SceneRecord();
        sceneRecord.setId(sceneExecuteRecordDo.getRecordId());
        sceneRecord.setSetRecordId(sceneExecuteRecordDo.getSetRecordId());
        sceneRecord.setSceneId(sceneExecuteRecordDo.getSceneId());
        sceneRecord.setSceneName(sceneExecuteRecordDo.getSceneName());
        sceneRecord.setUrl(sceneExecuteRecordDo.getUrl());
        sceneRecord.setWaitType(sceneExecuteRecordDo.getWaitType());
        sceneRecord.setWaitTime(sceneExecuteRecordDo.getWaitTime());
        sceneRecord.setStatus(sceneExecuteRecordDo.getStatus());
        sceneRecord.setType(sceneExecuteRecordDo.getType());
        sceneRecord.setExtInfo(sceneExecuteRecordDo.getExtInfo());
        sceneRecord.setOrderList(sceneExecuteRecordDo.getStepOrderList().toString());
        return sceneRecord;
    }

    public SceneExecuteRecordDo PoToDo(SceneRecord sceneRecord) {
        SceneExecuteRecordDo sceneExecuteRecord = new SceneExecuteRecordDo();
        sceneExecuteRecord.setRecordId(sceneRecord.getId());
        sceneExecuteRecord.setSetRecordId(sceneRecord.getSetRecordId());
        sceneExecuteRecord.setSceneId(sceneRecord.getSceneId());
        sceneExecuteRecord.setSceneName(sceneRecord.getSceneName());
        sceneExecuteRecord.setUrl(sceneRecord.getUrl());
        sceneExecuteRecord.setWaitType(sceneRecord.getWaitType());
        sceneExecuteRecord.setWaitTime(sceneRecord.getWaitTime());
        sceneExecuteRecord.setStatus(sceneRecord.getStatus());
        sceneExecuteRecord.setType(sceneRecord.getType());
        sceneExecuteRecord.setExecuteTime(sceneRecord.getCreateTime());
        sceneExecuteRecord.setStepOrderList(orderToList(sceneRecord.getOrderList()));
        return sceneExecuteRecord;
    }

    public SceneExecuteRecordDo DtoToDo(SceneExecuteRecordDto sceneExecuteRecordDto) {
        SceneExecuteRecordDo sceneExecuteRecordDo = new SceneExecuteRecordDo();
        sceneExecuteRecordDo.setRecordId(sceneExecuteRecordDto.getRecordId());
        sceneExecuteRecordDo.setSetRecordId(sceneExecuteRecordDto.getSetRecordId());
        sceneExecuteRecordDo.setSceneId(sceneExecuteRecordDto.getSceneId());
        sceneExecuteRecordDo.setSceneName(sceneExecuteRecordDto.getSceneName());
        sceneExecuteRecordDo.setUrl(sceneExecuteRecordDto.getUrl());
        sceneExecuteRecordDo.setWaitType(sceneExecuteRecordDto.getWaitType());
        sceneExecuteRecordDo.setWaitTime(sceneExecuteRecordDto.getWaitTime());
        sceneExecuteRecordDo.setStatus(sceneExecuteRecordDto.getStatus());
        sceneExecuteRecordDo.setType(sceneExecuteRecordDto.getType());
        sceneExecuteRecordDo.setExecuteTime(null);
        sceneExecuteRecordDo.setStepOrderList(sceneExecuteRecordDto.getStepOrderList());
        return sceneExecuteRecordDo;
    }

    public SceneExecuteRecordDto DoToDto(SceneExecuteRecordDo sceneExecuteRecordDo) {
        SceneExecuteRecordDto sceneExecuteRecordDto = new SceneExecuteRecordDto();
        sceneExecuteRecordDto.setRecordId(sceneExecuteRecordDo.getRecordId());
        sceneExecuteRecordDto.setSetRecordId(sceneExecuteRecordDo.getSetRecordId());
        sceneExecuteRecordDto.setSceneId(sceneExecuteRecordDo.getSceneId());
        sceneExecuteRecordDto.setSceneName(sceneExecuteRecordDo.getSceneName());
        sceneExecuteRecordDto.setUrl(sceneExecuteRecordDo.getUrl());
        sceneExecuteRecordDto.setWaitType(sceneExecuteRecordDo.getWaitType());
        sceneExecuteRecordDto.setWaitTime(sceneExecuteRecordDo.getWaitTime());
        sceneExecuteRecordDto.setStatus(sceneExecuteRecordDo.getStatus());
        sceneExecuteRecordDto.setType(sceneExecuteRecordDo.getType());
        sceneExecuteRecordDto.setExecuteTime(sceneExecuteRecordDo.getExecuteTime());
        sceneExecuteRecordDto.setStepOrderList(sceneExecuteRecordDo.getStepOrderList());
        return sceneExecuteRecordDto;
    }

    public SceneExecuteRecordDto buildSceneExecuteRecord(SceneDetailDto sceneDetailDto) {
        SceneExecuteRecordDto sceneExecuteRecordDto = new SceneExecuteRecordDto();
        sceneExecuteRecordDto.setRecordId(null);
        sceneExecuteRecordDto.setSetRecordId(null); // 记得在后续进行添加
        sceneExecuteRecordDto.setSceneId(sceneDetailDto.getSceneId());
        sceneExecuteRecordDto.setSceneName(sceneDetailDto.getSceneName());
        sceneExecuteRecordDto.setUrl(sceneDetailDto.getUrl());
        sceneExecuteRecordDto.setWaitType(sceneDetailDto.getWaitType());
        sceneExecuteRecordDto.setWaitTime(sceneDetailDto.getWaitTime());
        sceneExecuteRecordDto.setStatus(null);
        sceneExecuteRecordDto.setType(null);
        Long currentTime = System.currentTimeMillis();
        sceneExecuteRecordDto.setExecuteTime(currentTime);
        sceneExecuteRecordDto.setExtInfo(null);
        sceneExecuteRecordDto.setStepOrderList(null);
        return sceneExecuteRecordDto;
    }

}
