package com.testframe.autotest.service.impl;

import com.testframe.autotest.meta.dto.type.*;
import com.testframe.autotest.service.TypeService;
import com.testframe.autotest.ui.enums.check.AssertEnum;
import com.testframe.autotest.ui.enums.check.AssertModeEnum;
import com.testframe.autotest.ui.enums.operate.OperateEnum;
import com.testframe.autotest.ui.enums.operate.OperateModeEnum;
import com.testframe.autotest.ui.enums.wait.WaitEnum;
import com.testframe.autotest.ui.enums.wait.WaitModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * 用于测试检查当前枚举类型是否有误
 * @date:2022/11/19 17:48
 * @author: lyf
 */
@Slf4j
@Service
public class TypeServiceImpl implements TypeService {

    @Override
    public AllTypeInfo getAllType() {
        // 元素操作类
        OperateListTypeInfo operateListTypeInfo = new OperateListTypeInfo();
        List<OperateClassInfo> operateClassInfos = new ArrayList<>();
        List<OperateEnum> operateEnums = OperateEnum.getTypes();
        operateEnums.forEach(operateEnum -> {
            OperateClassInfo operateClassInfo = new OperateClassInfo();
            operateClassInfo.setClassType(operateEnum.getType());
            operateClassInfo.setOperateClassName(operateEnum.getDesc());
            List<OperateModeEnum> operateModeEnums = operateEnum.getOperateModeEnum();
            List<OperateModeInfo> operateModeInfos = operateModeEnums.stream().map(OperateModeInfo::build)
                    .collect(Collectors.toList());
            operateClassInfo.setOperateModeInfos(operateModeInfos);
            operateClassInfos.add(operateClassInfo);
        });
        operateListTypeInfo.setOperateClassInfos(operateClassInfos);

        // 元素等待类
        WaitListTypeInfo waitListTypeInfo = new WaitListTypeInfo();
        List<WaitClassInfo> waitClassInfos = new ArrayList<>();
        List<WaitEnum> waitEnums = WaitEnum.getTypes();
        waitEnums.forEach(waitEnum -> {
            WaitClassInfo waitClassInfo = new WaitClassInfo();
            waitClassInfo.setClassType(waitEnum.getType());
            waitClassInfo.setWaitClassName(waitEnum.getDesc());
            List<WaitModeEnum> waitModeEnums = waitEnum.getWaitModeEnums();
            List<WaitModeInfo> waitModeInfos = waitModeEnums.stream().map(WaitModeInfo::build)
                    .collect(Collectors.toList());
            waitClassInfo.setWaitModeInfos(waitModeInfos);
            waitClassInfos.add(waitClassInfo);
        });
        waitListTypeInfo.setWaitClassInfos(waitClassInfos);

        // 元素检验类
        AssertListTypeInfo assertListTypeInfo = new AssertListTypeInfo();
        List<AssertClassInfo> assertClassInfos = new ArrayList<>();
        List<AssertEnum> assertEnums = AssertEnum.getTypes();
        assertEnums.forEach(assertEnum -> {
            AssertClassInfo assertClassInfo = new AssertClassInfo();
            assertClassInfo.setClassType(assertEnum.getType());
            assertClassInfo.setAssertClassName(assertEnum.getDesc());
            List<AssertModeEnum> assertModeEnums = assertEnum.getAssertModeEnums();
            List<AssertModeInfo> assertModeInfos = assertModeEnums.stream().map(AssertModeInfo::build)
                    .collect(Collectors.toList());
            assertClassInfo.setAssertModeInfos(assertModeInfos);
            assertClassInfos.add(assertClassInfo);
        });
        assertListTypeInfo.setAssertClassInfos(assertClassInfos);

        AllTypeInfo allTypeInfo = new AllTypeInfo();
        allTypeInfo.setOperateListTypeInfo(operateListTypeInfo);
        allTypeInfo.setWaitListTypeInfo(waitListTypeInfo);
        allTypeInfo.setAssertListTypeInfo(assertListTypeInfo);
        return allTypeInfo;
    }
}
