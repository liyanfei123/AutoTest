package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.module.action.base.BaseAction;
import com.testframe.autotest.ui.elements.module.action.base.ActionI;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.meta.OperateData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Description:
 * 浏览器窗口操作
 * @date:2022/10/27 22:28
 * @author: lyf
 */
@Component
public class WindowAction extends BaseAction implements ActionI {

    @Override
    public String actionTypeIdentity() {
        return OperateEnum.WINDOW_OPERATE.getName();
    }

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
            throw new SeleniumRunException("切换窗口失败");
        }
    }
}
