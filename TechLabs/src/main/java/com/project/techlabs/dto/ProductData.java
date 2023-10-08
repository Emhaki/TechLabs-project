package com.project.techlabs.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductData {
    private ProductDTO target;
    private List<ProductResultDTO> results = new ArrayList<>();
}
