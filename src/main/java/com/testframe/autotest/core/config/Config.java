package com.testframe.autotest.core.config;

import lombok.Data;
import com.ctrip.framework.apollo.spring.annotation.ValueMapping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/10/30 20:49
 * @author: lyf
 */
@Data
@Component
public class Config {
    
    @ValueMapping("${bizType:[0]}")
    private List<Integer> bizTypes;

}
