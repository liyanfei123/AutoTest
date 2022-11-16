package com.testframe.autotest.ui.meta;

import com.testframe.autotest.meta.dto.SceneDetailInfo;
import com.testframe.autotest.meta.dto.SceneInfoDto;
import com.testframe.autotest.meta.dto.StepInfoDto;
import com.testframe.autotest.meta.dto.StepUIInfo;
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

    public static LocatorInfo build(StepUIInfo stepUIInfo, SceneInfoDto sceneInfoDto) {
        LocatorInfo locatorInfo = new LocatorInfo();
        locatorInfo.setLocatedType(stepUIInfo.getLocatorType());
        locatorInfo.setExpression(stepUIInfo.getLocator());
        locatorInfo.setWaitType(sceneInfoDto.getWaitType());
        locatorInfo.setWaitTime(sceneInfoDto.getWaitTime());
        return locatorInfo;
    }


}
