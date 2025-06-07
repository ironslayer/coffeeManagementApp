package com.inn.cafe.pojo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Users.findByEmailId", query = "select u from Users u where u.email=:email")

@NamedQuery(name = "Users.getAllUsers", query = "select new com.inn.cafe.wrapper.UserWrapper(u.id,u.name,u.email,u.contactNumber,u.status) from Users u where u.role='user'")

@NamedQuery(name = "Users.updateStatus", query = "update Users u set u.status=:status where u.id=:id")

@NamedQuery(name = "Users.getAllAdmin", query = "select u.email from Users u where u.role='admin'")



@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status", nullable = false /*, unique = true*/)
    private String status;

    @Column(name = "role")
    private String role;

}
