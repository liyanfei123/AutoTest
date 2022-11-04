package com.testframe.autotest.service;

import java.util.List;

public interface StepOrderService {

    public void updateStepOrder(Long sceneId, List<Long> stepIds);

    public void removeStepId(Long sceneId, Long stepId);

}
