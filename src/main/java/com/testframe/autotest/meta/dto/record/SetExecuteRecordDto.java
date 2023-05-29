package com.testframe.autotest.meta.dto.record;

import com.testframe.autotest.meta.dto.BaseDto;
import lombok.Data;

@Data
public class SetExecuteRecordDto extends BaseDto {

    private Long setRecordId;

    private Long setId;

    private String setName;

    private Integer status;

    private Long executeTime;

}
