package com.testframe.autotest.meta.bo;


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
public class SceneStepOrder {

    private Long id;

    private Long sceneId;

    private Long recordId;

    // 不处理
    private List<Long> orderList;

    // 调用接口保存时判断type类型进行赋值
    private Integer type;

    public static SceneStepOrder build(Long sceneId, String orderStr) {
        return SceneStepOrder.builder()
                .sceneId(sceneId)
                .orderList(orderToList(orderStr))
                .type(StepOrderEnum.BEFORE.getType()).build();
    }


}
