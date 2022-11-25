package com.testframe.autotest.ui.elements.module.check;

import com.testframe.autotest.ui.elements.module.check.base.AssertI;
import com.testframe.autotest.ui.elements.module.check.base.BaseAssert;
import com.testframe.autotest.ui.enums.check.AssertEnum;
import com.testframe.autotest.ui.meta.AssertData;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import org.testng.Assert;

/**
 * Description:
 *
 * @date:2022/10/27 21:26
 * @author: lyf
 */
@Slf4j
@Component
public class ElementAssert extends BaseAssert implements AssertI {

    @Override
    public String assertTypeIdentity() {
        return AssertEnum.ELEMENT_ASSERT.getName();
    }

    /**
     * 检验元素文本是否复合预期
     * @param driver
     * @param element
     * @param checkData
     */
    public static Boolean checkElementText(WebDriver driver, WebElement element, AssertData checkData) {
        try {
            Assert.assertEquals(element.getText(), checkData.getExpectString());
            log.info("[ElementAssert:checkElementText] now value = {}, expected value = {}",
                    element.getText(), checkData.getExpectString());
            return true;
        } catch (AssertionError e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查元素属性
     * @param driver
     * @param element
     * @param assertData
     * @return
     */
    public static Boolean checkElementAttr(WebDriver driver, WebElement element, AssertData assertData) {
        try {
            Assert.assertEquals(element.getAttribute("attr"), assertData.getExpectString());
            log.info("[ElementAssert:checkElementAttr] now value = {}, expected value = {}",
                    element.getAttribute("attr"), assertData.getExpectString());
            return true;
        } catch (AssertionError e) {
            e.printStackTrace();
            return false;
        }
    }

}
