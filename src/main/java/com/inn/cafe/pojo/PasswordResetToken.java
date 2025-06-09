package com.inn.cafe.pojo;
//REVISAR -------------------------------------------------------------------------------------------------------------------

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class PasswordResetToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(unique = true)
    private String token;

    private LocalDateTime expiry;

    public PasswordResetToken() {}

    public PasswordResetToken(String email, String token, LocalDateTime expiryDate) {
        this.email = email;
        this.token = token;
        this.expiry = expiryDate;
    }
}
// -------------------------------------------------------------------------------------------------------------------
