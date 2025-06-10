    package com.inn.cafe.serviceImpl;

    import com.google.common.base.Strings;
    import com.inn.cafe.constants.CafeConstants;
    import com.inn.cafe.dao.UserDao;
    import com.inn.cafe.jwt.CustomerUserDetailsService;
    import com.inn.cafe.jwt.JwtFilter;
    import com.inn.cafe.jwt.JwtUtil;
    import com.inn.cafe.pojo.Users;
    import com.inn.cafe.service.PasswordResetService;
    import com.inn.cafe.service.UserService;
    import com.inn.cafe.utils.CafeUtils;
    import com.inn.cafe.utils.EmailUtils;
    import com.inn.cafe.wrapper.UserWrapper;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.*;

    @Slf4j
    @Service
    public class UserServiceImpl implements UserService {

        @Autowired
        UserDao userDao;

        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        CustomerUserDetailsService customerUserDetailsService;

        @Autowired
        JwtUtil jwtUtil;

        @Autowired
        JwtFilter jwtFilter;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        EmailUtils emailUtils;

        @Override
        public ResponseEntity<String> signUp(Map<String, String> requestMap) {
            log.info("Inside signup {}", requestMap);
            try {
                if (validateSignUpMap(requestMap)) {
                    Users user = userDao.findByEmailId(requestMap.get("email"));
                    if (Objects.isNull(user)) {
                        userDao.save(getUserFromMap(requestMap));
                        return CafeUtils.getResponseEntity(CafeConstants.SUCCESSFULLY_REGISTERED, HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity(CafeConstants.EMAIL_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        private boolean validateSignUpMap(Map<String, String> requestMap) {
            return requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email") && requestMap.containsKey("password");
        }

        private Users getUserFromMap(Map<String, String> requestMap) {
            Users user = new Users();
            user.setName(requestMap.get("name"));
            user.setContactNumber(requestMap.get("contactNumber"));
            user.setEmail(requestMap.get("email"));
//            user.setPassword(requestMap.get("password"));
            user.setPassword(passwordEncoder.encode(requestMap.get("password")));
            user.setStatus("false");
            user.setRole("user");

            return user;
        }

        @Override
        public ResponseEntity<String> login(Map<String, String> requestMap) {
            log.info( "Inside login. " );
            try {
                Authentication auth = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(requestMap.get( "email" ),requestMap.get( "password" ))
                );
                if ( auth.isAuthenticated() ){
                    if ( customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase( "true" ) ){
                        return new ResponseEntity<String>( "{\"token\":\"" + jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),customerUserDetailsService.getUserDetail().getRole()) + "\"}", HttpStatus.OK);
                    }else {
                        return new ResponseEntity<>( "{\"message\":\"" + "Wait for admin approval." + "\"}", HttpStatus.BAD_REQUEST );
                    }
                }
            } catch ( Exception ex ){
                log.error( "{}", ex );
            }
            return new ResponseEntity<>( "{\"message\":\"" + "Bad Credentials." + "\"}", HttpStatus.BAD_REQUEST );
        }

        @Override
        public ResponseEntity<List<UserWrapper>> getAllUsers() {
            try {
                if ( jwtFilter.isAdmin() ){
                    return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
                }else {
                    return new ResponseEntity<>( new ArrayList<>(), HttpStatus.UNAUTHORIZED );
                }
            }catch ( Exception ex ){
                ex.printStackTrace();
            }
            return new ResponseEntity<>( new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Override
        public ResponseEntity<String> update(Map<String, String> requestMap) {
            try {
                if ( jwtFilter.isAdmin() ) {
                    Optional<Users> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));

                    if ( optional.isPresent() ) {
                        userDao.updateStatus( requestMap.get( "status" ),  Integer.parseInt(requestMap.get("id")) );
                        sendMailToAllAdmin( requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin() );
                        return CafeUtils.getResponseEntity( CafeConstants.USER_UPDATED_SUCCESSFULLY, HttpStatus.OK );
                    } else {
                        return CafeUtils.getResponseEntity( CafeConstants.USER_ID_DOES_NOT_EXIST, HttpStatus.OK );
                    }

                } else {
                    return CafeUtils.getResponseEntity( CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED );
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        private void sendMailToAllAdmin(String status, String email, List<String> allAdmin) {
            allAdmin.remove( jwtFilter.getCurrentUser() );
            if ( status!=null && status.equalsIgnoreCase("true") ){
                emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved.", "USER:- "+email+ " \n is approved by \nADMIN:- "+jwtFilter.getCurrentUser(), allAdmin );
            } else {
                emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled.", "USER:- "+email+ " \n is disabled by \nADMIN:- "+jwtFilter.getCurrentUser(), allAdmin );
            }
        }

        @Override
        public ResponseEntity<String> checkToken() {
            return CafeUtils.getResponseEntity("true", HttpStatus.OK);
        }

        @Override
        public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
            try {
                Users userObj = userDao.findByEmail( jwtFilter.getCurrentUser() );
                if ( userObj != null ) {
                    if ( passwordEncoder.matches( requestMap.get("oldPassword"), userObj.getPassword() ) ){
                        userObj.setPassword( passwordEncoder.encode(requestMap.get("newPassword")) );
                        userDao.save(userObj);
                        return CafeUtils.getResponseEntity( CafeConstants.PASSWORD_UPDATED_SUCCESSFULLY, HttpStatus.OK );
                    }
                    return  CafeUtils.getResponseEntity( CafeConstants.INCORRECT_OLD_PASSWORD, HttpStatus.BAD_REQUEST );
                }
                return CafeUtils.getResponseEntity( CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR );
            } catch ( Exception ex ){
                ex.printStackTrace();
            }
            return CafeUtils.getResponseEntity( CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR );
        }

        @Override
        public ResponseEntity<String> changePasswordWithEmail(Map<String,String> requestMap) {
            // idéntica a changePassword, pero basada en email en lugar de token JWT
            try {
                Users user = userDao.findByEmail(requestMap.get("email"));
                user.setPassword(passwordEncoder.encode(requestMap.get("newPassword")));
                userDao.save(user);
                return CafeUtils.getResponseEntity(
                        CafeConstants.PASSWORD_UPDATED_SUCCESSFULLY, HttpStatus.OK
                );
            } catch (Exception ex) {
                return CafeUtils.getResponseEntity(
                        CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        }

    }
