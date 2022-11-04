package com.testframe.autotest.service;

import com.testframe.autotest.meta.command.StepUpdateCmd;

import java.util.List;

public interface StepDetailService {

    public Long saveStepDetail(StepUpdateCmd stepUpdateCmd);

    public List<Long> batchSaveStepDetail(List<StepUpdateCmd> stepUpdateCmds);
}
