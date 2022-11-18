package com.testframe.autotest.ui.elements.wait;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public interface WaitElementI {

   public String waitIdentity();

   void wait(By by);

   void wait(By by, Integer time);


}
