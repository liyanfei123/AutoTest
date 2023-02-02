package com.testframe.autotest.service;

import com.testframe.autotest.meta.bo.StepExecuteRecord;
import com.testframe.autotest.ui.meta.StepExe;

import java.util.List;

public interface StepRecordService {

    Long saveRecord(Long recordId, StepExe stepExe, Integer status, String reason);

    boolean batchSaveRecord(Long recordId, List<StepExe> stepExeList, Integer status, String reason);

    boolean batchSaveRecord(List<StepExecuteRecord> stepExecuteRecords);
}
