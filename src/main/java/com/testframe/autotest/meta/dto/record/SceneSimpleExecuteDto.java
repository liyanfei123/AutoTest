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
        return sceneExecuteDto;
    }
}
