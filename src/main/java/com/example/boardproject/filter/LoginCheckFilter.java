package com.example.boardproject.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

@Component
public class LoginCheckFilter implements Filter {
    private static final String[] whiteList = {"/login", "/register", "/alert", "/logout", "/css/**"};

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String requestURI = httpServletRequest.getRequestURI();

        HttpSession session = httpServletRequest.getSession(false);

        if (!PatternMatchUtils.simpleMatch(whiteList, requestURI)) {
            if (session == null) {
                httpServletResponse.sendRedirect("/login");
                return;
            }
        }
        chain.doFilter(servletRequest, servletResponse);
    }
}
