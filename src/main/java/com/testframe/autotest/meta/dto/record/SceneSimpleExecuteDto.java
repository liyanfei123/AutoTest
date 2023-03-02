package com.testframe.autotest.meta.dto.record;

import com.testframe.autotest.core.meta.Do.SceneExecuteRecordDo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class SceneSimpleExecuteDto {

    private Integer status;

    // 最近执行时间
    private Long executeTime;

    public static SceneSimpleExecuteDto DoToDto(SceneExecuteRecordDo sceneExecuteRecordDo) {
        SceneSimpleExecuteDto sceneExecuteDto = new SceneSimpleExecuteDto();
        sceneExecuteDto.setStatus(sceneExecuteRecordDo.getStatus());
        sceneExecuteDto.setExecuteTime(sceneExecuteRecordDo.getExecuteTime());
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
