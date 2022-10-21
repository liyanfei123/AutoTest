package com.testframe.autotest.meta.bo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;


@Data
@Accessors(chain = true)
public class Scene  {

    private Long id;

    private Integer type;

    // 测试场景标题
    private String title;

    // 测试场景描述
    private String desc;

    private String url;

    private Integer waitType;

    // 场景全局等待时间
    private Integer waitTime;

    // 场景创建人
    private Long createBy;



}
