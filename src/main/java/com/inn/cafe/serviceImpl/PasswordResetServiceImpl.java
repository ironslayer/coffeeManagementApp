package com.inn.cafe.serviceImpl;

import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.PasswordResetTokenRepository;
import com.inn.cafe.pojo.PasswordResetToken;
import com.inn.cafe.pojo.Users;
import com.inn.cafe.service.PasswordResetService;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepo;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private UserService userService;


    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            String email = requestMap.get( "email" );
            String token = createTokenFor( email );
            String link = "http://localhost:4200" + "/reset-password?token=" + token; //TODO: REFACTORIZE
            emailUtils.sendPasswordResetMail( email, link );
            return CafeUtils.getResponseEntity(
                    CafeConstants.CHECK_YOUR_EMAIL_FOR_CREDENTIALS, HttpStatus.OK
            );
        } catch (Exception ex) {
            return CafeUtils.getResponseEntity(
                    CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<String> resetPassword(Map<String, String> requestMap) {
        try {
            String token = requestMap.get( "token" );
            String newPass = requestMap.get( "newPassword" );
            Optional<String> maybeEmail = validate( token );
            if ( maybeEmail.isEmpty() ){
                return CafeUtils.getResponseEntity( CafeConstants.INVALID_TOKEN, HttpStatus.BAD_REQUEST );
            }
            Map<String, String> req = Map.of(
                "email", maybeEmail.get(),
                "newPassword", newPass
            );
            return userService.changePasswordWithEmail( req );
        } catch (Exception ex) {
            return CafeUtils.getResponseEntity(
                    CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }

    private String createTokenFor(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);
        PasswordResetToken prt = new PasswordResetToken(email, token, expiry);
        tokenRepo.save(prt);
        return token;
    }

    private Optional<String> validate(String token) {
        PasswordResetToken prt = tokenRepo.findByToken(token);
        if (prt == null || prt.getExpiry().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }
        tokenRepo.deleteByToken(token); // BORRA EL TOKEN
        return Optional.of(prt.getEmail());
    }
}
