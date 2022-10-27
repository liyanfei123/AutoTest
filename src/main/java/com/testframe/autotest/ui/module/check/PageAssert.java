package com.testframe.autotest.ui.module.check;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Description:
 *
 * @date:2022/10/27 21:17
 * @author: lyf
 */
public class PageAssert {

    /**
     * 页面标题验证
     * @param driver
     * @param element
     * @param checkData
     * @return
     */
    public static Boolean assertPageTitle(WebDriver driver, WebElement element, CheckData checkData) {
        try {
            String title = driver.getTitle();
            Assert.assertEquals(title, checkData.getExpectString());
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    public static Boolean assetPageSource(WebDriver driver, WebElement element, CheckData checkData) {
        String pageSource = driver.getPageSource();
        try {
            Assert.assertTrue(pageSource.contains(checkData.getExpectString()));
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

}
