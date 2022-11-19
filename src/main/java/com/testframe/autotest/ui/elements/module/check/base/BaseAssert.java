package com.testframe.autotest.ui.elements.module.check.base;

import java.lang.reflect.Method;

/**
 * Description:
 *
 * @date:2022/11/19 16:03
 * @author: lyf
 */
public class BaseAssert {

    public Method[] methods;

    public void setMethods() {
        this.methods = this.getClass().getMethods();
    }

    public Method[] getMethods() {
        return this.methods;
    }

}
