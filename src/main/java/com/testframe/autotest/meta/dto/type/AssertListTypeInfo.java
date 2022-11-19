package com.testframe.autotest.meta.dto.type;

import com.testframe.autotest.ui.enums.OperateTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/11/19 18:02
 * @author: lyf
 */
@Data
public class AssertListTypeInfo {

    private Integer operateType = OperateTypeEnum.ASSERT.getType();

    private String operateName = OperateTypeEnum.ASSERT.getName();

    private List<AssertClassInfo> assertClassInfos;
}
