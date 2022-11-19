package com.testframe.autotest.ui.elements.module.action.base;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @date:2022/11/19 13:41
 * @author: lyf
 */
public interface ActionI {

    public String actionTypeIdentity();

    public void setMethods();

    public Method[] getMethods();

}
