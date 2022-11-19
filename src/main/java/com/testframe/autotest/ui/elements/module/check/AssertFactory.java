package com.testframe.autotest.ui.elements.module.check;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.ui.elements.module.check.base.AssertI;
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
public class AssertFactory {

    private static Map<String, AssertI> assertFactory = new HashMap<>();

    @Autowired
    private List<AssertI> asserts;

    @PostConstruct
    public void init() {
        asserts.forEach(e -> {
            e.setMethods();
            assertFactory.put(e.assertTypeIdentity(), e);
        });
        log.info("register all assert mode, {}", JSON.toJSONString(assertFactory));
    }

    public AssertI getAssert(String assertTypeIdentity) {
        return assertFactory.get(assertTypeIdentity);
    }

    
}
