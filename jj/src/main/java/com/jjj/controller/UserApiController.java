package com.jjj.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjj.service.CustomUserDetailsService;
import com.jjj.util.JwtUtil;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public UserApiController(CustomUserDetailsService uds, JwtUtil jwtUtil) {
        this.userDetailsService = uds;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body("Unauthorized");

        String username = auth.getName();
        var userDetails = userDetailsService.loadUserByUsername(username);

        return ResponseEntity.ok(Map.of(
            "username", userDetails.getUsername(),
            "roles", userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        ));
    }
}