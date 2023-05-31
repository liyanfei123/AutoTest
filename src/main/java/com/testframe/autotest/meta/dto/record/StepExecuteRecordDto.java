package com.testframe.autotest.meta.dto.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/10/21 20:52
 * @author: lyf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepExecuteRecordDto {

    private Long stepRecordId;

    private Long stepId;

    // 子场景的执行记录id
    private Long sceneRecordId;

    private String stepName;

    private String reason;

    private Integer status;

}
