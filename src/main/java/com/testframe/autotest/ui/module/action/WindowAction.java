package com.testframe.autotest.ui.module.action;

import com.testframe.autotest.core.exception.ActionExpection;
import com.testframe.autotest.ui.meta.OperateData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Set;

/**
 * Description:
 * 浏览器窗口操作
 * @date:2022/10/27 22:28
 * @author: lyf
 */
public class WindowAction implements ActionI {

    private static String parentWindowHandle;

    public static void switchWindow(WebDriver driver, WebElement element, OperateData data) {
        try {
            parentWindowHandle = driver.getWindowHandle();
            Set<String> allWindowHandlers = driver.getWindowHandles();
            if (!allWindowHandlers.isEmpty()) {
                Integer index = 0;
                for (String windowHandle : allWindowHandlers) {
                    if (index == data.getIndexes().get(0)) {
                        driver.switchTo().window(windowHandle);
                        break;
                    }
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ActionExpection("切换窗口失败");
        }
    }
}
