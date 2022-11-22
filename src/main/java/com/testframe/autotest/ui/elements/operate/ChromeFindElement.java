package com.testframe.autotest.ui.elements.operate;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.ByFactory;
import com.testframe.autotest.ui.elements.module.wait.*;
import com.testframe.autotest.ui.elements.module.wait.base.WaitI;
import com.testframe.autotest.ui.enums.LocatorTypeEnum;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import com.testframe.autotest.ui.meta.LocatorInfo;
import com.testframe.autotest.ui.meta.WaitInfo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/10/24 21:07
 * @author: lyf
 */
// TODO: 2022/11/15 添加一个浏览器选择工厂，用于选择 
@Slf4j
@Component
public class ChromeFindElement {

    WebDriver driver;

    public ChromeFindElement() {}

    public ChromeFindElement(WebDriver webDriver) {
        this.driver = webDriver;
    }

    public void setDriver(WebDriver webDriver) {
        this.driver = webDriver;
    }

    @Autowired
    private WaitFactory waitFactory;

    private WaitI waitStyle;

    private List<WebElement> elements;

    public void init(WaitInfo waitInfo) {
        try {
            // 全局的等待方式
            waitStyle = waitFactory.getWait(WaitModeEnum.getByType(waitInfo.getWaitMode()).getWaitIdentity());
            if (waitInfo.getWaitTime() != null) {
                waitStyle.setTime(waitInfo.getWaitTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SeleniumRunException("寻找元素初始化失败");
        }
    }

    public WebElement findElementByType(LocatorInfo locatorInfo) {
        String express = locatorInfo.getExpression();
        if (express == null || express.equals("")) {
            return null;
        }
        try {
            By by = ByFactory.createBy(locatorInfo.getLocatedType(), express);
            waitStyle.wait(By.linkText(express));
            elements = driver.findElements(by);
            if (elements.isEmpty()) {
                throw new SeleniumRunException(String.format("无当前控件, %s",express));
            }
        } catch (NoSuchElementException e) {
            log.error("[FindElement:findElementsByType] can not find element by {}, reason = {}, stack = {}",
                    JSON.toJSONString(locatorInfo), e.getMessage(), e);
            throw new SeleniumRunException(String.format("查找控件 %s 出现异常", express));
        }
        List<Integer> indexes = locatorInfo.getIndexes();
        if (indexes.isEmpty() || indexes == null) {
            return elements.get(0);
        }
        return elements.get(indexes.get(0) - 1);
    }

    public WebElement oldFindElementByType(LocatorInfo locatorInfo) {
        LocatorTypeEnum locatorType = LocatorTypeEnum.getByType(locatorInfo.getLocatedType());
        String express = locatorInfo.getExpression();
        if (express == null || express.equals("")) {
            return null;
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
                    throw new NoSuchElementException("Unexpected type: " + locatorType);
            }
            if (elements.isEmpty()) {
                throw new SeleniumRunException(String.format("无当前控件, %s",express));
            }
        } catch (NoSuchElementException e) {
            log.error("[FindElement:findElementsByType] can not find element by {}, reason = {}, stack = {}",
                    JSON.toJSONString(locatorInfo), e.getMessage(), e);
            throw new SeleniumRunException(String.format("查找控件 %s 出现异常", express));
        }
        List<Integer> indexes = locatorInfo.getIndexes();
        if (indexes.isEmpty() || indexes == null) {
            return elements.get(0);
        }
        return elements.get(indexes.get(0) - 1);
    }
}
