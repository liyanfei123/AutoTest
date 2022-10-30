package com.testframe.autotest.meta.command;

import lombok.Data;
import lombok.experimental.Accessors;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
@Accessors(chain = true)
public class SceneCreateCmd {

    // 测试类型 参考SceneTypeEnum
    @Range(min=1, max=5, message="测试场景类型错误")
    private Integer type;

    // 测试场景标题
    @NotBlank(message = "测试场景名称不能为空")
    private String title;

    // 测试场景描述
    private String desc;


}
