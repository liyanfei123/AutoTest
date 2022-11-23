package com.testframe.autotest.ui.elements.module.wait.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public interface WaitI {

   public String waitIdentity();

   void wait(By by);

   void wait(By by, Integer time);

   void setWebDriver(WebDriver webDriver);

   void setTime(Integer time);

   void setIdentity(String identity);

   void setWebDriverWait(WebDriver driver, Integer time);
}
