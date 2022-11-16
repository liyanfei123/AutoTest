package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.ActionExpection;
import com.testframe.autotest.ui.meta.OperateData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Description:
 * 键盘操作
 * @date:2022/10/26 21:26
 * @author: lyf
 */
public class KeyBoardAction implements ActionI {

    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void ctrl(WebDriver driver, WebElement element, OperateData data) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public static void shift(WebDriver driver, WebElement element, OperateData data) {
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    public static void alt(WebDriver driver, WebElement element, OperateData data) {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    public static void enter(WebDriver driver, WebElement element, OperateData data) {
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    public static void ctrlCV(WebDriver driver, WebElement element, OperateData data) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    /**
     * 输入框输入
     * @param driver
     * @param element
     * @param data
     */
    public static void input(WebDriver driver, WebElement element,  OperateData data) {
        try {
            if (element == null) {
                return;
            }
            element.clear();
            element.sendKeys(data.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("输入失败");
        }
    }

}
