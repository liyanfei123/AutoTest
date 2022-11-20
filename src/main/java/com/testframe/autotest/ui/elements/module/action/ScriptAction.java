package com.testframe.autotest.ui.elements.module.action;

import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.module.action.base.BaseAction;
import com.testframe.autotest.ui.elements.module.action.base.ActionI;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.meta.OperateData;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Js执行
 * @date:2022/10/27 21:12
 * @author: lyf
 */
@Component
public class ScriptAction extends BaseAction implements ActionI {

    @Override
    public String actionTypeIdentity() {
        return OperateEnum.SCRIPT_OPERATE.getName();
    }

    public static void executeJs(WebDriver driver, WebElement element, OperateData data) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript(data.getJsExt());
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeleniumRunException("js文件操作失败");
        }
    }

}
