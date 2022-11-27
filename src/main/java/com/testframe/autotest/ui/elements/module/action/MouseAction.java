package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.module.action.base.BaseAction;
import com.testframe.autotest.ui.elements.module.action.base.ActionI;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.meta.OperateData;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

/**
 * Description:
 * 鼠标操作
 * @date:2022/10/26 21:43
 * @author: lyf
 */
@Slf4j
@Component
public class MouseAction extends BaseAction implements ActionI {

    @Override
    public String actionTypeIdentity() {
        return OperateEnum.MOUSE_OPERATE.getName();
    }

    /**
     * 单击元素
     * 默认是左击
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
            log.info("[MouseAction:click] click element = {}", element);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeleniumRunException("元素单击失败");
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
            log.info("[MouseAction:doubleClick] double click element = {}", element);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeleniumRunException("元素双击失败");
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
            log.info("[MouseAction:doubleClick] drag element = {}", element);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeleniumRunException("元素拖拽失败");
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
            log.info("[MouseAction:doubleClick] left click element = {}", element);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeleniumRunException("元素右键失败");
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
            log.info("[MouseAction:doubleClick] pop click element = {}", element);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeleniumRunException("在元素上方悬浮失败");
        }
    }

}
