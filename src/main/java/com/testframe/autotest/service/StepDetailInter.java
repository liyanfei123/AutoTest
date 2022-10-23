package com.testframe.autotest.service;

import com.testframe.autotest.command.StepCreateCmd;
import com.testframe.autotest.command.StepUpdateCmd;

import java.util.List;

public interface StepDetailInter {

    public Long saveStepDetail(StepUpdateCmd stepUpdateCmd);

    public List<Long> batchSaveStepDetail(List<StepUpdateCmd> stepUpdateCmds);
}
