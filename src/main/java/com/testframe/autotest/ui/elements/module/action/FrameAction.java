package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.ActionExpection;
import com.testframe.autotest.ui.meta.OperateData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Description:
 * frame操作
 * @date:2022/10/27 22:41
 * @author: lyf
 */
public class FrameAction implements ActionI {

    /**
     * 切换回默认frame
     * @param driver
     * @param element
     * @param data
     */
    public static void switchDefaultFrame(WebDriver driver, WebElement element, OperateData data) {
        try {
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            throw new ActionExpection("切换到默认frame失败");
        }
    }

    /**
     * 根据id来切换frame
     * @param driver
     * @param element
     * @param data value需要是frame中的id才可使用
     */
    public static void switchFrameById(WebDriver driver, WebElement element, OperateData data) {
        try {
            if (data.getValue() != null) {
                driver.switchTo().frame(data.getValue());
                return;
            }
            if (data.getIndexes() != null) {
                driver.switchTo().frame(data.getIndexes().get(0));
            }
        } catch (Exception e) {
            throw new ActionExpection("切换frame失败");
        }
    }

    /**
     * 根据element来切换frame
     * @param driver
     * @param element
     * @param data 
     */
    public static void switchFrameByElement(WebDriver driver, WebElement element, OperateData data) {
        try {
                driver.switchTo().frame(element);
        } catch (Exception e) {
            throw new ActionExpection("切换frame失败");
        }
    }

}
