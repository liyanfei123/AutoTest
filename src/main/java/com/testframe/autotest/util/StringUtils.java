package com.testframe.autotest.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtils {

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * "[8, 9, 10, 11, 12, 13, 14, 15, 18]" 转 列表
     * @param orderStr
     * @return
     */
    public static List<Long> orderToList(String orderStr) {
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
