package com.project.techlabs.controller;

import com.project.techlabs.dto.ProductDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class ProductController {

    @GetMapping("rec/{id}")
    public ProductDTO getProductId(@PathVariable("id") Optional<Long> productId) {

        return null;
    }
}
