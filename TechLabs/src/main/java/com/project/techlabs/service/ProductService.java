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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String productFilePath = "/Users/emhaki/Desktop/product.csv";
    private static final String recFilePath = "/Users/emhaki/Desktop/rec.csv";

    private String productWriterCheck = null;
    private String recWriterCheck = null;
    /*
     * author: emhaki
     * date: 2023.10.06
     * description: Csv 데이터 read
     * */
    public ProductData readCsvByProductId(String productIds) throws IOException {
        ProductData productData = new ProductData();
        List<ProductResultDTO> resultList = new ArrayList<>();

        // 1. while문으로 recFilePath 엑셀을 쭉 돌면서 recId값과 productIds값이 일치하는 것을 DTO에 담기
        String recLine;
        String productLine;
        String productId = productIds.replaceAll("^\"|\"$", "");

        try {
            BufferedReader recBr = new BufferedReader(new FileReader(recFilePath));
            BufferedReader productBr = new BufferedReader(new FileReader(productFilePath));
            String[] productValues = null;
            String[] recValues = null;

            while ((recLine = recBr.readLine()) != null) {
                recValues = recLine.split(",");

                String recId = recValues[0].replaceAll("^\"|\"$", "");
                if (recId.equals(productId)) {

                    ProductResultDTO productResultDTO = new ProductResultDTO();
                    //  다시 읽도록 리셋
                    productBr = new BufferedReader(new FileReader(productFilePath));
                    while ((productLine = productBr.readLine()) != null) {
                        productValues = productLine.split(",");
                        if (recValues[1].replaceAll("^\"|\"$", "").equals(productValues[0].replaceAll("^\"|\"$", ""))) {
                            productResultDTO.setItem_id(recValues[1]);
                            productResultDTO.setItem_name(productValues[1]);
                            productResultDTO.setItem_image(productValues[2]);
                            productResultDTO.setItem_url(productValues[3]);
                            productResultDTO.setOriginal_price(productValues[4]);
                            productResultDTO.setSale_price(productValues[5]);
                            productResultDTO.setScore(recValues[2]);
                            productResultDTO.setRank(recValues[3]);
                            resultList.add(productResultDTO);
                        }
                    }
                    productBr = new BufferedReader(new FileReader(productFilePath));
                    try {
                        while ((productLine = productBr.readLine()) != null) {
                            productValues = productLine.split(",");
                            if (productId.contains(productValues[0].replaceAll("^\"|\"$", ""))) {
                                ProductDTO productDto = new ProductDTO();
                                productDto.setItem_id(productValues[0]);
                                productDto.setItem_name(productValues[1]);
                                productDto.setItem_image(productValues[2]);
                                productDto.setItem_url(productValues[3]);
                                productDto.setOriginal_price(productValues[4]);
                                productDto.setSale_price(productValues[5]);
                                productData.setTarget(productDto);
                            }
                        }
                    } catch (NumberFormatException e) {
                        logger.error("Invalid number");
                    }

                }
            }
            productData.setResults(resultList);
        } catch (IOException e) {
            logger.error("CSV-file Reading Error" + e.getMessage());
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
            CSVWriter productCsvWriter = new CSVWriter(new FileWriter(productFilePath, true));
            CSVWriter recCsvWriter = new CSVWriter(new FileWriter(recFilePath, true));

            // product csv 파일의 행 == 6
            if (paramMap.size() == 6) {
                String[] productRow = {
                        (String) paramMap.get("item_id"),
                        (String) paramMap.get("item_name"),
                        (String) paramMap.get("item_image"),
                        (String) paramMap.get("item_url"),
                        (String) paramMap.get("original_price"),
                        (String) paramMap.get("sale_price")
                };
                // 처음 데이터 삽입시
                if (productWriterCheck == null) {
                    String[] productSpace = {" "};
                    productCsvWriter.writeNext(productSpace);
                    productCsvWriter.writeNext(productRow);
                    productWriterCheck = "check";
                } else {
                    productCsvWriter.writeNext(productRow);
                }
                productCsvWriter.close();
            } else if (paramMap.size() == 4) {
                // 바로 다음행에 이어져서 써짐
                String[] recRow = {
                        (String) paramMap.get("relation_id"),
                        (String) paramMap.get("item_id"),
                        (String) paramMap.get("score"),
                        (String) paramMap.get("rank")
                };
                if (recWriterCheck == null) {
                    String[] recSpace = {" "};
                    recCsvWriter.writeNext(recSpace);
                    recCsvWriter.writeNext(recRow);
                    recWriterCheck = "check";
                } else {
                    recCsvWriter.writeNext(recRow);
                }
                recCsvWriter.close();
            }
        } catch (NullPointerException e) {
            e.getMessage();
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
                }

        } catch (NullPointerException e) {
            e.getMessage();
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
            }

            writeCsv(csvData , kindOfFileName);
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

    /*
     * author: emhaki
     * date: 2023.10.10
     * description: Csv 데이터 리스트 삽입 후 값 반환
     * */
    public List<String[]> readCsv(String kindOfFileName) throws Exception {
        List<String[]> csvData = new ArrayList<>();
        CSVReader csvReader = null;
        try {
            if (kindOfFileName.equals("product")) {
                csvReader = new CSVReader(new FileReader(productFilePath));
            } else if (kindOfFileName.equals("rec")) {
                csvReader = new CSVReader(new FileReader(recFilePath));
            }
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                csvData.add(row);
            }

        } catch (NullPointerException e) {
            e.getMessage();
        }
        return csvData;
    }

    /*
     * author: emhaki
     * date: 2023.10.10
     * description: Csv 데이터 delete
     * */
    public List<String[]> updateCsv(@RequestParam Map<String, Object> paramMap, String kindOfFileName) throws Exception {
        List<String[]> csvData = new ArrayList<>();
        CSVReader csvReader = null;
        String[] row;
        try{

        if (kindOfFileName.equals("product")) {
            csvReader = new CSVReader(new FileReader(productFilePath));

            while ((row = csvReader.readNext()) != null) {
                if (paramMap.get("search_product_id").equals(row[0])) {
                    row[0] = (String) paramMap.get("item_id");
                    row[1] = (String) paramMap.get("item_name");
                    row[2] = (String) paramMap.get("item_image");
                    row[3] = (String) paramMap.get("item_url");
                    row[4] = (String) paramMap.get("original_price");
                    row[5] = (String) paramMap.get("sale_price");
                    System.out.println(Arrays.toString(row) + "여기보세요");
                }
                csvData.add(row);
            }
        } else if (kindOfFileName.equals("rec")) {
            csvReader = new CSVReader(new FileReader(recFilePath));

            while ((row = csvReader.readNext()) != null) {
                System.out.println(Arrays.toString(row));
                // rec 파일의 고유 item_id = 두번째 열
                if (paramMap.get("search_product_id").equals(row[1])) {
                    row[0] = (String) paramMap.get("rec_id");
                    row[1] = (String) paramMap.get("item_id");
                    row[2] = (String) paramMap.get("score");
                    row[3] = (String) paramMap.get("rank");
                }
                csvData.add(row);
            }
        }
            writeCsv(csvData, kindOfFileName);
        } catch (NullPointerException e) {
            e.getMessage();
        }
        return csvData;
    }



    /*
     * author: emhaki
     * date: 2023.10.10
     * description: Parameter 넘어온 csvData 엑셀 작성
     * */
    public void writeCsv(List<String[]> csvData, String kindOfFileName) throws Exception {
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
        }
    }
}

