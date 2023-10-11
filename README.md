# TechLabs-project

### 지원자 성명
* 이명학
### 개발 환경
* **IDE**: IntelliJ
* **Language**: Java 11
* **Framework**: Spring boot 3.1.4
### 빌드 방법
* **Gradle**
* **Jar**
### 과제 수행 내역
1. **상품 정보 및 연관 상품에 대한 조회**
- 입력 파라미터 (URL Query Parameter)
    - id: 결과로 볼 상품코드
    - 2개 이상의 상품을 볼 경우 콤마(,)로 구분. ex: id=1,2,3
- 결과
    - JSON 형태의 결과
    - 입력 상품과 결과 상품을 상품 정보와 조합하여 반환한다. 
- **수행 내역(Description)**   

![상품정보 및 연관 상품 조회.png](..%2F%EC%83%81%ED%92%88%EC%A0%95%EB%B3%B4%20%EB%B0%8F%20%EC%97%B0%EA%B4%80%20%EC%83%81%ED%92%88%20%EC%A1%B0%ED%9A%8C.png)
```
1. Get 요청방식, Parameter에 id값을 입력
2. 2개 이상의 상품을 볼 경우 콤마로 구분
3. product.csv의 데이터와 rec.csv의 데이터를 결합하여 JSON 반환
```
----------
2. **상품 정보 및 연관 상품 정보에 대한 입력/수정/삭제 기능**

- 상품 정보(product)와 연관 상품 정보(rec)에 대한 Insert/Update/Delete 기능.
- 해당 기능에 대한 정확한 가이드는 없으며, 지원자가 생각하기에 적합한 방식으로 구현.
- 구현한 기능을 사용하기 위한 가이드를 README.md 파일에 간단한 설명 추가.

- **수행 내역(Description)**
  1. **Insert 기능**
  ![InsertData(product.csv).png](..%2FInsertData%28product.csv%29.png)
  ![InsertCsv(product.csv).png](..%2FInsertCsv%28product.csv%29.png)
  ```
  1. rec/inset로 Post요청을 보내게 되면 parameter에 담긴 값이 엑셀에 Insert 되게끔 구현했습니다.
  2. 삽입할 데이터의 item_id, item_name, item_image, item_url, original_price, sale_price를 입력합니다.
  ```
  2. **Update 기능**
  ![UpdateData(product.csv).png](..%2FUpdateData%28product.csv%29.png)
  ![UpdateCsv(product.csv).png](..%2FUpdateCsv%28product.csv%29.png)
  ```java
  1. rec/update로 Post요청을 보내게 되면 parameter에 담긴 값이 엑셀에 Update 되게끔 구현했습니다.
  2. Update기능은 update하고 싶은 search_product_id를 입력해야 합니다.
  3. 입력한 search_product_id를 기반으로 엑셀을 탐색하며 관리자가 입력한 값들로 update합니다.
  ```
  3. **Delete 기능**
  ![DeleteData(product.csv).png](..%2FDeleteData%28product.csv%29.png)
  ![DeleteCsv(product.csv).png](..%2FDeleteCsv%28product.csv%29.png)
  ```java
  1. rec/delete로 Post요청을 보내게 되면 search_product_id와 kind_of_file_name에 값에 따라 엑셀에 데이터가 Delete 되게끔 구현했습니다.
  2. kind_of_file_name이 product라면 product.csv, rec라면 rec.csv를 탐색하고 해당하는 값의 행을 삭제합니다. 
  ```
### 개발 중 고려한 사항 및 확인 시 참고할 사항
* item_id는 고유한 id값이라고 가정하고 코드를 구현했습니다.
