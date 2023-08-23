package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.module.action.base.BaseAction;
import com.testframe.autotest.ui.elements.module.action.base.ActionI;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.meta.OperateData;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.security.Key;

/**
 * Description:
 * 键盘操作
 * @date:2022/10/26 21:26
 * @author: lyf
 */
@Component
@Slf4j
public class KeyBoardAction extends BaseAction implements ActionI {

    @Override
    public String actionTypeIdentity() {
        return OperateEnum.KEYBOARD_OPERATE.getName();
    }

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
            log.info("[KeyBoardAction:input] start input value");
            if (element == null) {
                return;
            }
//            log.info("[KeyBoardAction:input] start input value 1");
            element.clear();
//            log.info("[KeyBoardAction:input] start input value 2");
            element.click();

            // 方式1：直接使用element自带的sendKeys方法，但是输入时执行会较慢
//            element.sendKeys(data.getValue());

            // 方式2：直接使用复制粘贴功能，速度较慢
            StringSelection stringSelection = new StringSelection(data.getValue());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            // TODO: 2022/11/25 区分mac和win的复制键
            // window用robot组件，mac使用Action
//            log.info("[KeyBoardAction:input] start input value 4");
            Actions action = new Actions(driver);
            action.keyDown(Keys.COMMAND).sendKeys("v").keyUp(Keys.COMMAND).build().perform();

            // 方式3：使用js输入，未调通
//            JavascriptExecutor jse = (JavascriptExecutor)driver;
//            jse.executeScript("arguments[0].innerText=arguments[1];", element, data.getValue());

            log.info("[KeyBoardAction:input] input value = {}", data.getValue());
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeleniumRunException("输入失败");
        }
    }

}
