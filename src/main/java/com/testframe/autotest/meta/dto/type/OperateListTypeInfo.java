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
public class OperateListTypeInfo {

    private Integer operateType = OperateTypeEnum.OPERATE.getType();

    private String operateName = OperateTypeEnum.OPERATE.getName();

    private List<OperateClassInfo> operateClassInfos;
}
