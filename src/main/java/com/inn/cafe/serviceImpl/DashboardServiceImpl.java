package com.inn.cafe.serviceImpl;

import com.inn.cafe.dao.BillDao;
import com.inn.cafe.dao.CaregoryDao;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    CaregoryDao caregoryDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    BillDao billDao;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String, Object> map = new HashMap<>();
        map.put( "category", caregoryDao.count() );
        map.put( "product", productDao.count() );
        map.put( "bill", billDao.count() );

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
