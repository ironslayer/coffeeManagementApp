package com.inn.cafe.pojo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column( name = "id" )
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column( name = "name")
    private String name;

    @Column( name = "contactNumber")
    private String contactNumber;

    @Column( name = "email")
    private String email;

    @Column( name = "password")
    private String password;

    @Column( name = "status", nullable = false, unique = true)
    private String status;

    @Column( name = "role")
    private String role;

}
