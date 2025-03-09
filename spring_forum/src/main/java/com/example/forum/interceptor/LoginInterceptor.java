package com.example.forum.interceptor;

import com.example.forum.config.AppConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${forum.login.url}")
    private String defaultURL;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AppConfig.USER_SESSION) != null) {
            return true;
        }
        if (!defaultURL.startsWith("/")) {
            defaultURL = "/" + defaultURL;
        }
        response.sendRedirect(defaultURL);
        return false;
    }

}
