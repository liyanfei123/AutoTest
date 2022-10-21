package com.testframe.autotest.core.meta.convertor;

import com.testframe.autotest.core.meta.po.SceneRecord;
import com.testframe.autotest.core.meta.po.SceneStep;
import com.testframe.autotest.meta.bo.Scene;
import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @date:2022/10/21 20:57
 * @author: lyf
 */
@Component
public class SceneExecuteRecordConverter {

    public SceneRecord toPo(SceneExecuteRecord sceneExecuteRecord) {
        SceneRecord sceneRecord = new SceneRecord();
        sceneRecord.setSceneId(sceneExecuteRecord.getSceneId());
        sceneRecord.setUrl(sceneExecuteRecord.getUrl());
        sceneRecord.setWaitType(sceneExecuteRecord.getWaitType());
        sceneRecord.setWaitTime(sceneExecuteRecord.getWaitTime());
        sceneRecord.setExtInfo(null);
        List<Long> stepIds = new ArrayList<>();
        for (StepExecuteRecord stepExecuteRecord : sceneExecuteRecord.getSteSpRecords()) {
            stepIds.add(stepExecuteRecord.getStepId());
        }
        String stepOrders = stepIds.toString();
        sceneRecord.setOrderList(stepOrders.substring(1, stepOrders.length()-1));
        return sceneRecord;
    }

    public SceneExecuteRecord toSceneExecuteRecord(SceneRecord sceneRecord) {
        SceneExecuteRecord sceneExecuteRecord = new SceneExecuteRecord();
        sceneExecuteRecord.setSceneId(sceneExecuteRecord.getSceneId());
        sceneExecuteRecord.setSceneName(sceneExecuteRecord.getSceneName());
        sceneExecuteRecord.setUrl(sceneExecuteRecord.getUrl());
        sceneExecuteRecord.setWaitType(sceneExecuteRecord.getWaitType());
        sceneExecuteRecord.setWaitTime(sceneExecuteRecord.getWaitTime());
        return sceneExecuteRecord;
    }

    public static void main(String[] args) {
        List<Integer> stepIds = new ArrayList<>();
        stepIds.add(1);
        stepIds.add(2);
        String t = stepIds.toString();
        String tm = t.substring(1, t.length()-1);
        String[] ts = tm.split(",");
        System.out.println(t.substring(1, t.length()-1));

    }

}
