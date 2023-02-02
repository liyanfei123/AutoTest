package com.testframe.autotest.core.meta.convertor;


import com.testframe.autotest.core.meta.po.StepDetail;
import com.testframe.autotest.meta.bo.Step;
import org.springframework.stereotype.Component;

@Component
public class StepDetailConvertor {

    public StepDetail toPo(Step step) {
        StepDetail stepDetail = new StepDetail();
        stepDetail.setId(step.getStepId());
        stepDetail.setStepName(step.getStepName());
        stepDetail.setSceneId(step.getSceneId());
        stepDetail.setStepInfo(step.getStepInfo());
        return stepDetail;
    }

    public Step PoToDo(StepDetail stepDetail) {
        if (stepDetail == null) {
            return null;
        }
        Step step = new Step();
        step.setStepId(stepDetail.getId());
        step.setStepName(stepDetail.getStepName());
        step.setSceneId(stepDetail.getSceneId());
        step.setStepInfo(stepDetail.getStepInfo());
        return step;
    }

}
