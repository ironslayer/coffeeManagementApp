package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.jwt.JwtFilter;
import com.inn.cafe.pojo.Category;
import com.inn.cafe.pojo.Product;
import com.inn.cafe.service.ProductService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if ( jwtFilter.isAdmin() ) {
                if ( validateProductMap(requestMap, false) ){
                    productDao.save( getProductFromMap( requestMap, false ) );
                    return CafeUtils.getResponseEntity( CafeConstants.PRODUCT_ADDED_SUCCESSFULLY, HttpStatus.OK );
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

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if ( requestMap.containsKey("name") ){ // TODO: Se puede validar más cosas?
            if ( requestMap.containsKey( "id" ) && validateId ){
                return true;
            } else if ( !validateId ) {
                return true;
            }
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Category category = new Category();
        category.setId( Integer.parseInt( requestMap.get("categoryId") ) );

        Product product = new Product();

        if ( isAdd ) {
            product.setId( Integer.parseInt( requestMap.get( "id" ) ) );
        } else {
            product.setStatus( "true" );
        }

        product.setCategory( category );
        product.setName( requestMap.get( "name" ) );
        product.setDescription( requestMap.get( "description" ) );
        product.setPrice( Integer.parseInt( requestMap.get( "price" )  ) );

        return product;
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);

        } catch ( Exception ex ){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if ( jwtFilter.isAdmin() ) {
                if ( validateProductMap(requestMap, true) ){
                    Optional<Product> optional =  productDao.findById( Integer.parseInt( requestMap.get("id") ) );
                    if (optional.isPresent()){
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus( optional.get().getStatus() );
                        productDao.save(product);
                        return CafeUtils.getResponseEntity( CafeConstants.PRODUCT_UPDATED_SUCCESSFULLY, HttpStatus.OK );
                    } else {
                        return CafeUtils.getResponseEntity( CafeConstants.PRODUCT_DOES_NOT_EXIST, HttpStatus.OK );
                    }
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

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            if ( jwtFilter.isAdmin() ) {
                Optional<Product> optional = productDao.findById( id );
                if ( optional.isPresent() ) {
                    productDao.deleteById( id );
                    return CafeUtils.getResponseEntity( CafeConstants.PRODUCT_DELETED_SUCCESSFULLY, HttpStatus.OK );
                } else {
                    return CafeUtils.getResponseEntity( CafeConstants.PRODUCT_DOES_NOT_EXIST, HttpStatus.OK );
                }
            } else {
                return CafeUtils.getResponseEntity( CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED );
            }
        } catch ( Exception ex ){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try {
            if ( jwtFilter.isAdmin() ) {
                Optional<Product> optional = productDao.findById( Integer.parseInt( requestMap.get("id") ) );
                if (optional.isPresent()){
                    productDao.updateProductStatus( requestMap.get( "status" ), Integer.parseInt( requestMap.get( "id" ) ) );
                    return CafeUtils.getResponseEntity( CafeConstants.PRODUCT_UPDATED_STATUS_SUCCESSFULLY, HttpStatus.OK );
                } else {
                    return CafeUtils.getResponseEntity( CafeConstants.PRODUCT_DOES_NOT_EXIST, HttpStatus.OK );
                }

            } else {
                return CafeUtils.getResponseEntity( CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED );
            }

        } catch ( Exception ex ){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductByCategory( id ), HttpStatus.OK);
        } catch ( Exception ex ){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getById(Integer id) {
        try {
            return new ResponseEntity<>(productDao.getProductById( id ), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>( new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR );
    }

}
