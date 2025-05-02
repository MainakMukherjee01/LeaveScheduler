package com.sap.fsad.leaveApp.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter implements Filter {
    private static final int MAX_REQUESTS_PER_MINUTE = 120;
    private static final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws java.io.IOException, jakarta.servlet.ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientIp = httpRequest.getRemoteAddr();

        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());
        if (counter.incrementAndCheckLimit()) {
            response.getWriter().write("Too many requests. Please try again later.");
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            return;
        }

        chain.doFilter(request, response);
    }

    private static class RequestCounter {
        private int count = 0;
        private long timestamp = System.currentTimeMillis();

        synchronized boolean incrementAndCheckLimit() {
            long now = System.currentTimeMillis();
            if (now - timestamp > TimeUnit.MINUTES.toMillis(1)) {
                count = 0;
                timestamp = now;
            }
            count++;
            return count > MAX_REQUESTS_PER_MINUTE;
        }
    }
}