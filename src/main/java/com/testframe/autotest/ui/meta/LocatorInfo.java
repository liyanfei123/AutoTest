package com.testframe.autotest.ui.meta;

import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Description:
 * 定位方式
 * @date:2022/10/24 21:17
 * @author: lyf
 */
@Data
public class LocatorInfo {

    Integer locatedType;

    String expression;

    Integer waitType;

    Integer waitTime;

}
