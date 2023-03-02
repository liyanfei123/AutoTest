package com.testframe.autotest.core.meta.Do;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 *
 * @date:2022/10/21 20:52
 * @author: lyf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepExecuteRecordDo {

    //  主键id
    private Long stepRecordId;

    // 场景执行记录id
    private Long recordId;

    private Long stepId;

    // 子场景的执行记录id
    private Long sceneRecordId;

    private String stepName;

    private String reason;

    private Integer status;

}
