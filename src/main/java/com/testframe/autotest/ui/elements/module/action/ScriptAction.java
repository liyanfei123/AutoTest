package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.ActionExpection;
import com.testframe.autotest.ui.meta.OperateData;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Description:
 * Js执行
 * @date:2022/10/27 21:12
 * @author: lyf
 */
public class ScriptAction implements ActionI {

    public static void executeJs(WebDriver driver, WebElement element, OperateData data) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript(data.getJsExt());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("js文件操作失败");
        }
    }

}
