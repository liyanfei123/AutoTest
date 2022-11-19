package com.testframe.autotest.ui.elements;

import com.testframe.autotest.ui.enums.LocatorTypeEnum;
import io.swagger.models.auth.In;
import org.openqa.selenium.By;

import static com.testframe.autotest.ui.enums.LocatorTypeEnum.ById;

/**
 * Description:
 *
 * @date:2022/11/19 17:18
 * @author: lyf
 */
public class ByFactory {

    public static By createBy(Integer locatorType, String locatorExpress) {
        By by = null;
        if (locatorType == null || locatorExpress == null) {
            return by;
        }
        switch (LocatorTypeEnum.getByType(locatorType)) {
            case ById:
                by = By.id(locatorExpress);
                break;
            case ByName:
                by = By.name(locatorExpress);
                break;
            case ByClassName:
                by = By.className(locatorExpress);
                break;
            case ByTagName:
                by = By.tagName(locatorExpress);
                break;
            case ByLinkText:
                by = By.linkText(locatorExpress);
                break;
            case ByPartialLinkText:
                by = By.partialLinkText(locatorExpress);
                break;
            case ByCssSelector:
                by = By.cssSelector(locatorExpress);
                break;
            case ByXpath:
                by = By.xpath(locatorExpress);
                break;
            case ByJQuery:
                by = null;
        }
        return by;
    }

}
