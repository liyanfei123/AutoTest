package com.testframe.autotest.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtil {

    public static boolean isRepeat(List<Long> list) {
        Set<Long> set = new HashSet<Long>();
        for (Long element : list) {
            set.add(element);
        }
        if (set.size() != list.size()) {
            return true;
        } else {
            return false;
        }
    }

}
