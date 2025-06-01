package com.inn.cafe.dao;

import com.inn.cafe.pojo.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<Users, Integer> {
}
