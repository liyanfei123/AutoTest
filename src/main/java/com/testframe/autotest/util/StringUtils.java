package com.testframe.autotest.util;

import java.util.regex.Pattern;

public class StringUtils {

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
