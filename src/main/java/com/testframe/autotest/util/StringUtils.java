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
        List<Long> orderList = new ArrayList<>();
        orderStr = orderStr.substring(1, orderStr.length()-1).replaceAll(" ", "");
        if (orderStr.equals("")) {
            return orderList;
        }
        String[] orders = orderStr.split(",");
        for (String order : orders) {
            orderList.add(Long.parseLong(order));
        }
        return orderList;
    }

    public static List<Integer> strToList(String str) {
        List<Integer> list = new ArrayList<>();
        if (str == null || str.equals("")) {
            return list;
        }
        String[] chs = str.split(",");
        for (String ch : chs) {
            list.add(Integer.parseInt(ch));
        }
        return list;
    }

}
