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
 * @date:2022/10/27 21:17
 * @author: lyf
 */
@Slf4j
@Component
public class PageAssert extends BaseAssert implements AssertI {

    @Override
    public String assertTypeIdentity() {
        return AssertEnum.PAGE_ASSERT.getName();
    }

    /**
     * 页面标题验证
     * @param driver
     * @param element
     * @param assertData
     * @return
     */
    public static Boolean assertPageTitle(WebDriver driver, WebElement element, AssertData assertData) {
        try {
            String title = driver.getTitle();
            Assert.assertEquals(title, assertData.getExpectString());
            log.info("[PageAssert:assertPageTitle] now value = {}, expected value = {}",
                    title, assertData.getExpectString());
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    /**
     * 源码关键字验证
     * @param driver
     * @param element
     * @param assertData
     * @return
     */
    public static Boolean assetPageSource(WebDriver driver, WebElement element, AssertData assertData) {
        String pageSource = driver.getPageSource();
        try {
            Assert.assertTrue(pageSource.contains(assertData.getExpectString()));
            log.info("[PageAssert:assetPageSource] expected value = {}", assertData.getExpectString());
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

}
