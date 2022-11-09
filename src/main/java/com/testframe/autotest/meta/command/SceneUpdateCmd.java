package com.testframe.autotest.meta.command;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Accessors(chain = true)
public class SceneUpdateCmd {

    @NotNull
    private Long id;

    // 测试类型 参考SceneTypeEnum
    private Integer type;

    // 测试场景标题
    @NotBlank(message = "测试场景名称不能为空")
    private String title;

    // 测试场景描述
    private String desc;

    @NotBlank(message = "测试地址")
    private String url;

    private Integer waitType;

    // 场景全局等待时间
    private Integer waitTime;

    // 场景步骤详细信息
    private List<StepUpdateCmd> stepUpdateCmds;


}
