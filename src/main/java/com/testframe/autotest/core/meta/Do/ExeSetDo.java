package com.testframe.autotest.core.meta.Do;

import lombok.Data;

@Data
public class ExeSetDo {

    private Long setId;

    private String setName;

    // ref:OpenStatusEnum
    private Integer status;
}
