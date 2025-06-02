package com.inn.cafe.dao;

import com.inn.cafe.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<Users, Integer> {

    Users findByEmailId(@Param("email") String email);


}
