    package com.inn.cafe.serviceImpl;

    import com.inn.cafe.constants.CafeConstants;
    import com.inn.cafe.dao.UserDao;
    import com.inn.cafe.jwt.CustomerUserDetailsService;
    import com.inn.cafe.jwt.JwtUtil;
    import com.inn.cafe.pojo.Users;
    import com.inn.cafe.service.UserService;
    import com.inn.cafe.utils.CafeUtils;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.Map;
    import java.util.Objects;

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
        private PasswordEncoder passwordEncoder;

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

    }
