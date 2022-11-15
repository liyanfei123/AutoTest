package com.testframe.autotest.ui.elements.operate;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.ui.elements.wait.ExplicitWait;
import com.testframe.autotest.ui.elements.wait.ImplictWait;
import com.testframe.autotest.ui.elements.wait.WaitElementI;
import com.testframe.autotest.ui.elements.wait.WaitTypeFactory;
import com.testframe.autotest.ui.enums.LocatorTypeEnum;
import com.testframe.autotest.ui.enums.WaitEnum;
import com.testframe.autotest.ui.meta.LocatorInfo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * Description:
 *
 * @date:2022/10/24 21:07
 * @author: lyf
 */
// TODO: 2022/11/15 添加一个浏览器选择工厂，用于选择 
@Slf4j
public class ChromeFindElement {

    WebDriver driver = new ChromeDriver();

    @Autowired
    private WaitTypeFactory waitTypeFactory;

    private WaitElementI waitStyle;

    private WebElement element;

    private List<WebElement> elements;

    // 目前只支持显式等待
    public WebElement findElementsByType(LocatorInfo locatorInfo) {
        LocatorTypeEnum locatorType = LocatorTypeEnum.getByType(locatorInfo.getLocatedType());
        String express = locatorInfo.getExpression();
        WaitEnum waitType = WaitEnum.getByType(locatorInfo.getWaitType());
        if (waitType == WaitEnum.Explicit_Wait) {
            waitStyle = new ExplicitWait(driver, locatorInfo.getWaitTime());
        } else {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(locatorInfo.getWaitTime()));
            waitStyle = new ImplictWait(driver, locatorInfo.getWaitTime());
        }
        try {
            switch (locatorType) {
                case ById:
                    waitStyle.wait(By.id(express));
                    elements = driver.findElements(By.id(express));
                    break;
                case ByName:
                    waitStyle.wait(By.name(express));
                    elements = driver.findElements(By.name(express));
                    break;
                case ByClassName:
                    waitStyle.wait(By.className(express));
                    elements = driver.findElements(By.className(express));
                    break;
                case ByTagName:
                    waitStyle.wait(By.tagName(express));
                    elements = driver.findElements(By.tagName(express));
                    break;
                case ByLinkText:
                    waitStyle.wait(By.linkText(express));
                    elements = driver.findElements(By.linkText(express));
                    break;
                case ByPartialLinkText:
                    waitStyle.wait(By.partialLinkText(express));
                    elements = driver.findElements(By.partialLinkText(express));
                    break;
                case ByCssSelector:
                    waitStyle.wait(By.cssSelector(express));
                    elements = driver.findElements(By.cssSelector(express));
                    break;
                case ByXpath:
                    waitStyle.wait(By.xpath(express));
                    elements = driver.findElements(By.xpath(express));
                    break;
                case ByJQuery:
                    // todo:增加jquery验证
                    break;
                default:
                    throw new NoSuchElementException("Unexpected type: " + waitType);
            }
        } catch (NoSuchElementException e) {
            log.error("[FindElement:findElementByType] can not find element by {}, reason = {}, stack = {}",
                    JSON.toJSONString(locatorInfo), e.getMessage(), e);
            throw new NoSuchElementException("控件未出现");
        }
        return element;
    }




}
