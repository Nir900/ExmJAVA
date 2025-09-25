package com.jjj.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jjj.entity.RefreshToken;
import com.jjj.repository.RefreshTokenRepository;
import com.jjj.util.JwtProperties;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final int refreshDays;
    private final TokenBlacklistService blacklist;

    public RefreshTokenService(
        RefreshTokenRepository repo,
        JwtProperties properties,
        TokenBlacklistService blacklist
    ) {
        this.repo = repo;
        this.refreshDays = properties.getRefreshExpirationDays();
        this.blacklist = blacklist;
    }

    public RefreshToken createRefreshToken(String username, String ipAddress)
    {
        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString() + "." + UUID.randomUUID().toString());
        token.setUsername(username);
        token.setExpiryDate(Instant.now().plus(refreshDays, ChronoUnit.DAYS));
        token.setIpAddress(ipAddress);
        token.setRevoked(false);

        return repo.save(token);
    }

    public Optional<RefreshToken> validateRefreshToken(String token, String requestIp)
    {
        Optional<RefreshToken> tokenOpt = repo.findByToken(token);
        if (tokenOpt.isEmpty())
            return Optional.empty();
        
        RefreshToken rt = tokenOpt.get();

        if (rt.isRevoked() || isExpired(rt) || !rt.getIpAddress().equals(requestIp)) {
            revoke(rt);
            return Optional.empty();
        }

        return tokenOpt;
    }

    public Optional<RefreshToken> findByToken(String token)
    {
        return repo.findByToken(token);
    }

    @Transactional
    public void revoke(RefreshToken token)
    {
        long expiry = token.getExpiryDate().toEpochMilli() - System.currentTimeMillis();
        blacklist.add(token.getToken(), expiry > 0 ? expiry : 0);
        token.setRevoked(true);
        repo.save(token);
    }

    @Transactional
    public void revokeAllForUser(String username)
    {
        repo.deleteByUsername(username);
    }

    public boolean isExpired(RefreshToken token)
    {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
