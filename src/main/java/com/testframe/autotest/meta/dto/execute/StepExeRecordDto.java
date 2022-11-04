package com.testframe.autotest.meta.dto.execute;

import lombok.Data;

/**
 * Description:
 *
 * @date:2022/11/04 22:43
 * @author: lyf
 */
@Data
public class StepExeRecordDto {

    // 步骤名称
    private String stepName;

    //执行状态
    private Integer status;

    // 错误信息
    private Integer errInfo;

    // 执行时间
    private Long stepExeTime;

}