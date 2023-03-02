package com.testframe.autotest.core.meta.convertor;


import com.testframe.autotest.core.meta.Do.StepDetailDo;
import com.testframe.autotest.core.meta.po.StepDetail;
import org.springframework.stereotype.Component;

@Component
public class StepDetailConvertor {

    public StepDetail DoToPo(StepDetailDo stepDetailDo) {
        StepDetail stepDetail = new StepDetail();
        stepDetail.setId(stepDetailDo.getStepId());
        stepDetail.setStepName(stepDetailDo.getStepName());
        stepDetail.setSceneId(stepDetailDo.getSonSceneId());
        stepDetail.setStepInfo(stepDetailDo.getStepInfo());
        return stepDetail;
    }

    public StepDetailDo PoToDo(StepDetail stepDetail) {
        StepDetailDo stepDetailDo = new StepDetailDo();
        stepDetailDo.setStepId(stepDetail.getId());
        stepDetailDo.setStepName(stepDetail.getStepName());
        stepDetailDo.setSonSceneId(stepDetail.getSceneId());
        stepDetailDo.setStepInfo(stepDetail.getStepInfo());
        return stepDetailDo;
    }

}
