package com.testframe.autotest.service;

import com.testframe.autotest.meta.command.StepUpdateCmd;
import com.testframe.autotest.meta.dto.StepInfoDto;

import java.util.HashMap;
import java.util.List;

public interface StepDetailService {

    public Long saveStepDetail(StepUpdateCmd stepUpdateCmd);

    public List<Long> batchSaveStepDetail(List<StepUpdateCmd> stepUpdateCmds);

    public HashMap<Long, StepInfoDto> batchQueryStepDetail(List<Long> stepIds);
}
