package com.inn.cafe.dao;

import com.inn.cafe.pojo.Users;
import com.inn.cafe.wrapper.UserWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends JpaRepository<Users, Integer> {

    Users findByEmailId(@Param("email") String email);

    List<UserWrapper> getAllUsers();

    List<String> getAllAdmin();

    @Transactional
    @Modifying
    Integer updateStatus( @Param( "status" ) String status, @Param( "id" ) Integer id );

    Users findByEmail( String email );

}
