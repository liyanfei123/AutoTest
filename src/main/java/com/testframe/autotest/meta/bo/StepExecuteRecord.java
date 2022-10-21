package com.testframe.autotest.meta.bo;

import lombok.Data;

/**
 * Description:
 *
 * @date:2022/10/21 20:52
 * @author: lyf
 */
@Data
public class StepExecuteRecord {

    private Long recordId;

    private Long stepId;

    private String reason;

    private Integer status;

}
