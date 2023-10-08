package com.project.techlabs.service;

import com.project.techlabs.dto.ProductDTO;
import com.project.techlabs.dto.ProductData;
import com.project.techlabs.dto.ProductResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class CsvReaderService {

    private static final Logger logger = LoggerFactory.getLogger(CsvReaderService.class);

    // productIds 값을 여러개 받을 수 있도록 Optional로 처리
    public ProductData readCsvByProductId(String productIds) throws IOException {
        ProductData productData = new ProductData();
        List<ProductDTO> targetList = new ArrayList<>();
        List<ProductResultDTO> resultList = new ArrayList<>();
        String productFilePath = "/Users/emhaki/Desktop/product.csv";
        String recFilePath = "/Users/emhaki/Desktop/rec.csv";

        // 1. while문으로 recFilePath 엑셀을 쭉 돌면서 recId값과 productIds값이 일치하는 것을 DTO에 담기
        String recLine;
        String productLine;
        String productId = productIds.replaceAll("^\"|\"$", "");

        try {
            BufferedReader recBr = new BufferedReader(new FileReader(recFilePath));
            BufferedReader productBr = new BufferedReader(new FileReader(productFilePath));
            String[] productValues = null;
            String[] recValues = null;

            while ((productLine = productBr.readLine()) != null) {

                productValues = productLine.split(",");

                // replaceAll을 통해 제거
                // 입력받은 productIdList에 recValues[0]값이 들어있다면 아래 로직 실행

                /*if recValues[0].replaceAll("^\"|\"$", ""))*/
                if (productId.contains(productValues[0].replaceAll("^\"|\"$", ""))) {
                    ProductDTO productDto = new ProductDTO();
                    productDto.setItem_id(productValues[0]);
                    productDto.setItem_name(productValues[1]);
                    productDto.setItem_image(productValues[2]);
                    productDto.setItem_url(productValues[3]);
                    productDto.setOriginal_price(productValues[4]);
                    productDto.setSale_price(productValues[5]);

                    targetList.add(productDto);
                    /*productData.setTarget(targetList);
                    productData.getTarget().add(productDto);*/
                    {
                        try {
                            while ((recLine = recBr.readLine()) != null) {
                                recValues = recLine.split(",");
                                String recId = recValues[0].replaceAll("^\"|\"$", "");
                                if (recId.equals(productId)) {
                                    System.out.println("여기 오긴옴");

                                    ProductResultDTO productResultDTO = new ProductResultDTO();
                                    productResultDTO.setItem_id(recValues[1]);
                                    productResultDTO.setItem_name(productValues[1]);
                                    productResultDTO.setItem_image(productValues[2]);
                                    productResultDTO.setItem_url(productValues[3]);
                                    productResultDTO.setOriginal_price(productValues[4]);
                                    productResultDTO.setSale_price(productValues[5]);
                                    productResultDTO.setScore(recValues[2]);
                                    productResultDTO.setRank(recValues[3]);
                                    resultList.add(productResultDTO);

                                    /*productData.setResults(resultsList);
                                    productData.getResults().add(productResultDTO);*/

                                }
                            }
                        } catch (NumberFormatException e) {
                            logger.error("Invalid number");
                        }
                    }
                }
            }
            productData.setTarget(targetList);
            productData.setResults(resultList);
        } catch (IOException e) {
            logger.error("CSV-file Reading Error" + e.getMessage());
        }

        return productData;
    }
}
