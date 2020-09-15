package com.hergomsoft.easyoapi.services;

import com.hergomsoft.easyoapi.models.Token;
import com.hergomsoft.easyoapi.repositories.TokenRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService implements ITokenService {
    
    @Autowired
    private TokenRepository tokenRepository;

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

}
