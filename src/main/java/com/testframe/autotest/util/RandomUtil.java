package com.testframe.autotest.util;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public class RandomUtil {

    public static final String charStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static final char[] chars = charStr.toCharArray();

    public static String randomCode(int length) {
        if (length < 1) throw new IllegalArgumentException();
        Random random = new SecureRandom();
        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = chars[random.nextInt(chars.length)];
        }
        return new String(buf);
    }

    public static void main(String[] args) {
        System.out.println(randomCode(8));
    }

}
