package redswitch.greenledger.project.service;

import org.springframework.stereotype.Service;
import redswitch.greenledger.project.model.BlacklistedToken;
import redswitch.greenledger.project.model.JwtUtil;
import redswitch.greenledger.project.repository.BlacklistedTokenRepository;

import java.time.Instant;
import java.util.Date;

@Service
public class TokenBlacklistService {
    private final JwtUtil jwtUtil;
    private BlacklistedTokenRepository blacklistedTokenRepository;
    public  TokenBlacklistService(BlacklistedTokenRepository blacklistedTokenRepository,JwtUtil jwtUtil){
        this.blacklistedTokenRepository=blacklistedTokenRepository;
        this.jwtUtil=jwtUtil;
    }

    public Date extractExpiration(String token) {
        return jwtUtil.extractAllClaims(token).getExpiration();
    }
    public void blacklistToken(String token) {
        Instant expiry = extractExpiration(token).toInstant();
        BlacklistedToken bt = new BlacklistedToken(token, expiry);
        blacklistedTokenRepository.save(bt);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}
