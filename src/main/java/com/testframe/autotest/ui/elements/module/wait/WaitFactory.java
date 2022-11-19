package com.testframe.autotest.ui.elements.module.wait;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.ui.elements.module.wait.base.WaitI;
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
@Slf4j
@Component
public class WaitFactory {

    private static Map<String, WaitI> waitFactory = new HashMap<>();

    private static Map<Integer, WaitI> waitTypeMap = new HashMap<>();

    @Autowired
    private List<WaitI> waitTypes;

    @PostConstruct
    public void init() {
//        for (WaitEnum waitEnum : WaitEnum.values()) {
//            try {
//                Class<WaitI> clazz = (Class<WaitI>) Class.forName(waitEnum.getWaitIdentity());
//                WaitI wait = clazz.newInstance();
//                waitTypeMap.put(waitEnum.getType(), wait);
//            } catch (Exception e) {
//                e.printStackTrace();
//                continue;
//            }
//        }
        waitTypes.forEach(e -> {
            waitFactory.put(e.waitIdentity(), e);
        });
        log.info("register all wait form, {}", JSON.toJSONString(waitFactory));
    }

    public WaitI getWait(String waitIdentity) {
        return waitFactory.get(waitIdentity);
    }

    public WaitI getWait(Integer type) {
        return waitTypeMap.get(type);
    }

}
