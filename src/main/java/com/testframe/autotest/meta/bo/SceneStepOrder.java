package com.testframe.autotest.meta.bo;


import com.testframe.autotest.core.enums.StepOrderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SceneStepOrder {

    private Long id;

    private Long sceneId;

    private String orderList;

    // 调用接口保存时判断type类型进行赋值
    private Integer type;

    public static SceneStepOrder build(Long sceneId, String orderList) {
        return SceneStepOrder.builder()
                .sceneId(sceneId)
                .orderList(orderList)
                .type(StepOrderEnum.BEFORE.getType()).build();
    }

    public static List<Long> orderToList(String orderStr) {
        orderStr = orderStr.substring(1, orderStr.length()-1);
        String[] orders = orderStr.split(",");
        List<Long> orderList = new ArrayList<>();
        for (String order : orders) {
            orderList.add(Long.parseLong(order));
        }
        return orderList;
    }

}
