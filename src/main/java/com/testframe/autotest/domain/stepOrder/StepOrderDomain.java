package com.testframe.autotest.domain.stepOrder;

import java.util.List;

public interface StepOrderDomain {

    public Boolean changeOrder(Long sceneId, List<Long> newStepOrder);

    List<Long> stepOrderList(Long sceneId, Integer type);
}
