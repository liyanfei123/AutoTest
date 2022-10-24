package com.testframe.autotest.ui.elements.operate;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.AutoTestException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/10/24 21:07
 * @author: lyf
 */
@Component
@Slf4j
public class FindElement {

    @Autowired
    private WaitTypeFactory waitTypeFactory;

    private WebElement element;

    private List<WebElement> elements;

    // 目前只支持显式等待
    public WebElement findElementByType(LocatorInfo locatorInfo) {
        LocatorTypeEnum locatorType = locatorInfo.getLocatedType();
        String express = locatorInfo.getExpression();
        WaitEnum waitType = locatorInfo.getWaitType();
        WaitElementI waitFactory = waitTypeFactory.getWaitType(waitType.getWaitIdentity());
        try {
            switch (locatorType) {
                case ById:
                    element = waitFactory.wait(By.id(express));
                    break;
                case ByName:
                    element = waitFactory.wait(By.name(express));
                    break;
                case ByClassName:
                    element = waitFactory.wait(By.className(express));
                    break;
                case ByTagName:
                    element = waitFactory.wait(By.tagName(express));
                    break;
                case ByLinkText:
                    element = waitFactory.wait(By.linkText(express));
                    break;
                case ByPartialLinkText:
                    element = waitFactory.wait(By.partialLinkText(express));
                    break;
                case ByCssSelector:
                    element = waitFactory.wait(By.cssSelector(express));
                    break;
                case ByXpath:
                    element = waitFactory.wait(By.xpath(express));
                    break;
                case ByJQuery:
                    // todo:增加jquery验证
                    break;
                default:
                    throw new NoSuchElementException("Unexpected type: " + waitType);
            }
        } catch (NoSuchElementException e) {
            log.error("[FindElement:findElementByType] can not find element by {}, reason = {}, stack = {}",
                    JSON.toJSONString(locatorInfo), e.getMessage(), e.getStackTrace());
            throw new NoSuchElementException("控件未出现");
        }
        return element;
    }




}
