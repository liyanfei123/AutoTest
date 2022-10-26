package com.testframe.autotest.ui.module.action;

import com.testframe.autotest.core.exception.ActionExpection;
import com.testframe.autotest.ui.module.ExtraData;
import com.testframe.autotest.ui.module.action.ActionI;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Description:
 * 鼠标操作
 * @date:2022/10/26 21:43
 * @author: lyf
 */
public class MouseAction implements ActionI {

    /**
     * 单击元素
     * @param driver
     * @param element
     * @param data
     */
    public static void click(WebDriver driver, WebElement element, ExtraData data) {
        try {
            if (element == null) {
                return;
            }
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("元素单击失败");
        }
    }

    /**
     * 双击元素
     * @param driver
     * @param element
     * @param data
     */
    public static void doubleClick(WebDriver driver, WebElement element, ExtraData data) {
        try {
            if (element == null) {
                return;
            }
            Actions builder = new Actions(driver);
            builder.doubleClick(element).build().perform();
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("元素双击失败");
        }
    }






}
