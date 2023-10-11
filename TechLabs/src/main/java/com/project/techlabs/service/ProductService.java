package com.project.techlabs.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.project.techlabs.dto.ProductDTO;
import com.project.techlabs.dto.ProductData;
import com.project.techlabs.dto.ProductResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String productFilePath = "/Users/emhaki/Desktop/product.csv";
    private static final String recFilePath = "/Users/emhaki/Desktop/rec.csv";

    /*
     * author: emhaki
     * date: 2023.10.06
     * description: parameter로 요청 온 product_id값으로 csv파일 탐색
     * */
    public ProductData readCsvByProductId(String productIds) throws IOException {

        ProductData productData = new ProductData();
        ProductDTO productDto = new ProductDTO();
        ProductResultDTO productResultDTO = new ProductResultDTO();
        List<ProductResultDTO> resultList = new ArrayList<>();

        try {
            List<String[]> productList = readCsv("product");
            List<String[]> recList = readCsv("rec");
            for (String[] productArray : productList) {
                /*System.out.println(Arrays.toString(productArray));*/
                if (productArray[0].replaceAll("^\"|\"$", "").equals(productIds)) {

                    productDto.setItem_id(productArray[0]);
                    productDto.setItem_name(productArray[1]);
                    productDto.setItem_image(productArray[2]);
                    productDto.setItem_url(productArray[3]);
                    productDto.setOriginal_price(productArray[4]);
                    productDto.setSale_price(productArray[5]);
                    productData.setTarget(productDto);
                }
            }

            List<String[]> recValues = new ArrayList<>();
            for (String[] recArray : recList) {
                if (recArray[0].replaceAll("^\"|\"$", "").equals(productIds)) {
                    recValues.add((recArray));
                }
            }

            for (String[] recData : recValues) {
                for (String[] productArray : productList) {
                    if (recData[1].replaceAll("^\"|\"$", "").equals(productArray[0].replaceAll("^\"|\"$", ""))) {
                        productResultDTO = new ProductResultDTO();
                        productResultDTO.setItem_id(recData[1]);
                        productResultDTO.setItem_name(productArray[1]);
                        productResultDTO.setItem_image(productArray[2]);
                        productResultDTO.setItem_url(productArray[3]);
                        productResultDTO.setOriginal_price(productArray[4]);
                        productResultDTO.setSale_price(productArray[5]);
                        productResultDTO.setScore(recData[2]);
                        productResultDTO.setRank(recData[3]);
                        resultList.add(productResultDTO);
                    }
                }
            }
            productData.setResults(resultList);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return productData;
    }

    /*
    * author: emhaki
    * date: 2023.10.08
    * description: Csv 데이터 insert
    * */
    public void insertCsvData (@RequestParam Map<String, Object> paramMap) throws Exception {

        try {
            // product csv 파일의 행 == 6
            if (paramMap.size() == 6) {
                String kindOfFileName = "product";
                List<String[]> csvData = readCsv(kindOfFileName);

                String[] productRow = {
                        (String) paramMap.get("item_id"),
                        (String) paramMap.get("item_name"),
                        (String) paramMap.get("item_image"),
                        (String) paramMap.get("item_url"),
                        (String) paramMap.get("original_price"),
                        (String) paramMap.get("sale_price")
                };
                csvData.add(productRow);
                insertCsv(csvData, kindOfFileName);
            } else if (paramMap.size() == 4) {
                String kindOfFileName = "rec";
                List<String[]> csvData = readCsv(kindOfFileName);

                String[] recRow = {
                        (String) paramMap.get("rec_id"),
                        (String) paramMap.get("item_id"),
                        (String) paramMap.get("score"),
                        (String) paramMap.get("rank")
                };
                csvData.add(recRow);
                insertCsv(csvData, kindOfFileName);
            } else {
                throw new IllegalArgumentException("Invalid paramMap size. Expected 6 or 4, but got: " + paramMap.size());
            }
        } catch (NullPointerException e) {
            logger.error("NullPointerException: " + e.getMessage());
        }
    }
    /*
     * author: emhaki
     * date: 2023.10.10
     * description: Csv 데이터 update
     * */
    public void updateCsvData(@RequestParam Map<String, Object> paramMap) throws Exception {

        try {
            if (paramMap.size() == 7) {
                String kindOfFileName = "product";
                updateCsv(paramMap, kindOfFileName);

            } else if (paramMap.size() == 5) {
                    String kindOfFileName = "rec";
                    updateCsv(paramMap, kindOfFileName);
            } else {
                throw new IllegalArgumentException("Invalid paramMap size. Expected 7 or 5, but got: " + paramMap.size());
            }

        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
    }

    /*
     * author: emhaki
     * date: 2023.10.10
     * description: Csv 데이터 delete
     * */
    public void deleteCsvData(@RequestParam("search_product_id") String searchProductId,
                            @RequestParam("kindOfFileName") String kindOfFileName) throws Exception {

        List<String[]> csvData = readCsv(kindOfFileName);
        // 검색한 search_product_id에 해당하는 열 삭제
        try {
            if (kindOfFileName.equals("product")) {
                csvData.removeIf(row -> row[0].equals(searchProductId));
            } else if (kindOfFileName.equals("rec")) {
                csvData.removeIf(row -> row.length > 1 && row[1].equals(searchProductId));
            } else {
                throw new IllegalArgumentException("Invalid FileName: " + kindOfFileName);
            }

            insertCsv(csvData , kindOfFileName);
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
    }

    /*
     * author: emhaki
     * date: 2023.10.10
     * description: Csv 파일 데이터를 리스트에 저장
     * */
    public List<String[]> readCsv(String kindOfFileName) throws Exception {
        List<String[]> csvData = new ArrayList<>();
        CSVReader csvReader = null;
        try {
            if (kindOfFileName.equals("product")) {
                csvReader = new CSVReader(new FileReader(productFilePath));
            } else if (kindOfFileName.equals("rec")) {
                csvReader = new CSVReader(new FileReader(recFilePath));
            } else {
                throw new IllegalArgumentException("Invalid FileName: " + kindOfFileName);
            }

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                csvData.add(row);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return csvData;
    }

    /*
     * author: emhaki
     * date: 2023.10.10
     * description: Csv 데이터 update
     * */
    public List<String[]> updateCsv(@RequestParam Map<String, Object> paramMap, String kindOfFileName) throws Exception {
        List<String[]> csvData = new ArrayList<>();
        CSVReader csvReader = null;
        String[] row;
        try {
            if (kindOfFileName.equals("product")) {
                csvReader = new CSVReader(new FileReader(productFilePath));
            } else if (kindOfFileName.equals("rec")) {
                csvReader = new CSVReader(new FileReader(recFilePath));
            } else {
                throw new IllegalArgumentException("Invalid FileName: " + kindOfFileName);
            }

            while ((row = csvReader.readNext()) != null) {
                if (paramMap.get("search_product_id").equals(row[0]) && kindOfFileName.equals("product")) {
                    row[0] = (String) paramMap.get("item_id");
                    row[1] = (String) paramMap.get("item_name");
                    row[2] = (String) paramMap.get("item_image");
                    row[3] = (String) paramMap.get("item_url");
                    row[4] = (String) paramMap.get("original_price");
                    row[5] = (String) paramMap.get("sale_price");
                } else if (paramMap.get("search_product_id").equals(row[1]) && kindOfFileName.equals("rec")) {
                    row[0] = (String) paramMap.get("rec_id");
                    row[1] = (String) paramMap.get("item_id");
                    row[2] = (String) paramMap.get("score");
                    row[3] = (String) paramMap.get("rank");
                }
                csvData.add(row);
            }

            insertCsv(csvData, kindOfFileName);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return csvData;
    }



    /*
     * author: emhaki
     * date: 2023.10.10
     * description: Parameter로 넘어온 csvData 엑셀 작성
     * */
    public void insertCsv(List<String[]> csvData, String kindOfFileName) throws Exception {
        try {
            if (kindOfFileName.equals("product")) {
                try (CSVWriter csvWriter = new CSVWriter(new FileWriter(productFilePath))) {
                    for (String[] row : csvData) {
                        csvWriter.writeNext(row);
                    }
                }
            } else if (kindOfFileName.equals("rec")) {
                try (CSVWriter csvWriter = new CSVWriter(new FileWriter(recFilePath))) {
                    for (String[] row : csvData) {
                        csvWriter.writeNext(row);
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid FileName: " + kindOfFileName);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

