package com.testframe.autotest.meta.dto.type;

import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @date:2022/11/19 17:44
 * @author: lyf
 */
@Data
public class AllTypeInfo {

    private OperateListTypeInfo operateListTypeInfo;

    private AssertListTypeInfo assertListTypeInfo;

    private WaitListTypeInfo waitListTypeInfo;

}
