package com.project.techlabs.service;

import com.project.techlabs.dto.ProductDTO;
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

    public List<String> readCsvByProductId(Optional<List<String>> productIds) throws IOException {
        List<String> records = new ArrayList<>();
        List<String> productIdList = productIds.orElse(Collections.emptyList());

        String filePath = "/Users/emhaki/Desktop/product.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                /* values[0] 첫번째 인덱스 추출 */
                /*System.out.println(Arrays.toString(values));*/
                String productId = values[0].replaceAll("^\"|\"$", "");

                for (String id : productIdList) {
                    try {
                        int isAsNumber = Integer.parseInt(id.replaceAll("^\"|\"$", ""));
                        if (Integer.parseInt(productId) == isAsNumber) {
                            records.add(Arrays.toString(values));
                            logger.debug("Record added: " + Arrays.toString(values));

                        }
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("CSV-file Reading Error" + e.getMessage());
        }

        return records;
    }
}
