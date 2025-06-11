package com.inn.cafe.wrapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ProductWrapper {
    Integer id;

    String name;

    String description;

    Integer price;

    String status;

    Integer categoryId;

    String categoryName;

    public ProductWrapper(){

    }

    public ProductWrapper(Integer id, String name, String description, Integer price, String status, Integer categoryId, String categoryName) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.price = price;
        this.status = status;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
