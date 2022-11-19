package com.testframe.autotest.ui.elements.module.action.base;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @date:2022/10/26 21:27
 * @author: lyf
 */
public class BaseAction {

    public Method[] methods;

    public void setMethods() {
        this.methods = this.getClass().getMethods();
    }

    public Method[] getMethods() {
        return this.methods;
    }

}
