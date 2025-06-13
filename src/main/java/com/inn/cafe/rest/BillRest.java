package com.inn.cafe.rest;

import com.inn.cafe.pojo.Bill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping( path = "/bill")
public interface BillRest {

    @PostMapping( path = "/generate-report")
    ResponseEntity<String> generateReport(@RequestBody Map<String, Object> requestMap );

    @GetMapping( path = "/get-bills")
    ResponseEntity<List<Bill>> getBills();

    @PostMapping( path = "/generate-pdf")
    ResponseEntity<byte[]> getPdf( @RequestBody Map< String, Object > requestMap );

    @PostMapping( path = "/delete/{id}")
    ResponseEntity<String> deleteBill(@PathVariable Integer id);

}
