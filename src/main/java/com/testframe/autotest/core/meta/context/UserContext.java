package com.testframe.autotest.core.meta.context;

public class UserContext {

    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();


    public static long getUserId() {
        Long userId = userIdThreadLocal.get();
        return null == userId ? 0L : userId;
    }

    public static void setUserId(long userId) {
        userIdThreadLocal.set(userId);
    }

    public static void clearUserId() {
        userIdThreadLocal.remove();
    }

}
