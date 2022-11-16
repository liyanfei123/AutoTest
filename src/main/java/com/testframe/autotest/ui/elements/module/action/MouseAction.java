package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.ActionExpection;
import com.testframe.autotest.ui.meta.OperateData;
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
    public static void click(WebDriver driver, WebElement element, OperateData data) {
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
    public static void doubleClick(WebDriver driver, WebElement element, OperateData data) {
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

    /**
     * 拖拽页面元素
     * @param driver
     * @param element
     * @param data
     */
    public static void dragElement(WebDriver driver, WebElement element, OperateData data) {
        try {
            if (element == null) {
                return;
            }
            Actions actions = new Actions(driver);
            actions.dragAndDropBy(element, data.getOffsetX(), data.getOffsetY()).build().perform();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("元素拖拽失败");
        }
    }


    /**
     * 鼠标左键
     * @param driver
     * @param element
     * @param data
     */
    public static void leftClick(WebDriver driver, WebElement element, OperateData data) {
        try {
            if (element == null) {
                return;
            }
            Actions actions = new Actions(driver);
            actions.contextClick(element).perform();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("元素右键失败");
        }
    }


    /**
     * 在元素上方悬浮
     * @param driver
     * @param element
     * @param data
     */
    public static void roverOnElement(WebDriver driver, WebElement element, OperateData data) {
        try {
            if (element == null) {
                return;
            }
            Actions actions = new Actions(driver);
            actions.moveToElement(element).perform();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("在元素上方悬浮失败");
        }
    }


}
