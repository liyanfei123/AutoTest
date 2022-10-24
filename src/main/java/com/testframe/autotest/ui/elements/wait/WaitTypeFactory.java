package com.testframe.autotest.ui.elements.wait;

import com.testframe.autotest.ui.elements.operate.WaitElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @date:2022/10/24 22:30
 * @author: lyf
 */
@Component
@Slf4j
public class WaitTypeFactory {

    private static Map<String, WaitElementI> waitTypeMap = new HashMap<>();

    @Autowired
    private List<WaitElementI> waitTypes;

    @PostConstruct
    public void init() {
        waitTypes.forEach(e -> {
            waitTypeMap.put(e.waitIdentity(), e);
        });
    }

    public WaitElementI getWaitType(String waitIdentity) {
        return waitTypeMap.get(waitIdentity);
    }

}
