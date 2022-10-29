package com.testframe.autotest.service;

import java.util.List;

public interface StepOrderInter {


    // 首先查询当前数据库中是否存在该数据，若存在的话，则是对数据进行更新，且判断是执行前的数据，还是执行时的顺序
    public void updateStepOrder(Long sceneId, List<Long> stepIds);

}
