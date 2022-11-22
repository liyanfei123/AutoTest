package com.testframe.autotest.service;

import com.testframe.autotest.ui.meta.StepExeInfo;

import java.util.List;

public interface StepRecordService {

    Long saveRecord(Long recordId, StepExeInfo stepExeInfo, Integer status, String reason);
}
