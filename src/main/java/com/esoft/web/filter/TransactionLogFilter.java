package com.esoft.web.filter;

import com.esoft.domain.Transaction;
import com.esoft.repository.TransactionRepository;
import com.esoft.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class TransactionLogFilter extends OncePerRequestFilter {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
        CachedBodyHttpServletResponse wrappedResponse = new CachedBodyHttpServletResponse(response);

        long start = System.currentTimeMillis();
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        long duration = System.currentTimeMillis() - start;

        String method = request.getMethod();
        String path = request.getRequestURI();

        Long userId = SecurityUtils.getCurrentUserId().orElse(0L);
        String username = SecurityUtils.getCurrentUserLogin()
            .orElse("anynomous");
        String clientIp = request.getRemoteAddr();
        int status = wrappedResponse.getStatus();
//        String reqBody = new String(wrappedRequest.getCachedBody());
//        String resBody = new String(wrappedResponse.getCachedBody());

        Transaction log = new Transaction();
        log.setRequestMethod(method);
        log.setRequestPath(path);
        log.setUserId(userId);
        log.setUsername(username);
        log.setClientIp(clientIp);
        log.setStatus(status);
        log.setDuration(duration);
        log.setCreatedDate(Instant.now());
        transactionRepository.save(log);
        wrappedResponse.copyBodyToResponse();
    }
}
