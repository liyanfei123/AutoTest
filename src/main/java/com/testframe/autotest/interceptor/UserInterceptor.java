package com.testframe.autotest.interceptor;

import com.testframe.autotest.core.config.AutoTestConfig;
import com.testframe.autotest.core.exception.AutoTestException;
import com.testframe.autotest.core.meta.common.http.HttpStatus;
import com.testframe.autotest.core.meta.context.UserContext;
import com.testframe.autotest.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户拦截器
 * 若用户不是可执行用户，则不给执行
 * 测试时，使用apollo配置设置
 */
@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private AutoTestConfig autoTestConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("[UserInterceptor:preHandle] start...");
        handleUserId(request);
        return true;
    }

    private void handleUserId(HttpServletRequest request) {
        if (!autoTestConfig.getUserSwitch()){
            return;
        }
        long userId;
        String userIdStr = request.getHeader("userId");
        if (userIdStr == null || userIdStr.equals("")) {
            userIdStr = CookieUtil.getCookieValue(request, "autoUserId");
        }
        if (userIdStr == null) {
            userId = 0;
        } else {
            userId = Long.parseLong(userIdStr);
        }
        // TODO: 2023/9/8 判断当前用户是否有注册 
        UserContext.setUserId(userId);
        // 白名单用户
        if (autoTestConfig.getWhiteUserIds().contains(Long.valueOf(userId))) {
            return;
        }
        // todo 授权用户，需要单独进行判断
        throw new AutoTestException(HttpStatus.SC_BAD_REQUEST, "未授权");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContext.clearUserId();
    }

}
