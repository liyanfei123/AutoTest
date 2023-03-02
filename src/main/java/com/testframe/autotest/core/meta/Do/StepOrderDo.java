package com.testframe.autotest.core.meta.Do;


import com.testframe.autotest.core.enums.StepOrderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.testframe.autotest.util.StringUtils.orderToList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StepOrderDo {

    private Long id;

    private Long sceneId;

    private Long recordId;

    // 不处理
    private List<Long> orderList;

    // 调用接口保存时判断type类型进行赋值
    // StepOrderEnum
    private Integer type;


}
