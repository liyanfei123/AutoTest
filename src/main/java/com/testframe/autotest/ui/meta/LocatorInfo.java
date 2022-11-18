package com.testframe.autotest.ui.meta;

import com.testframe.autotest.meta.dto.SceneDetailInfo;
import com.testframe.autotest.meta.dto.SceneInfoDto;
import com.testframe.autotest.meta.dto.StepInfoDto;
import com.testframe.autotest.meta.dto.StepUIInfo;
import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

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

    // 根据索引来选择操作
    // 若为1个或没有，则默认使用findElement
    private List<Integer> indexes;

    public static LocatorInfo build(StepUIInfo stepUIInfo, SceneInfoDto sceneInfoDto) {
        LocatorInfo locatorInfo = new LocatorInfo();
        locatorInfo.setLocatedType(stepUIInfo.getLocatorType());
        locatorInfo.setExpression(stepUIInfo.getLocator());
        locatorInfo.setIndexes(stepUIInfo.getIndexes());
        return locatorInfo;
    }


}
