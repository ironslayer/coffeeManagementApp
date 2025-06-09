package com.inn.cafe.restImpl;

import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.rest.UserRest;
import com.inn.cafe.service.PasswordResetService;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserRestImpl implements UserRest {
//REVISAR -------------------------------------------------------------------------------------------------------------------

    @Autowired
    private PasswordResetService resetService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailUtils emailUtils;
// -------------------------------------------------------------------------------------------------------------------


    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        try{
            return userService.signUp(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            return userService.login( requestMap );
        }catch ( Exception ex ){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            return userService.getAllUsers();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>( new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            return userService.update( requestMap );
        }catch ( Exception ex ){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        try {
            return userService.checkToken();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            return userService.changePassword(requestMap);
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
//REVISAR -------------------------------------------------------------------------------------------------------------------
//    @Override
//    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
//        try {
//            return userService.forgotPassword(requestMap);
//        } catch ( Exception ex ){
//            ex.printStackTrace();
//        }
//        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
//
//    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        String email = requestMap.get("email");
        String token = resetService.createTokenFor(email);
        String link = "http://localhost:4200" + "/reset-password?token=" + token;
        try {
            emailUtils.sendPasswordResetMail(email, link);
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
        String token = requestMap.get("token");
        String newPass = requestMap.get("newPassword");
        Optional<String> maybeEmail = resetService.validate(token);
        if (maybeEmail.isEmpty()) {
            return CafeUtils.getResponseEntity("Token inválido o expirado", HttpStatus.BAD_REQUEST);
        }
        // reutilizas tu UserService para cambiar la clave:
        Map<String,String> req = Map.of(
                "email", maybeEmail.get(),
                "newPassword", newPass
        );
        return userService.changePasswordWithEmail(req);
    }
}
//-------------------------------------------------------------------------------------------------------------------
