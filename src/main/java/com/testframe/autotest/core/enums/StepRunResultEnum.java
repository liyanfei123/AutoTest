package com.testframe.autotest.core.enums;

import com.testframe.autotest.meta.dto.record.StepExecuteRecordDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 步骤运行结果
 */
public enum StepRunResultEnum {

    SUCCESS(1, "成功"),
    FAIL(2, "失败"),
    RUN(3, "执行中"),
    STOP(4, "暂停"),
    CLOSE(5, "终止"), // 因为失败导致未执行
    NORUN(6, "未执行"); // 初始状态

    private int type;

    private String name;

    StepRunResultEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static Integer stepStatusToSceneStatus(List<StepExecuteRecordDto> stepExecuteRecords) {
        List<Integer> status = stepExecuteRecords.stream().map(StepExecuteRecordDto::getStatus)
                .collect(Collectors.toList());
        if (status.isEmpty()) {
            return SceneStatusEnum.NEVER.getType();
        } else if (status.contains(StepRunResultEnum.RUN.getType())) {
            return SceneStatusEnum.ING.getType();
        } else if (status.contains(StepRunResultEnum.FAIL.getType())) {
            return SceneStatusEnum.FAIL.getType();
        }
        return SceneStatusEnum.SUCCESS.getType();
    }

}
