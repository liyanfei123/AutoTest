package com.testframe.autotest.ui.module.action;

import com.testframe.autotest.core.exception.ActionExpection;
import com.testframe.autotest.ui.meta.OperateData;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Description:
 * JavaScript的弹窗操作
 * @date:2022/10/27 22:35
 * @author: lyf
 */
public class JsPopAction implements ActionI {


    public static void handleAlert(WebDriver driver, WebElement element, OperateData data) {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (Exception e) {
            throw new ActionExpection("Alert弹窗操作失败");
        }
    }

    public static void handleConfirm(WebDriver driver, WebElement element, OperateData data) {
        try {
            Alert alert = driver.switchTo().alert();
            if (Boolean.parseBoolean(data.getValue())) {
                alert.accept();
            } else {
                alert.dismiss();
            }
        } catch (Exception e) {
            throw new ActionExpection("confirm弹窗操作失败");
        }
    }

    public static void handlePrompt(WebDriver driver, WebElement element, OperateData data) {
        try {
            Alert alert = driver.switchTo().alert();
            if (data.getValue() != null) {
                element.sendKeys(data.getValue());
                alert.accept();
            } else {
                alert.dismiss();
            }
        } catch (Exception e) {
            throw new ActionExpection("Prompt弹窗操作失败");
        }
    }

}
