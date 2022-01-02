##Tutorial URL 
https://spring.io/guides/gs/batch-processing/

##TODO
  - [X] HSQL 대신 Mysql 사용하기
  - [ ] Mybatis 도입

##문제 발생 및 해결
* mysql로 DB변경시 테이블이 자동생성 되지않는 현상 발생 
  - 기본적으로는 embedded db(H2 HSQL...)를 사용하지 않으면 schema sql 문을 실행시키지 않음. 
    - spring.datasource.initialization-mode의 기본값이 DatabaseInitializationMode.EMBEDDED기 때문 
  - application.yml 설정 
    - spring.datasource.initialization-mode=always => 모든 DB에 대해서 class패스 내 schema-{platform}.sql을 실행한다. 
    - spring.datasource.initialization-mode=never => class패스 내 schema-{platform}.sql을 절대 실행하지 않는다.
    - spring.datasource.platform=mysql => schema-mysql.sql을 실행한다. 지정해 주지 않으면 기본값으로 schema-all.sql을 실행한다.
  - 참고 : https://wan-blog.tistory.com/52

* Spring Batch 메타 테이블이 자동생성되지 않는 현상 
  - spring batch의 경우 따로 설정해주어야 시작 시점에 schema-sql을 실행시킴. 
  - 설정 방법 찾기
    1. 검색어 "@ConfigurationProperties"로 라이브러리 파일 검색
    2. org.springframework.boot.autoconfigure.batch.BatchProperties 발견
    3. jdbc의 schema, platform, tablePrefix, initializeSchema 등을 설정할 수 있음.
  - spring.batch.jdbc.initialize-schema: always 추가  