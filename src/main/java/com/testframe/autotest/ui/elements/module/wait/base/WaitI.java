package com.testframe.autotest.ui.elements.module.wait.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public interface WaitI {

   public String waitIdentity();

   void wait(By by);

   void wait(By by, Integer time);

   void setWebDriver(WebDriver webDriver);

   void setTime(Integer time);

}
