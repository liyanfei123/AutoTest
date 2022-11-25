package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.module.action.base.BaseAction;
import com.testframe.autotest.ui.elements.module.action.base.ActionI;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.meta.OperateData;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

/**
 * Description:
 * JavaScript的弹窗操作
 * @date:2022/10/27 22:35
 * @author: lyf
 */
@Slf4j
@Component
public class PopAction extends BaseAction implements ActionI {

    @Override
    public String actionTypeIdentity() {
        return OperateEnum.POP_OPERATE.getName();
    }

    /**
     * 确认Alert弹窗
     * @param driver
     * @param element
     * @param data
     */
    public static void handleAlert(WebDriver driver, WebElement element, OperateData data) {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            log.info("[PopAction:handleAlert] handle alert");
        } catch (Exception e) {
            throw new SeleniumRunException("Alert弹窗操作失败");
        }
    }

    /**
     * 操作confirm弹窗
     * @param driver
     * @param element
     * @param data 判定当前是确认还是取消
     */
    public static void handleConfirm(WebDriver driver, WebElement element, OperateData data) {
        try {
            Alert alert = driver.switchTo().alert();
            if (Boolean.parseBoolean(data.getValue())) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            log.info("[PopAction:handleAlert] handle confirm");
        } catch (Exception e) {
            throw new SeleniumRunException("confirm弹窗操作失败");
        }
    }

    /**
     * 操作prompt弹窗
     * 如果需要输入内容则输入内容，不需要输入内容则直接关闭
     * @param driver
     * @param element
     * @param data
     */
    public static void handlePrompt(WebDriver driver, WebElement element, OperateData data) {
        try {
            Alert alert = driver.switchTo().alert();
            if (data.getValue() != null) {
                element.sendKeys(data.getValue());
                alert.accept();
            } else {
                alert.dismiss();
            }
            log.info("[PopAction:handleAlert] handle prompt");
        } catch (Exception e) {
            throw new SeleniumRunException("Prompt弹窗操作失败");
        }
    }

}
