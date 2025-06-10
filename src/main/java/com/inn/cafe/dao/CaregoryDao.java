package com.inn.cafe.dao;

import com.inn.cafe.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaregoryDao extends JpaRepository<Category, Integer> {

    List<Category> getAllCategory();

}
