package com.testframe.autotest.meta.dto;

import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import com.testframe.autotest.meta.dto.execute.SceneExeRecordDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SceneSimpleInfoDto {

    private Long id;

    private String sceneName;

    // 步骤数
    private Integer stepNum;

    // 最近执行状态
    private Integer status;

    // 最近执行时间
    private Long executeTime;
}
