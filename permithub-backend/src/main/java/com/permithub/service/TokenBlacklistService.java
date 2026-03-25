package com.permithub.service;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {
    
    // In-memory blacklist for now. In production, use Redis or DB.
    private final Set<String> blacklistedTokens = new HashSet<>();

    public void blacklistToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return blacklistedTokens.contains(token);
    }
}
