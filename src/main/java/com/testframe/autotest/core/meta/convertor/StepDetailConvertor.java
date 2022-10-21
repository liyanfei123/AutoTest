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
        stepDetail.setStepInfo(step.getStepInfo());
        return stepDetail;
    }

}
