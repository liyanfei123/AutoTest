package com.testframe.autotest.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class AutoTestConfig {

    @Value("${copy.switch:True}")
    private Boolean copySwitch;

    // 是否校验登录开关
    @Value("${login.switch:True}")
    private Boolean loginSwitch;

    // 是否校验用户身份开关
    @Value("${user.switch:True}")
    private Boolean userSwitch;

    // 用户身份白名单
    @Value("${white.userIds:1,2,3}")
    private List<Long> whiteUserIds;

    // 场景执行记录展示的默认条数
    @Value("${record.size:20}")
    private Integer recordSize;
}
