package com.vuelosapp.flightreservation.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS_PER_MINUTE = 5;
    private final ConcurrentMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        if ("/auth/login".equals(path)) {
            String ip = getClientIP(httpRequest);
            RequestCounter counter = requestCounts.computeIfAbsent(ip, k -> new RequestCounter());
            
            synchronized (counter) {
                long currentTime = System.currentTimeMillis();
                counter.cleanOldRequests(currentTime);
                
                if (counter.getCount() >= MAX_REQUESTS_PER_MINUTE) {
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    httpResponse.getWriter().write("Demasiadas solicitudes. Por favor, intente más tarde.");
                    return;
                }
                
                counter.increment();
            }
        }

        chain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private static class RequestCounter {
        private final Queue<Long> timestamps = new LinkedBlockingQueue<>();
        private static final long ONE_MINUTE = 60 * 1000;

        public synchronized void increment() {
            long now = System.currentTimeMillis();
            timestamps.add(now);
        }

        public synchronized void cleanOldRequests(long currentTime) {
            while (!timestamps.isEmpty() && 
                  (currentTime - timestamps.peek() > ONE_MINUTE)) {
                timestamps.poll();
            }
        }

        public synchronized int getCount() {
            return timestamps.size();
        }
    }
}
