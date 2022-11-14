package com.testframe.autotest.meta.bo;


import com.testframe.autotest.core.enums.StepOrderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static List<Long> orderToList(String orderStr) {
        // "[8, 9, 10, 11, 12, 13, 14, 15, 18]"
        orderStr = orderStr.substring(1, orderStr.length()-1).replaceAll(" ", "");
        if (orderStr.equals("")) {
            return Collections.EMPTY_LIST;
        }
        String[] orders = orderStr.split(",");
        List<Long> orderList = new ArrayList<>();
        for (String order : orders) {
            orderList.add(Long.parseLong(order));
        }
        return orderList;
    }

}
