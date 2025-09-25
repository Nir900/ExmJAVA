package com.jjj.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();
    
    public void add(String token, long expiryMillis)
    {
        blacklist.put(token, System.currentTimeMillis() + expiryMillis);
    }

    public boolean isBlacklisted(String token)
    {
        Long expiry = blacklist.get(token);
        if (expiry == null)
            return false;
        
        if (expiry < System.currentTimeMillis()) {
            blacklist.remove(token);
            return false;
        }

        return true;
    }

    public void remove(String token)
    {
        blacklist.remove(token);
    }
}
