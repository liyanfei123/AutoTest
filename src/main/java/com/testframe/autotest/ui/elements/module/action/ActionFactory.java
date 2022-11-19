package com.testframe.autotest.ui.elements.module.action;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.ui.elements.module.action.base.ActionI;
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
public class ActionFactory {

    private static Map<String, ActionI> actionFactory = new HashMap<>();

    @Autowired
    private List<ActionI> actions;

    @PostConstruct
    public void init() {
        actions.forEach(e -> {
            e.setMethods();
            actionFactory.put(e.actionTypeIdentity(), e);
        });
        log.info("register all action operate, {}", JSON.toJSONString(actions));
    }

    public ActionI getAction(String actionTypeIdentity) {
        return actionFactory.get(actionTypeIdentity);
    }


}
