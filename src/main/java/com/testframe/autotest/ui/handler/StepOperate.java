package com.testframe.autotest.ui.handler;


import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.ui.elements.ByFactory;
import com.testframe.autotest.ui.elements.module.action.ActionFactory;
import com.testframe.autotest.ui.elements.module.action.base.ActionI;
import com.testframe.autotest.ui.elements.module.check.AssertFactory;
import com.testframe.autotest.ui.elements.module.check.base.AssertI;
import com.testframe.autotest.ui.elements.module.wait.WaitFactory;
import com.testframe.autotest.ui.elements.module.wait.base.WaitI;
import com.testframe.autotest.ui.elements.operate.BrowserFindElement;
import com.testframe.autotest.ui.enums.OperateTypeEnum;
import com.testframe.autotest.ui.enums.check.AssertEnum;
import com.testframe.autotest.ui.enums.check.AssertModeEnum;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.enums.operate.OperateModeEnum;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import com.testframe.autotest.ui.meta.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class StepOperate {

    @Autowired
    private BrowserFindElement browserFindElement;

    @Autowired
    private ActionFactory actionFactory;

    @Autowired
    private AssertFactory assertFactory;

    @Autowired
    private WaitFactory waitFactory;

    /**
     * 单步骤运行
     * @param driver
     * @param stepExe
     */
    public void stepRun(WebDriver driver, StepExe stepExe) {
        WebElement element = null;
        Integer operateType = stepExe.getOperaType();
        LocatorInfo locatorInfo = stepExe.getLocatorInfo();
        OperateData operateData = stepExe.getOperateData();
        AssertData checkData = stepExe.getCheckData();
        WaitInfo waitInfo = stepExe.getWaitInfo();
        try {
            // 查找元素
            if (locatorInfo != null &&
                    OperateTypeEnum.getOperateByType(operateType) != OperateTypeEnum.WAIT) {
                // 等待类型的不用先找
                element = findElements(stepExe);
                // 调试用
                if (element != null) {
                    log.info("[SeleniumEventHandler:executeStep] find element {} by info = {} ",
                            element, JSON.toJSONString(stepExe));
                } else {
                    log.info("[SeleniumEventHandler:executeStep] can not find element by info = {}",
                            JSON.toJSONString(stepExe));
                }
            }
            switch (OperateTypeEnum.getOperateByType(operateType)) {
                case OPERATE:
                    runOperate(driver, element, operateData);
                    break;
                case WAIT:
                    log.info("[SeleniumEventHandler:executeStep] wait operate not need to find element");
                    runWait(driver, element, waitInfo, locatorInfo); // 元素不存在时会抛出异常
                    break;
                case ASSERT:
                    runAssert(driver, element, checkData); // 判断结果错误时抛出异常
                    break;
                default:
                    log.info("[SeleniumEventHandler:handler] uncorrect operate type, {}", operateType);
                    throw new SeleniumRunException(String.format("暂不支持 operateType=%s 操作类型", operateType));
            }
        } catch (SeleniumRunException e) {
            throw new SeleniumRunException(e.getMessage());
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:handler] unexpected exception, e = {}", e);
            throw new SeleniumRunException(e.getMessage());
        }
    }

    /**
     * 运行元素执行操作
     */
    private void runOperate(WebDriver driver, WebElement element, OperateData operateData) {
        log.info("[SeleniumEventHandler:runOperate] start run operate, operateData = {}",
                JSON.toJSONString(operateData));
        try {
            if (element == null &&
                    !OperateModeEnum.OPERATE_SKIP_ELEMENT.contains(operateData.getOperateMode())) {
                log.error("[SeleniumEventHandler:handler] no element can be operate");
                throw new SeleniumRunException("没有可被执行的元素，请填写正确的定位方式");
            }
            // 获取当前的操作类型
            ActionI nowOperateAction = actionFactory.getAction(
                    OperateEnum.getByOperateMode(operateData.getOperateMode()).getName());
            if (nowOperateAction == null) {
                log.error("[SeleniumEventHandler:runOperate] uncorrect operate mode {}",
                        operateData.getOperateMode());
                throw new SeleniumRunException(String.format("当前不支持 operateMode=%s 元素操作类型", operateData.getOperateMode()));
            }
            log.info("[SeleniumEventHandler:runOperate] now operation action = {}", JSON.toJSONString(nowOperateAction));
            // 需要操作的函数名
            String needOperateFunc = OperateModeEnum.getByType(operateData.getOperateMode()).getFunc();
            Method method = findMethod(nowOperateAction.getMethods(), needOperateFunc);
            log.info("[SeleniumEventHandler:runOperate] find need run method, method = {}", method.getName());
            method.invoke(nowOperateAction, driver, element, operateData);
            log.info("[SeleniumEventHandler:runOperate] run operate success over");
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:runCheck] run operate error, reason ", e);
            throw new SeleniumRunException(e.getMessage());
        }
    }

    /**
     * 运行元素检验操作
     * 判断元素不符合预期时，抛出异常
     */
    private void runAssert(WebDriver driver, WebElement element, AssertData assertData) {
        log.info("[SeleniumEventHandler:runAssert] start run assert, assertData = {}",
                JSON.toJSONString(assertData));
        try {
            // 获取当前的验证类型
            AssertI nowAssert = assertFactory.getAssert(
                    AssertEnum.getByAssertMode(assertData.getAssertMode()).getName()
            );
            if (nowAssert == null) {
                log.error("[SeleniumEventHandler:runAssert] uncorrect assert mode {}", assertData.getAssertMode());
                throw new SeleniumRunException(String.format("当前不支持 assertMode=%s 元素验证类型", assertData.getAssertMode()));
            }
            // 检验操作的函数名
            String needAssertFunc = AssertModeEnum.getByType(assertData.getAssertMode()).getFunc();
            Method method = findMethod(nowAssert.getMethods(), needAssertFunc);
            log.info("[SeleniumEventHandler:runAssert] find need run method, method = {}", method.getName());
            Boolean stepRunResult = (Boolean) method.invoke(nowAssert, driver, element, assertData);
            log.info("[SeleniumEventHandler:runAssert] run assert success over, result = {}", stepRunResult);
            if (stepRunResult == false) {
                throw new SeleniumRunException(String.format("%s 验证方式，不存在 %s",
                        assertData.getAssertMode(), assertData.getExpectString().trim()));
            }
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:runAssert] run assert error, reason ", e);
            throw new SeleniumRunException(e.getMessage());
        }
    }

    /**
     * 运行元素等待操作
     * 元素不存在时会被抛出异常
     */
    private void runWait(WebDriver driver, WebElement element, WaitInfo waitInfo, LocatorInfo locatorInfo) {
        log.info("[SeleniumEventHandler:runWait] start run wait, waitInfo = {}",
                JSON.toJSONString(waitInfo));
        try {
            // 获取当前的等待类型
            WaitI nowWait = waitFactory.getWait(WaitModeEnum.getByType(waitInfo.getWaitMode()).getWaitIdentity(), driver);
            if (nowWait == null) {
                log.error("[SeleniumEventHandler:runWait] uncorrect wait mode {}", waitInfo.getWaitMode());
                throw new SeleniumRunException(String.format("当前不支持 waitMode=%s 等待类型", waitInfo.getWaitMode()));
            }
            log.info("[SeleniumEventHandler:runWait] now wait = {}", nowWait.waitIdentity());
            By by = ByFactory.createBy(locatorInfo.getLocatedType(), locatorInfo.getExpression());
            nowWait.wait(by, waitInfo.getWaitTime());
            log.info("[SeleniumEventHandler:runWait] run wait success over");
        } catch (SeleniumRunException e) {
            log.error("[SeleniumEventHandler:runWait] no expected element exist");
            throw new SeleniumRunException(e.getMessage());
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:runWait] run wait error, reason ", e);
            throw new SeleniumRunException(e.getMessage());
        }
    }

    private WebElement findElements(StepExe stepExe) {
        try {
            LocatorInfo locatorInfo = stepExe.getLocatorInfo();
            WebElement webElement = browserFindElement.findElementByType(locatorInfo);
            return webElement;
        } catch (SeleniumRunException e) {
            log.info("[SeleniumEventHandler:findElements] find element fail, reason = {}",  e.getMessage());
            throw new SeleniumRunException(e.getMessage());
        }
    }

    private Method findMethod(Method[] methods, String funcName) {
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(funcName)) {
                return methods[i];
            }
        }
        log.error("[SeleniumEventHandler:findMethod] can not find func {}", funcName);
        throw new SeleniumRunException(String.format("方法名 %s 错误/不存在", funcName));
    }
}
