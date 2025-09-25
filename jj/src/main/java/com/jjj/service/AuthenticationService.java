package com.jjj.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jjj.dto.AuthResponse;
import com.jjj.entity.RefreshToken;
import com.jjj.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    
    public AuthResponse login(String username, String password, String ipAddress)
    {
        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = (UserDetails)authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());

            String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), roles);
            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(userDetails.getUsername(), ipAddress);

            return new AuthResponse(accessToken, refreshTokenEntity.getToken(), roles);
        } catch (AuthenticationException ex) {
            log.debug("Authentication failed for username='{}' : {}", username, ex.getMessage());
            throw new org.springframework.security.authentication.BadCredentialsException("Invalid username or password");
        }       
    }
}