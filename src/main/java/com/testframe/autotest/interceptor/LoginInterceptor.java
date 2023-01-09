package com.testframe.autotest.interceptor;

import com.alibaba.fastjson.JSON;
import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.common.http.HttpStatus;
import com.testframe.autotest.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器
 * 若用户未登录的话，不允许用户执行
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private static final String testKey = "login"; // 用于测试的key

    @Autowired
    private AutoTestConfig autoTestConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[LoginInterceptor:preHandle] start...");
        if (!autoTestConfig.getLoginSwitch()) {
            return true;
        }
        String value = CookieUtil.getCookieValue(request, testKey);
        if (value != null && value.equals("true")) {
            return true;
        }
        throw new AutoTestException(HttpStatus.SC_UNAUTHORIZED, "未登陆");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }

}
