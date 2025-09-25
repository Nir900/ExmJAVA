package com.jjj.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjj.dto.AuthResponse;
import com.jjj.dto.RefreshRequest;
import com.jjj.entity.RefreshToken;
import com.jjj.service.AuthenticationService;
import com.jjj.service.CustomUserDetailsService;
import com.jjj.service.RefreshTokenService;
import com.jjj.service.TokenBlacklistService;
import com.jjj.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService blacklistService;

    public AuthenticationController(
        AuthenticationService authenticationService,
        RefreshTokenService refreshTokenService,
        JwtUtil jwtUtil,
        CustomUserDetailsService userDetailsService,
        TokenBlacklistService blacklistService
    ) {
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.blacklistService = blacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody Map<String, String> body, HttpServletRequest request)
    {
        String username = body.get("username");
        String password = body.get("password");
        String ipAddress = request.getRemoteAddr();

        AuthResponse response = authenticationService.login(username, password, ipAddress);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request, HttpServletRequest httpRequest)
    {
        String reqToken = request.refreshToken();
        String clientIp = httpRequest.getRemoteAddr();

        Optional<RefreshToken> tokenOpt = refreshTokenService.validateRefreshToken(reqToken, clientIp);

        if (tokenOpt.isEmpty())
            return ResponseEntity.status(401).body(Map.of(
                "error", "invalid_refresh_token",
                "message", "Refresh token expired, revoked, or IP mismatch"
            ));
        
        RefreshToken stored = tokenOpt.get();
        refreshTokenService.revoke(stored);


        UserDetails userDetails = userDetailsService.loadUserByUsername(stored.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
            .map(a -> a.getAuthority())
            .collect(Collectors.toList());

        String newAccess = jwtUtil.generateAccessToken(userDetails.getUsername(), roles);
        RefreshToken newRefresh = refreshTokenService.createRefreshToken(userDetails.getUsername(), clientIp);
        
        return ResponseEntity.ok(new AuthResponse(newAccess, newRefresh.getToken(), roles));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body, HttpServletRequest request)
    {
        String username = body.get("username");
        refreshTokenService.revokeAllForUser(username);

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer")) {
            String accessToken = token.substring(7);
            long ttl = jwtUtil.getAllClaims(accessToken).getExpiration().getTime() - System.currentTimeMillis();
            if (ttl > 0)
                blacklistService.add(accessToken, ttl);
        }
        
        return ResponseEntity.ok("Logged out");
    }
}
