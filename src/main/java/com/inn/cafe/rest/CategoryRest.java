package com.inn.cafe.rest;

import com.inn.cafe.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping( path = "/category")
public interface CategoryRest {

    @PostMapping( path = "/add")
    ResponseEntity<String> addNewCategory( @RequestBody( required = true) Map<String, String> requestMap );

    @GetMapping( path = "/get")
    public ResponseEntity<List<Category>> getAllCategory( @RequestParam( required = false ) String filterValue );

    @PostMapping( path = "/update")
    public ResponseEntity<String> updateCategory( @RequestBody( required = true) Map<String, String> requestMap );

}
