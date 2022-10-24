package com.testframe.autotest.ui.meta;

import com.testframe.autotest.ui.enums.LocatorTypeEnum;
import com.testframe.autotest.ui.enums.WaitEnum;
import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Description:
 *
 * @date:2022/10/24 21:17
 * @author: lyf
 */
@Data
public class LocatorInfo {

    // 默认谷歌浏览器
    WebDriver driver = new ChromeDriver();

    LocatorTypeEnum locatedType;

    String expression;

    WaitEnum waitType;

    Integer waitTime;

}
