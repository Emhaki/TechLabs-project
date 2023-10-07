package com.project.techlabs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techlabs.dto.ProductDTO;
import com.project.techlabs.dto.ProductData;
import com.project.techlabs.service.CsvReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private CsvReaderService csvReaderService;

    private ObjectMapper objectMapper = new ObjectMapper();
//    private ProductService productService;

    @GetMapping("/rec")
    public ResponseEntity<String> getProductId(@RequestParam("id") Optional<List<String>> productIds) throws Exception {
        ProductData result = null;
        System.out.println(productIds);

        if (productIds.isPresent()) {
            try {
                result = csvReaderService.readCsvByProductId(productIds);
                String json = objectMapper.writeValueAsString(result);
                System.out.println(json);
                return ResponseEntity.ok(json);
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Response");
            }
        } else {
            return ResponseEntity.badRequest().body("Not Found Product Id");
        }
    }
}
