package com.jjj.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.jjj.service.RefreshTokenService;
import com.jjj.service.TokenBlacklistService;
import com.jjj.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenBlacklistService blacklistService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            long expiryMillis = jwtUtil.getAllClaims(accessToken).getExpiration().getTime();
            long ttl = expiryMillis - System.currentTimeMillis();
            if (ttl > 0)
                blacklistService.add(accessToken, ttl);
        }

        String refreshToken = request.getHeader("X-Refresh-Token");
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.findByToken(refreshToken).ifPresent(rt -> {
                refreshTokenService.revoke(rt);
            });
        }
    }
}
