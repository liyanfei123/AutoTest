package com.testframe.autotest.ui.elements.wait;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public interface WaitElementI {

   public String waitIdentity();

   WebElement wait(By by);

   List<WebElement> waits(By by);

}
