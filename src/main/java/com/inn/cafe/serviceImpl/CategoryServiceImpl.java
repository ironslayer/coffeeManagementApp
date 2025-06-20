package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.CaregoryDao;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.pojo.Category;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CaregoryDao caregoryDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if ( jwtFilter.isAdmin() ) {
                if ( validateCategoryMap(requestMap, false) ){
                    caregoryDao.save( getCategoryFromMap( requestMap, false ) );
                    return CafeUtils.getResponseEntity( CafeConstants.CATEGORY_ADDED_SUCCESSFULLY, HttpStatus.OK );
                }
                return CafeUtils.getResponseEntity( CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST );
            } else {
                return CafeUtils.getResponseEntity( CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED );
            }

        } catch ( Exception ex ){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if ( requestMap.containsKey("name") ){
            if ( requestMap.containsKey( "id" ) && validateId ){
                return true;
            } else if ( !validateId ) {
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap( Map< String, String > requestMap, Boolean isAdd) {
        Category category = new Category();
        if ( isAdd ) {
            category.setId( Integer.parseInt( requestMap.get( "id" ) ) );
        }
        category.setName(requestMap.get( "name" ));
        return category;
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if ( !Strings.isNullOrEmpty( filterValue ) && filterValue.equalsIgnoreCase( "true" ) ){
                log.info( "Inside if (getAllCategory)" );
                return new ResponseEntity<List<Category>>( caregoryDao.getAllCategory(), HttpStatus.OK );
            }
            return new ResponseEntity<>(caregoryDao.findAll(), HttpStatus.OK);
        } catch ( Exception ex ){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
           if ( jwtFilter.isAdmin() ) {
               if ( validateCategoryMap( requestMap, true ) ){
                   Optional optional = caregoryDao.findById( Integer.parseInt( requestMap.get("id") ) );
                   if (optional.isPresent()){
                       caregoryDao.save( getCategoryFromMap( requestMap, true ) );
                       return CafeUtils.getResponseEntity( CafeConstants.CATEGORY_UPDATED_SUCCESSFULLY, HttpStatus.OK );
                   } else {
                       return CafeUtils.getResponseEntity( CafeConstants.CATEGORY_DOES_NOT_EXIST, HttpStatus.OK );
                   }
               }
               return CafeUtils.getResponseEntity( CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST );
           } else {
               return CafeUtils.getResponseEntity( CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED );
           }

        } catch ( Exception ex ){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity( CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR );
    }

}
