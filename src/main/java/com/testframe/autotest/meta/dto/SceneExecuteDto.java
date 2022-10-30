package com.testframe.autotest.meta.dto;

import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.StepRunResultEnum;
import com.testframe.autotest.meta.bo.SceneExecuteRecord;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @date:2022/10/29 20:38
 * @author: lyf
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SceneExecuteDto {

    private Integer status;

    // 最近执行时间
    private Long executeTime;

    public static SceneExecuteDto toDto(SceneExecuteRecord sceneExecuteRecord) {
        SceneExecuteDto sceneExecuteDto = new SceneExecuteDto();
        sceneExecuteDto.setStatus(sceneExecuteRecord.getStatus());
        sceneExecuteDto.setExecuteTime(sceneExecuteDto.getExecuteTime());
//        List<Integer> stepStatus = sceneExecuteRecord.getStepRecords().stream().map(StepExecuteRecord::getStatus).collect(Collectors.toList());
//        if (stepStatus.isEmpty()) {
//            sceneExecuteDto.setStatus(SceneStatusEnum.NEVER.getType());
//        } else if (stepStatus.contains(StepRunResultEnum.FAIL.getType())) {
//            sceneExecuteDto.setStatus(SceneStatusEnum.FAIL.getType());
//        } else if (stepStatus.contains(StepRunResultEnum.RUN.getType())) {
//            sceneExecuteDto.setStatus(SceneStatusEnum.ING.getType());
//        } else {
//            sceneExecuteDto.setStatus(SceneStatusEnum.SUCCESS.getType());
//        }
        return sceneExecuteDto;
    }
}
