package com.esoft.web.filter;

import com.esoft.domain.TokenHistory;
import com.esoft.domain.Transaction;
import com.esoft.repository.TransactionRepository;
import com.esoft.security.SecurityUtils;
import com.esoft.service.TokenHistoryService;
import com.esoft.service.TransactionService;
import com.esoft.service.dto.TransactionDTO;
import com.esoft.utils.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class TransactionLogFilter extends OncePerRequestFilter {
    private static final String API_PREFIX = "/api/";

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TokenHistoryService tokenHistoryService;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!path.startsWith(API_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
        CachedBodyHttpServletResponse wrappedResponse = new CachedBodyHttpServletResponse(response);

        // TODO: Do filter with specific request action

        long start = System.currentTimeMillis();
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        long duration = System.currentTimeMillis() - start;

        String authToken = extractTokenFromRequest(wrappedRequest);
        Long tokenId = getTokenIdFromTokenHistory(authToken);
        String method = request.getMethod();

        Long userId = SecurityUtils.getCurrentUserId().orElse(0L);
        String username = SecurityUtils.getCurrentUserLogin()
            .orElse("anynomous");
        String clientIp = request.getRemoteAddr();
        int status = wrappedResponse.getStatus();
//        String reqBody = new String(wrappedRequest.getCachedBody());
//        String resBody = new String(wrappedResponse.getCachedBody());

        saveTransactionLogAsync(method, path, userId, tokenId, username, clientIp, status, duration);
        wrappedResponse.copyBodyToResponse();
    }

    private Long getTokenIdFromTokenHistory(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return tokenHistoryService.findOneByHashedToken(jwtUtil.hashToken(token))
            .map(TokenHistory::getId)
            .orElse(null);
    }
    private String extractTokenFromRequest(CachedBodyHttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void saveTransactionLogAsync(String method, String path, Long userId, Long tokenId, String username,
                                      String clientIp, int status, long duration) {
        TransactionDTO log = new TransactionDTO();
        log.setRequestMethod(method);
        log.setRequestPath(path);
        log.setTokenHistoryId(tokenId);
        log.setUserId(userId);
        log.setUsername(username);
        log.setClientIp(clientIp);
        log.setStatus(status);
        log.setDuration(duration);
        log.setCreatedDate(Instant.now());
        transactionService.saveAsync(log);
    }
}
