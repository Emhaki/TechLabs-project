package com.project.techlabs.service;

import com.project.techlabs.dto.ProductDTO;
import com.project.techlabs.dto.ProductData;
import com.project.techlabs.dto.ProductResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class CsvReaderService {

    private static final Logger logger = LoggerFactory.getLogger(CsvReaderService.class);

    public ProductData readCsvByProductId(Optional<List<String>> productIds) throws IOException {
        List<String> productIdList = productIds.orElse(Collections.emptyList());
        List<ProductDTO> targetList = new ArrayList<>();
        List<ProductResultDTO> resultsList = new ArrayList<>();
        ProductData productData = null;

        String productFilePath = "/Users/emhaki/Desktop/product.csv";
        String recFilePath = "/Users/emhaki/Desktop/rec.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(productFilePath))) {
            BufferedReader br2 = new BufferedReader(new FileReader(recFilePath));
            String line;
            String line2;
            while ((line2 = br2.readLine()) != null) {
                line = br.readLine();
                String[] productValues = line.split(",");
                String[] recValues = line2.split(",");
                /* values[0] 첫번째 인덱스 추출 */
                /*System.out.println(Arrays.toString(values));*/
                String productId = productValues[0].replaceAll("^\"|\"$", "");
                String recId = recValues[0].replaceAll("^\"|\"$", "");
                // 입력 받은 ID값 갯수에 따라 포문이 반복 -> 수정해야 함
                for (String id : productIdList) {
                    try {
                        int isAsNumber = Integer.parseInt(id.replaceAll("^\"|\"$", ""));
                        if (Integer.parseInt(productId) == isAsNumber) {

                            ProductDTO productDto = new ProductDTO();
                            productDto.setItem_id(productValues[0]);
                            productDto.setItem_name(productValues[1]);
                            productDto.setItem_image(productValues[2]);
                            productDto.setItem_url(productValues[3]);
                            productDto.setOriginal_price(productValues[4]);
                            productDto.setSale_price(productValues[5]);
                            targetList.add(productDto);

                            System.out.println(recValues[0]);
                            System.out.println(productValues[0]);
                            if (recValues[0].equals(productValues[0])) {
                                ProductResultDTO productResultDTO = new ProductResultDTO();
                                productResultDTO.setItem_id(recValues[0]);
                                productResultDTO.setItem_name(productValues[1]);
                                productResultDTO.setItem_image(productValues[2]);
                                productResultDTO.setItem_url(productValues[3]);
                                productResultDTO.setOriginal_price(productValues[4]);
                                productResultDTO.setSale_price(productValues[5]);
                                productResultDTO.setScore(recValues[2]);
                                productResultDTO.setRank(recValues[3]);
                                resultsList.add(productResultDTO);
                            }
                            /*records.add(Arrays.toString(values));/**/

                            logger.debug("Record added: " + Arrays.toString(productValues));
                        }
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number");
                    }
                }
                productData = new ProductData();
                productData.setTarget(targetList);
                productData.setResults(resultsList);
            }
        } catch (IOException e) {
            logger.error("CSV-file Reading Error" + e.getMessage());
        }

        return productData;
    }
}
