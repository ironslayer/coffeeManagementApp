package com.inn.cafe.service;
//REVISAR -------------------------------------------------------------------------------------------------------------------

import java.util.Optional;

public interface PasswordResetService {
    /** Genera y guarda un token, y lo devuelve */
    String createTokenFor(String email);
    /** Valida un token y devuelve el email si sigue vigente */
    Optional<String> validate(String token);
}
// -------------------------------------------------------------------------------------------------------------------
