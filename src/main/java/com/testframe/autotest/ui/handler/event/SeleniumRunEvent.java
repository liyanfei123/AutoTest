package com.testframe.autotest.ui.handler.event;

import com.testframe.autotest.ui.meta.LocatorInfo;
import com.testframe.autotest.ui.meta.StepExeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/11/08 21:58
 * @author: lyf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeleniumRunEvent {

    private Long sceneId;

    // 场景执行记录id
    // 当执行完成后，需要将该执行记录置为已完成
    private Long recordId;

    // 执行时间
    private Long exeTime;

    private List<StepExeInfo> stepExeInfos;

}
