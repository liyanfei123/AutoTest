package com.testframe.autotest.ui.handler;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.enums.*;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.exception.SeleniumRunException;
import com.testframe.autotest.core.repository.StepExecuteRecordRepository;
import com.testframe.autotest.meta.bo.Step;
import com.testframe.autotest.meta.bo.StepExecuteRecord;
import com.testframe.autotest.service.SceneExecuteService;
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
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import com.testframe.autotest.ui.meta.AssertData;
import com.testframe.autotest.ui.elements.operate.ChromeFindElement;
import com.testframe.autotest.ui.enums.OperateTypeEnum;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.enums.operate.OperateModeEnum;
import com.testframe.autotest.ui.handler.event.SeleniumRunEvent;
import com.testframe.autotest.ui.meta.LocatorInfo;
import com.testframe.autotest.ui.meta.OperateData;
import com.testframe.autotest.ui.meta.StepExe;
import com.testframe.autotest.ui.meta.WaitInfo;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private SceneExecuteService sceneExecuteService;

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

    private void chromeInit() {
        log.info("[SeleniumEventHandler:chromeInit] start init chrome");
        // TODO: 2022/11/19 可让用户自行选择浏览器
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        // 优化加载策略
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);
        driver = new ChromeDriver(chromeOptions);
        chromeFindElement.setDriver(driver);
        log.info("[SeleniumEventHandler:chromeInit] init chrome over");
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void eventHandler(SeleniumRunEvent seleniumRunEvent) {
        try {
            log.info("[SeleniumEventHandler:eventHandler] get seleniumRunEvent, event={}",
                    JSON.toJSONString(seleniumRunEvent));
            executeEvent(seleniumRunEvent, SceneExecuteEnum.SINGLE.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对事件进行处理和执行
     * @param seleniumRunEvent
     * @return
     */
    private String executeEvent(SeleniumRunEvent seleniumRunEvent, Integer type) {
        if (seleniumRunEvent == null) {
            return "";
        }
        if (type == SceneExecuteEnum.SINGLE.getType()) {
            log.info("[SeleniumEventHandler:executeEvent] start execute event");
        } else if (type == SceneExecuteEnum.BELOW.getType()){
            log.info("[SeleniumEventHandler:executeEvent] start execute son scene event");
        }
        Long recordId = seleniumRunEvent.getSceneRunRecordInfo().getRecordId();
        Map<Long, StepExecuteRecord> stepExecuteRecordMap = new HashMap<>(); // 步骤执行信息保存
        List<StepExe> stepExes = seleniumRunEvent.getStepExes();  // 需要执行的步骤信息
        stepExes.forEach(stepExe -> {
            // 生成每个步骤的执行记录
            StepExecuteRecord stepExecuteRecord = new StepExecuteRecord(recordId, stepExe.getStepId(), 0L,
                    stepExe.getStepName(), null, StepRunResultEnum.NORUN.getType());
            stepExecuteRecordMap.put(stepExe.getStepId(), stepExecuteRecord);
        });
        String sceneFailReason = "";  // 场景失败原因
        try {
            if (type == SceneExecuteEnum.SINGLE.getType()) {
                // 作为父场景执行时初始化
                chromeInit();
                // 默认打开当前场景中的url
                log.info("[SeleniumEventHandler:eventHandler] open url = {}", seleniumRunEvent.getSceneRunInfo().getUrl());
                driver.get(seleniumRunEvent.getSceneRunInfo().getUrl());
                log.info("[SeleniumEventHandler:eventHandler] open url over");
                chromeFindElement.init(seleniumRunEvent.getWaitInfo()); // 配置全局等待方式 子场景执行共用父场景的配置
            }
            sceneFailReason = executeStepSet(stepExes, stepExecuteRecordMap, sceneFailReason);
        } catch (Exception e) {
            // 场景未成功开启的其他失败原因
            e.printStackTrace();
            sceneFailReason = e.getMessage();
        } finally {
            if (driver != null && type == SceneExecuteEnum.SINGLE.getType()) {
                driver.quit();
            }
        }
        try {
            saveRunResult(seleniumRunEvent, stepExecuteRecordMap, sceneFailReason);
        } catch (Exception e) {
            throw new AutoTestException("场景执行结果保存更新失败");
        }
        log.info("[SeleniumEventHandler:executeEvent] event end");
        return sceneFailReason;
    }

    private String executeStepSet(List<StepExe> stepExes, Map<Long, StepExecuteRecord> stepExecuteRecordMap,
                             String sceneFailReason) {
        try {
            for (StepExe stepExe : stepExes) {
                try {
                    // 判断暂停的步骤，更新其状态
                    if (stepExe.getStatus() == StepStatusEnum.CLOSE.getType()) {
                        stepExecuteRecordMap.get(stepExe.getStepId()).setStatus(StepRunResultEnum.STOP.getType());
                        continue;
                    }
                    if (stepExe.getStepSceneId() > 0) {
                        // 子场景执行
                        log.info("[SeleniumEventHandler:executeStepSet] start run scene step, stepExeInfo = {}",
                                JSON.toJSONString(stepExe));
                        SeleniumRunEvent seleniumRunEvent = null;
                        try {
                            seleniumRunEvent = sceneExecuteService.generateEvent(stepExe.getStepSceneId(), SceneExecuteEnum.BELOW.getType());
                            // 修改子场景执行记录id
                            Long sonSceneRecordId = seleniumRunEvent.getSceneRunRecordInfo().getRecordId();
                            stepExecuteRecordMap.get(stepExe.getStepId()).setSceneRecordId(sonSceneRecordId);
                        } catch (AutoTestException e) {
                            // 子场景执行事件生成错误异常
                            sceneFailReason = e.getMessage();
                        }
                        sceneFailReason = executeEvent(seleniumRunEvent, SceneExecuteEnum.BELOW.getType()); // 执行子场景
                        if (sceneFailReason != null && !sceneFailReason.equals("")) {
                            stepExecuteRecordMap.get(stepExe.getStepId()).setStatus(StepRunResultEnum.FAIL.getType());
                            stepExecuteRecordMap.get(stepExe.getStepId()).setReason(sceneFailReason);
                            log.info("[SeleniumEventHandler:executeStepSet] run scene step fail, stepInfo = {}, reason = {}",
                                    JSON.toJSONString(stepExe), sceneFailReason);
                        } else {
                            // 执行状态置为成功
                            stepExecuteRecordMap.get(stepExe.getStepId()).setStatus(StepRunResultEnum.SUCCESS.getType());
                            log.info("[SeleniumEventHandler:executeStepSet] run scene step success");
                        }
                    } else {
                        log.info("[SeleniumEventHandler:executeStepSet] start run single step, stepExeInfo = {}",
                                JSON.toJSONString(stepExe));
                        stepRun(driver, stepExe);
                        stepExecuteRecordMap.get(stepExe.getStepId()).setStatus(StepRunResultEnum.SUCCESS.getType());
                    }
                    log.info("[SeleniumEventHandler:executeStepSet] run single step success");
                } catch (SeleniumRunException e) {
                    // 单步骤执行时有异常进行步骤操作记录保存，并保存错误消息
                    sceneFailReason = e.getMessage();
                    if (sceneFailReason == null || sceneFailReason.equals("")) {
                        sceneFailReason = JSON.toJSONString(e);
                    }
                    stepExecuteRecordMap.get(stepExe.getStepId()).setStatus(StepRunResultEnum.FAIL.getType());
                    stepExecuteRecordMap.get(stepExe.getStepId()).setReason(sceneFailReason);
                    log.info("[SeleniumEventHandler:executeStepSet] run step fail, stepInfo = {}, reason = {}",
                            JSON.toJSONString(stepExe), e);
                    break;
                }
            }
        } catch (SeleniumRunException e) {
            // 预期内的失败原因
            e.printStackTrace();
            sceneFailReason = e.getMessage();
        }
        return sceneFailReason;
    }


    /**
     * 不存在子场景时的执行方法
     * @param seleniumRunEvent
     */
    @Deprecated
//    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void eventHandlerOld(SeleniumRunEvent seleniumRunEvent) {
        log.info("[SeleniumEventHandler:handler] get event info, {}", JSON.toJSONString(seleniumRunEvent));
        Long recordId = seleniumRunEvent.getSceneRunRecordInfo().getRecordId();
        Map<Long, StepExecuteRecord> stepExecuteRecordMap = new HashMap<>(); // 步骤执行信息保存
        List<StepExe> stepExes = seleniumRunEvent.getStepExes();  // 需要执行的步骤信息
        stepExes.forEach(stepExe -> {
            // 生成每个步骤的执行记录
            StepExecuteRecord stepExecuteRecord = new StepExecuteRecord(recordId, stepExe.getStepId(), stepExe.getStepSceneId(),
                    stepExe.getStepName(), null, StepRunResultEnum.NORUN.getType());
            stepExecuteRecordMap.put(stepExe.getStepId(), stepExecuteRecord);
        });
        String sceneFailReason = "";  // 场景失败原因
        try {
            chromeInit();
             // 默认打开当前场景中的url
            log.info("[SeleniumEventHandler:eventHandler] open url = {}", seleniumRunEvent.getSceneRunInfo().getUrl());
            driver.get(seleniumRunEvent.getSceneRunInfo().getUrl());
            log.info("[SeleniumEventHandler:eventHandler] open url over");
            chromeFindElement.init(seleniumRunEvent.getWaitInfo()); // 配置全局等待方式
            for (StepExe stepExe : stepExes) {
                try {
                    // 判断暂停的步骤，更新其状态
                    if (stepExe.getStatus() == StepStatusEnum.CLOSE.getType()) {
                        stepExecuteRecordMap.get(stepExe.getStepId()).setStatus(StepRunResultEnum.STOP.getType());
                        continue;
                    }
                    log.info("[SeleniumEventHandler:eventHandler] start run step, stepExeInfo = {}",
                             JSON.toJSONString(stepExe));
                    stepRun(driver, stepExe);
                    // 执行状态置为成功
                    stepExecuteRecordMap.get(stepExe.getStepId()).setStatus(StepRunResultEnum.SUCCESS.getType());
                    log.info("[SeleniumEventHandler:eventHandler] run step success");
                } catch (SeleniumRunException e) {
                    // 有异常进行步骤操作记录保存，并保存错误消息
                    sceneFailReason = e.getMessage();
                    if (sceneFailReason == null || sceneFailReason.equals("")) {
                        sceneFailReason = JSON.toJSONString(e);
                    }
                    stepExecuteRecordMap.get(stepExe.getStepId()).setStatus(StepRunResultEnum.FAIL.getType());
                    stepExecuteRecordMap.get(stepExe.getStepId()).setReason(sceneFailReason);
                    log.info("[SeleniumEventHandler:eventHandler] run step fail, stepInfo = {}, reason = {}",
                            JSON.toJSONString(stepExe), e);
                    break;
                }
            }
        } catch (SeleniumRunException e) {
            // 预期内的失败原因
            e.printStackTrace();
            sceneFailReason = e.getMessage();
        } catch (Exception e) {
            // 场景未成功开启的其他失败原因
            e.printStackTrace();
            sceneFailReason = e.getMessage();
        } finally {
            if (driver != null) {
                 driver.quit();
            }
            try {
                saveRunResult(seleniumRunEvent, stepExecuteRecordMap, sceneFailReason);
            } catch (Exception e) {
                throw new AutoTestException("场景执行结果保存更新失败");
            }
        }
    }

    /**
     * 单步骤运行
     * @param driver
     * @param stepExe
     */
    private void stepRun(WebDriver driver, StepExe stepExe) {
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
            WebElement webElement = chromeFindElement.findElementByType(locatorInfo);
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


    private void saveRunResult(SeleniumRunEvent seleniumRunEvent, Map<Long, StepExecuteRecord> stepExecuteRecordMap,
                               String sceneFailReason) {
        try {
            List<Long> runOrderList = seleniumRunEvent.getSceneRunInfo().getRunOrderList();
            List<StepExecuteRecord> stepExecuteRecords = new ArrayList<StepExecuteRecord>(stepExecuteRecordMap.values());
            // 不改变步骤编排顺序
            Collections.sort(stepExecuteRecords, new Comparator<StepExecuteRecord>() {
                @Override
                public int compare(StepExecuteRecord record1, StepExecuteRecord record2) {
                    return runOrderList.indexOf(record1.getStepId()) - runOrderList.indexOf(record2.getStepId());
                }
            });
            List<Integer> stepRunStatus = stepExecuteRecords.stream().map(StepExecuteRecord::getStatus)
                    .collect(Collectors.toList());
            Set stepRunStatusSet = new HashSet(stepRunStatus);
            log.info("[SeleniumEventHandler:saveRunResult] all step run result, {}", JSON.toJSONString(stepRunStatus));
            if (stepRunStatusSet.size() == 1 && stepRunStatusSet.contains(StepRunResultEnum.NORUN.getType())) {
                // 未执行任何步骤，无需保存步骤，只需更新场景执行信息
                sceneRecordService.updateRecord(seleniumRunEvent.getSceneRunRecordInfo().getRecordId(),
                        SceneStatusEnum.FAIL.getType(), "场景执行失败，原因：" + sceneFailReason);
            } else if (stepRunStatusSet.size() == 1 && stepRunStatusSet.contains(StepRunResultEnum.RUN.getType())) {
                // 未执行任何步骤，无需保存步骤，只需更新场景执行信息
                sceneRecordService.updateRecord(seleniumRunEvent.getSceneRunRecordInfo().getRecordId(),
                        SceneStatusEnum.ING.getType(), null);
            } else if (stepRunStatusSet.size() >= 1 &&
                    (stepRunStatusSet.contains(StepRunResultEnum.NORUN.getType()) // 执行了部分步骤，但未全部执行完，需同时更新
                            || stepRunStatusSet.contains(StepRunResultEnum.FAIL.getType()))) { // 执行结果包含失败的，需同时更新
                stepRecordService.batchSaveRecord(stepExecuteRecords);
                sceneRecordService.updateRecord(seleniumRunEvent.getSceneRunRecordInfo().getRecordId(),
                        SceneStatusEnum.FAIL.getType(), sceneFailReason);
            } else if (stepRunStatusSet.size() >= 1 &&
                    (stepRunStatusSet.contains(StepRunResultEnum.SUCCESS.getType())  // 全部执行成功
                            || stepRunStatusSet.contains(StepRunResultEnum.STOP.getType()))) { // 包含暂停的
                stepRecordService.batchSaveRecord(stepExecuteRecords);
                sceneRecordService.updateRecord(seleniumRunEvent.getSceneRunRecordInfo().getRecordId(),
                        SceneStatusEnum.SUCCESS.getType(), null);
            }
        } catch (Exception e) {
            log.error("[SeleniumEventHandler:saveRunResult] save run result error, reason = {}", e);
            throw new AutoTestException("场景执行信息保存失败");
        }
    }
}
