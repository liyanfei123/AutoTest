package com.testframe.autotest.ui.handler;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.SceneStatusEnum;
import com.testframe.autotest.core.enums.StepRunResultEnum;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.core.repository.StepExecuteRecordRepository;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import com.testframe.autotest.service.SceneRecordService;
import com.testframe.autotest.service.StepRecordService;
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
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
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
import org.checkerframework.checker.units.qual.A;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ChromeFindElement chromeFindElement;

    private WebDriver driver;

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

    @Autowired
    private SceneRecordService sceneRecordService;

    @Autowired
    private StepRecordService stepRecordService;

    @Autowired
    private StepExecuteRecordRepository stepExecuteRecordRepository;

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void eventHandler(SeleniumRunEvent seleniumRunEvent) {
         try {
             log.info("[SeleniumEventHandler:handler] get event info, {}", JSON.toJSONString(seleniumRunEvent));
             Long recordId = seleniumRunEvent.getSceneRunRecordInfo().getRecordId();
             // TODO: 2022/11/19 可让用户自行选择浏览器

             System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
             driver = new ChromeDriver();
             chromeFindElement.setDriver(driver);
             // 默认打开当前场景中的url
             driver.get(seleniumRunEvent.getSceneRunInfo().getUrl());
             chromeFindElement.init(seleniumRunEvent.getWaitInfo()); // 配置全局等待方式

             Map<Long, StepExecuteRecord> stepExecuteRecordMap = new HashMap<>(); // 步骤执行信息保存
             List<StepExeInfo> stepExeInfoList = seleniumRunEvent.getStepExeInfos();
             // 步骤默认是意外中断
             stepExeInfoList.forEach(stepExeInfo -> {
                 StepExecuteRecord stepExecuteRecord = new StepExecuteRecord(recordId, stepExeInfo.getStepId(),
                                 stepExeInfo.getStepName(), null, StepRunResultEnum.CLOSE.getType());
                 stepExecuteRecordMap.put(stepExeInfo.getStepId(), stepExecuteRecord);
                     });
             for (StepExeInfo stepExeInfo : stepExeInfoList) {
                 // 可先记录状态，待执行结束后再去保存
                 try {
                     execute(driver, stepExeInfo);
                     // 执行状态置为成功
                     stepExecuteRecordMap.get(stepExeInfo.getStepId()).setStatus(StepRunResultEnum.SUCCESS.getType());
                     log.info("[SeleniumEventHandler:eventHandler] run step success, stepInfo = {}",
                             JSON.toJSONString(stepExeInfo));
                 } catch (SeleniumRunException e) {
                     // 有异常进行步骤操作记录保存，并保存错误消息
                     stepExecuteRecordMap.get(stepExeInfo.getStepId()).setStatus(StepRunResultEnum.FAIL.getType());
                     stepExecuteRecordMap.get(stepExeInfo.getStepId()).setReason(e.getMessage());
                     log.info("[SeleniumEventHandler:eventHandler] run step fail, stepInfo = {}, reason = {}",
                             JSON.toJSONString(stepExeInfo), e);
                     break;
                 }
             }
             List<StepExecuteRecord> stepExecuteRecords = new ArrayList<StepExecuteRecord>(stepExecuteRecordMap.values());
             Boolean flag = stepExecuteRecordRepository.batchSaveStepExecuteRecord(stepExecuteRecords);
             if (flag == false) {
                 log.error("[SeleniumEventHandler:eventHandler] add step run records error, stepExecuteRecords = {}",
                         JSON.toJSONString(stepExecuteRecords));
                 throw new AutoTestException("步骤结果保存异常");
             }
         } catch (SeleniumRunException e) {
             // 直接启动失败，不需要对步骤保存
             sceneRecordService.updateRecord(seleniumRunEvent.getSceneRunRecordInfo().getRecordId(),
                     SceneStatusEnum.FAIL.getType(), e.getMessage());
         }
         catch (Exception e) {
             // 步骤未成功开启的失败原因
             e.printStackTrace();
             sceneRecordService.updateRecord(seleniumRunEvent.getSceneRunRecordInfo().getRecordId(),
                     SceneStatusEnum.FAIL.getType(), "场景检验失败，原因：" + e.getMessage());
         } finally {
             if (driver != null) {
                 driver.quit();
             }
         }
    }

    private void execute(WebDriver driver, StepExeInfo stepExeInfo) {
        WebElement element = null;
        Integer operateType = stepExeInfo.getOperaType();
        LocatorInfo locatorInfo = stepExeInfo.getLocatorInfo();
        OperateData operateData = stepExeInfo.getOperateData();
        AssertData checkData = stepExeInfo.getCheckData();
        WaitInfo waitInfo = stepExeInfo.getWaitInfo();
        try {
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
                    throw new SeleniumRunException(String.format("暂不支持 operateType=%s 操作类型", operateType));
            }
        } catch (SeleniumRunException e) {
            throw new SeleniumRunException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
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
            String name = OperateEnum.getByOperateMode(operateData.getOperateMode()).getName();
            ActionI nowOperateAction = actionFactory.getAction(
                    OperateEnum.getByOperateMode(operateData.getOperateMode()).getName()
            );
            if (nowOperateAction == null) {
                log.error("[SeleniumEventHandler:handler] uncorrect operate mode {}",
                        operateData.getOperateMode());
                throw new SeleniumRunException(String.format("当前不支持 operateMode=%s 元素操作类型", operateData.getOperateMode()));
            }
            // 需要操作的函数名
            String needOperateFunc = OperateModeEnum.getByType(operateData.getOperateMode()).getFunc();
            Method method = findMethod(nowOperateAction.getMethods(), needOperateFunc);
            method.invoke(nowOperateAction, driver, element, operateData);
            Method[] methods = nowOperateAction.getMethods();
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:runCheck] run operate error, reason ", e);
            throw new SeleniumRunException(e.getMessage());
        }
    }

    /**
     * 运行元素检验操作
     */
    private void runCheck(WebDriver driver, WebElement element, AssertData assertData) {
        try {
            // 获取当前的验证类型
            AssertI nowAssert = assertFactory.getAssert(
                    AssertEnum.getByAssertMode(assertData.getAssertMode()).getName()
            );
            if (nowAssert == null) {
                log.error("[SeleniumEventHandler:handler] uncorrect check mode {}", assertData.getAssertMode());
                throw new SeleniumRunException(String.format("当前不支持 assertMode=%s 元素验证类型", assertData.getAssertMode()));
            }
            // 检验操作的函数名
            String needAssertFunc = AssertModeEnum.getByType(assertData.getAssertMode()).getFunc();
//        if (element == null) {
//            // 执行不需要元素的等待验证，比如源码验证等
//        }
            Method[] methods = nowAssert.getMethods();
            Method method = findMethod(nowAssert.getMethods(), needAssertFunc);
            method.invoke(nowAssert, driver, element, assertData);
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:runCheck] run check error, reason ", e);
            throw new SeleniumRunException(e.getMessage());
        }
    }

    /**
     * 运行元素等待操作
     */
    private void runWait(WebDriver driver, WebElement element, WaitInfo waitInfo, LocatorInfo locatorInfo) {
        try {
            // 获取当前的等待类型
            WaitI nowWait = waitFactory.getWait(WaitModeEnum.getByType(waitInfo.getWaitMode()).getWaitIdentity());
            if (nowWait == null) {
                log.error("[SeleniumEventHandler:handler] uncorrect wait mode {}", waitInfo.getWaitMode());
                throw new SeleniumRunException(String.format("当前不支持 waitMode=%s 等待类型", waitInfo.getWaitMode()));
            }
            By by = ByFactory.createBy(locatorInfo.getLocatedType(), locatorInfo.getExpression());
            nowWait.wait(by, waitInfo.getWaitTime());
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
            throw new SeleniumRunException(e.getMessage());
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
        throw new SeleniumRunException(String.format("方法名 %s 错误/不存在", funcName));
    }

}
