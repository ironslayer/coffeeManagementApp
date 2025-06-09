package com.inn.cafe.serviceImpl;
//REVISAR -------------------------------------------------------------------------------------------------------------------

import com.inn.cafe.dao.PasswordResetTokenRepository;
import com.inn.cafe.pojo.PasswordResetToken;
import com.inn.cafe.service.PasswordResetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Override
    public String createTokenFor(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        PasswordResetToken prt = new PasswordResetToken(email, token, expiry);
        tokenRepo.save(prt);
        return token;
    }

    @Override
    public Optional<String> validate(String token) {
        PasswordResetToken prt = tokenRepo.findByToken(token);
        if (prt == null || prt.getExpiry().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }
        // opcionalmente, podés borrar el token para que sea de un solo uso:
        tokenRepo.deleteByToken(token);
        return Optional.of(prt.getEmail());
    }
}
// -------------------------------------------------------------------------------------------------------------------
