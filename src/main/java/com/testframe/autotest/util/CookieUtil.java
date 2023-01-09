package com.testframe.autotest.util;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 操作cookie类
 */
@Slf4j
public class CookieUtil {

    private static final String SESSION = "session";

    public static void  addCookie(HttpServletResponse response, String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static String getCookieValue(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length < 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().trim().equals(key)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
