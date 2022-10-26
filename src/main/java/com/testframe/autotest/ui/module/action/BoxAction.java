package com.testframe.autotest.ui.module.action;

import com.testframe.autotest.core.exception.ActionExpection;
import com.testframe.autotest.ui.module.ExtraData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description:
 * 单选框/复选框操作
 * @date:2022/10/26 22:05
 * @author: lyf
 */
public class BoxAction implements ActionI {


    /**
     * 选中单选框
     * @param driver
     * @param element
     * @param data
     */
    public static void beSelected(WebDriver driver, WebElement element, ExtraData data) {
        try {
            if (element == null || element.isSelected()) {
                return;
            }
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("单选框选中失败");
        }
    }

    /**
     * 取消选中单选框
     * @param driver
     * @param element
     * @param data
     */
    public static void notBeSelected(WebDriver driver, WebElement element, ExtraData data) {
        try {
            if (element == null || !element.isSelected()) {
                return;
            }
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("取消单选框选中失败");
        }
    }

}
