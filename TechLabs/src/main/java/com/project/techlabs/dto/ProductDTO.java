package com.project.techlabs.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

    private String item_id;
    private String item_name;
    private String item_image;
    private String item_url;
    private String original_price;
    private String sale_price;
}
