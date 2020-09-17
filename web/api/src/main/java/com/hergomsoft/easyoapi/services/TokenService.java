package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Token;
import com.hergomsoft.easyoapi.repositories.TokenRepository;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService implements ITokenService {
    
    @Autowired
    private TokenRepository tokenRepository;
    
    
    private final int TOKEN_BYTES = 36;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Override
    public Token getToken(String token) {
        Optional<Token> t =  tokenRepository.findByToken(token);
        return (t.isPresent()) ? t.get() : null;
    }

    @Override
    public Token guardaToken(Token token) {
        return tokenRepository.save(token);
    }
    
    @Override
    public void borraToken(Token token) {
        tokenRepository.delete(token);
    }

    @Override
    public String generaToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return base64Encoder.encodeToString(bytes);
    }

}
