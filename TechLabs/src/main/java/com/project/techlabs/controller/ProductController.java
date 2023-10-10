package com.project.techlabs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techlabs.dto.ProductData;
import com.project.techlabs.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class ProductController {

    @Autowired
    private ProductService csvReaderService;

    private ObjectMapper objectMapper = new ObjectMapper();
//    private ProductService productService;

    @GetMapping("/rec")
    public ResponseEntity<List<String>> getProductId(@RequestParam("id") Optional<List<String>> productIds) throws Exception {
        ProductData result = null;
        List<String> resultData = new ArrayList<>();

        if (productIds.isPresent()) {
            try {
                for (String id : productIds.orElse(Collections.emptyList())) {

                    result = csvReaderService.readCsvByProductId(id);
                    String json = objectMapper.writeValueAsString(result);
                    resultData.add(json.replace("\\", ""));
                }
                return ResponseEntity.ok(resultData);
            } catch (JsonProcessingException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList("Error Response"));
            }
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonList("Not Found Product Id"));
        }
    }

    @PostMapping("/rec/insert")
    public ResponseEntity<String> insertCsvData(@RequestParam Map<String, Object> pramMap) throws Exception {
        csvReaderService.insertCsvData(pramMap);

        return ResponseEntity.ok("Success");
    }

    @PostMapping("/rec/update")
    public ResponseEntity<String> updateCsvData(@RequestParam Map<String, Object> pramMap) throws Exception {
        csvReaderService.updateCsvData(pramMap);

        return ResponseEntity.ok("Success");
    }
}
