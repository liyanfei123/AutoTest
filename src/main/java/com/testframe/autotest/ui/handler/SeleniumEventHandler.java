package com.testframe.autotest.ui.handler;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.ByFactory;
import com.testframe.autotest.ui.elements.module.action.ActionFactory;
import com.testframe.autotest.ui.elements.module.action.base.ActionI;
import com.testframe.autotest.ui.elements.module.check.AssertFactory;
import com.testframe.autotest.ui.elements.module.check.base.AssertI;
import com.testframe.autotest.ui.elements.module.wait.WaitFactory;
import com.testframe.autotest.ui.elements.module.wait.base.WaitI;
import com.testframe.autotest.ui.enums.check.AssertEnum;
import com.testframe.autotest.ui.enums.check.AssertModeEnum;
import com.testframe.autotest.ui.enums.wait.WaitEnum;
import com.testframe.autotest.ui.meta.AssertData;
import com.testframe.autotest.ui.elements.operate.ChromeFindElement;
import com.testframe.autotest.ui.enums.OperateTypeEnum;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.enums.operate.OperateModeEnum;
import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;
import com.testframe.autotest.ui.meta.LocatorInfo;
import com.testframe.autotest.ui.meta.OperateData;
import com.testframe.autotest.ui.meta.StepExeInfo;
import com.testframe.autotest.ui.meta.WaitInfo;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

/**
 * Description:
 *
 * @date:2022/11/08 21:55
 * @author: lyf
 */
@DependsOn("myEventBus")
@Component
@Slf4j
public class SeleniumEventHandler implements EventHandlerI<SeleniumRunEvent> {

    private ChromeFindElement chromeFindElement;

//    @Autowired
//    @Qualifier("myEventBus")
//    private EventBus eventBus;
//
//    @PostConstruct
//    private void init() {
//        SeleniumEventHandler seleniumEventHandler = new SeleniumEventHandler();
//        log.info("开始注册当前监听器");
//        eventBus.register(seleniumEventHandler);
//    }

//    @Subscribe
//    public void eventHandler(Object event) {
//        try {
//            Thread.sleep(2000);
//            log.info("[SeleniumEventHandler:eventHandler] start handle selenium test, event = {}", JSON.toJSONString(event));
//        } catch (InterruptedException e) {
//            log.error("[SeleniumEventHandler:eventHandler] handle selenium error, reason = {}", e);
//        }
//    }


    @Autowired
    private ActionFactory actionFactory;

    @Autowired
    private AssertFactory assertFactory;

    @Autowired
    private WaitFactory waitFactory;

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void eventHandler(SeleniumRunEvent seleniumRunEvent) {
         try {
             log.info("[SeleniumEventHandler:handler] get event info, {}", JSON.toJSONString(seleniumRunEvent));
             // TODO: 2022/11/19 可让用户自行选择浏览器
             System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
             WebDriver driver = new ChromeDriver();
             chromeFindElement = new ChromeFindElement(driver);
             // 默认打开当前场景中的url
             driver.get(seleniumRunEvent.getSceneRunInfo().getUrl());
             chromeFindElement.init(seleniumRunEvent.getWaitInfo()); // 配置全局等待方式
             for (StepExeInfo stepExeInfo : seleniumRunEvent.getStepExeInfos()) {
                 execute(driver, stepExeInfo);
             }
             driver.quit();
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    private void execute(WebDriver driver, StepExeInfo stepExeInfo) {
        WebElement element = null;
        Integer operateType = stepExeInfo.getOperaType();
        LocatorInfo locatorInfo = stepExeInfo.getLocatorInfo();
        OperateData operateData = stepExeInfo.getOperateData();
        AssertData checkData = stepExeInfo.getCheckData();
        WaitInfo waitInfo = stepExeInfo.getWaitInfo();
        // 查找元素
        if (locatorInfo != null &&
                OperateTypeEnum.getOperateByType(operateType) != OperateTypeEnum.WAIT) {
                // 等待类型的不用先找
                element = findElements(stepExeInfo);
        }
        switch (OperateTypeEnum.getOperateByType(operateType)) {
            case OPERATE:
                runOperate(driver, element, operateData);
                break;
            case WAIT:
                runWait(driver, element, waitInfo, locatorInfo);
                break;
            case ASSERT:
                runCheck(driver, element, checkData);
                break;
            default:
                log.info("[SeleniumEventHandler:handler] uncorrect operate type, {}", operateType);
                throw new SeleniumRunException("暂不支持该操作类型");
        }
    }

    /**
     * 运行元素执行操作
     */
    private void runOperate(WebDriver driver, WebElement element, OperateData operateData) {
        try {
            if (element == null) {
                log.error("[SeleniumEventHandler:handler] no element can be operate");
                throw new SeleniumRunException("没有可被执行的元素，请填写正确的定位方式");
            }
            // 获取当前的操作类型
            ActionI nowOperateAction = actionFactory.getAction(
                    OperateEnum.getByOperateMode(operateData.getOperateMode()).getName()
            );
            if (nowOperateAction == null) {
                log.error("[SeleniumEventHandler:handler] uncorrect operate mode {}",
                        operateData.getOperateMode());
                throw new SeleniumRunException("当前不支持该元素操作类型");
            }
            // 需要操作的函数名
            String needOperateFunc = OperateModeEnum.getByType(operateData.getOperateMode()).getFunc();
            Method method = findMethod(nowOperateAction.getMethods(), needOperateFunc);
            method.invoke(nowOperateAction, driver, element, operateData);
            Method[] methods = nowOperateAction.getMethods();
        } catch (SeleniumRunException e) {
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:runCheck] run operate error, reason ", e);
            throw new AutoTestException(e.getMessage());
        }
    }

    /**
     * 运行元素检验操作
     */
    private void runCheck(WebDriver driver, WebElement element, AssertData checkData) {
        try {
            // 获取当前的验证类型
            AssertI nowAssert = assertFactory.getAssert(
                    AssertEnum.getByAssertMode(checkData.getAssertMode()).getName()
            );
            if (nowAssert == null) {
                log.error("[SeleniumEventHandler:handler] uncorrect check mode {}", checkData.getAssertMode());
                throw new SeleniumRunException("当前不支持该元素验证类型");
            }
            // 检验操作的函数名
            String needAssertFunc = AssertModeEnum.getByType(checkData.getAssertMode()).getFunc();
//        if (element == null) {
//            // 执行不需要元素的等待验证，比如源码验证等
//        }
            Method[] methods = nowAssert.getMethods();
            Method method = findMethod(nowAssert.getMethods(), needAssertFunc);
            method.invoke(nowAssert, driver, element, checkData);
        } catch (SeleniumRunException e) {
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:runCheck] run check error, reason ", e);
            throw new AutoTestException(e.getMessage());
        }
    }

    /**
     * 运行元素等待操作
     */
    private void runWait(WebDriver driver, WebElement element, WaitInfo waitInfo, LocatorInfo locatorInfo) {
        try {
            // 获取当前的等待类型
            WaitI nowWait = waitFactory.getWait(
                    WaitEnum.getByWaitMode(waitInfo.getWaitMode()).getName()
            );
            if (nowWait == null) {
                log.error("[SeleniumEventHandler:handler] uncorrect wait mode {}", waitInfo.getWaitMode());
                throw new SeleniumRunException("当前不支持该等待类型");
            }
            By by = ByFactory.createBy(locatorInfo.getLocatedType(), locatorInfo.getExpression());
            nowWait.wait(by, waitInfo.getWaitTime());
        } catch (SeleniumRunException e) {
            throw new AutoTestException(e.getMessage());
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:runWait] run wait error, reason ", e);
            throw new AutoTestException(e.getMessage());
        }
//        if (element == null) {
//            // 执行不需要元素的等待操作，例如强制等待
//        }
    }

    private WebElement findElements(StepExeInfo stepExeInfo) {
        try {
            LocatorInfo locatorInfo = stepExeInfo.getLocatorInfo();
            WebElement webElement = chromeFindElement.findElementByType(locatorInfo);
            return webElement;
        } catch (SeleniumRunException e) {
            log.info("[SeleniumEventHandler:findElements] find element fail, reason = {}",  e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:findElements] find element has exception, e = {}", e);
            throw new AutoTestException(e.getMessage());
        }
    }

//    protected void executeAction(ActionI nowOperateAction, String actionName,
//                                 WebDriver driver, WebElement element, OperateData data) {
//        Method[] methods = nowOperateAction.getMethods();
//        try {
//            for (int i = 0; i < methods.length; i++) {
//                if (methods[i].getName().equals(actionName)) {
//                    methods[i].invoke(nowOperateAction, driver, element, data);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private Method findMethod(Method[] methods, String funcName) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(funcName)) {
               return methods[i];
            }
        }
        log.error("[SeleniumEventHandler:findMethod] can not find func {}", funcName);
        throw new SeleniumRunException("方法名错误");
    }

}
