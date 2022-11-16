package com.testframe.autotest.ui.elements.module.check;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Description:
 *
 * @date:2022/10/27 21:26
 * @author: lyf
 */
public class ElementAssert implements AssertI {

    /**
     * 检验元素文本是否复合预期
     * @param driver
     * @param element
     * @param checkData
     */
    public static Boolean checkElementText(WebDriver driver, WebElement element, CheckData checkData) {
        try {
            Assert.assertEquals(element.getText(), checkData.getExpectString());
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    /**
     * 检查元素属性
     * @param driver
     * @param element
     * @param checkData
     * @return
     */
    public static Boolean checkElementAttr(WebDriver driver, WebElement element, CheckData checkData) {
        try {
            Assert.assertEquals(element.getAttribute("attr"), checkData.getExpectString());
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

}
