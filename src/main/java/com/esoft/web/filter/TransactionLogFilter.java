package com.esoft.web.filter;

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

@Component
public class TransactionLogFilter extends OncePerRequestFilter {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Wrap request and response to read body
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
        CachedBodyHttpServletResponse wrappedResponse = new CachedBodyHttpServletResponse(response);

        long start = System.currentTimeMillis();
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        long duration = System.currentTimeMillis() - start;

        // Collect data
        String method = request.getMethod();
        String path = request.getRequestURI();

        // If you have a way to get the username, use it; otherwise, default to "anonymous"
        String username = SecurityUtils.getCurrentUserLogin()
            .orElse("anonymous");
        String clientIp = request.getRemoteAddr();
        int status = wrappedResponse.getStatus();
        String reqBody = new String(wrappedRequest.getCachedBody());
        String resBody = new String(wrappedResponse.getCachedBody());

        // Save
//        TransactionLog log = new TransactionLog();
//        log.setMethod(method);
//        log.setPath(path);
//        log.setUsername(username);
//        log.setClientIp(clientIp);
//        log.setStatusCode(status);
//        log.setRequestBody(reqBody);
//        log.setResponseBody(resBody);
//        log.setTimestamp(Instant.now());
//
//        transactionRepository.save(log);

        // save transaction to database as async
//        transactionRepository.saveAsync(method, path, username, clientIp, status, reqBody, resBody, duration);

        wrappedResponse.copyBodyToResponse();
    }
}
