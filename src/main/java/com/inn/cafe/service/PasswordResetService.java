package com.inn.cafe.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PasswordResetService {

    ResponseEntity<String> forgotPassword(Map<String, String> requestMap );

    ResponseEntity<String> resetPassword(Map<String, String> requestMap );

}
