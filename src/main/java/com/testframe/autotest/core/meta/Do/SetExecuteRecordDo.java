package com.testframe.autotest.core.meta.Do;

import lombok.Data;

@Data
public class SetExecuteRecordDo extends BaseDo {

    private Long setRecordId;

    private Long setId;

    private String setName;

    private Integer status;

    private Long executeTime;
}
