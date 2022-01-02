##Tutorial URL 
https://spring.io/guides/gs/batch-processing/

##TODO
  - [X] HSQL 대신 Mysql 사용하기
  - [X] Mybatis 도입 
    - 참고 1 : https://devlog-wjdrbs96.tistory.com/200
    - 참고 2 : https://jsonobject.tistory.com/225
  - [ ] REST API 데이터 저장 예시 작성 
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
  - 커스터마이징 설정 방법 찾기
    1. 검색어 "@ConfigurationProperties" 혹은 "@EnableConfigurationProperties"
    2. org.springframework.boot.autoconfigure.batch.BatchProperties 발견
    3. jdbc의 schema, platform, tablePrefix, initializeSchema 등을 설정할 수 있음.
  - spring.batch.jdbc.initialize-schema: always 추가

* MySQL에서 테이블명 인식하지 못하는 현상
  - MySQL설치시 대소문자를 구분하도록 되어 있는 경우가 있다.(lower_case_table_names = 0 인경우) 
  - 컨테이너 재생성해서 해결했음. 
    - mysql 컨테이너 설정 방법 참고 URL : https://hub.docker.com/_/mysql?tab=description
    - 컨테이너 생성옵션 추가 -v C:/Users/user/mysql-volume/my-conf:/etc/mysql/conf.d
    - C:/Users/user/mysql-volume/my-conf/myconfig.cnf 내용 
      ```
      lower_case_table_names=1
      ```
## 설정정보들
#### 공식 사이트
- https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html

#### MyBatis
* pom 설정 
  ```xml
  <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
      <version>2.2.1</version>
  </dependency>
  ```
* 설정파일
  ```text
  org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
  org.mybatis.spring.boot.autoconfigure.MybatisAutoProperties
  ```
* 주요 설정 값들(https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
  ```properties
  mybatis.config-location=
  mybatis.mapper-locations=
  mybatis.type-aliases-package=
  mybatis.executor-type=SIMPLE
  mybatis.executor-type=REUSE
  # BATCH모드 이해하기 https://bkim.tistory.com/4
  mybatis.executor-type=BATCH
  
  # mybatis.configuration.* (mybatis.config-location과 같이 사용하지 못함.)
  # https://mybatis.org/mybatis-3/configuration.html#settings
  mybatis.configuration.map-underscore-to-camel-case=true
  # 실행가능 JAR로 실행할 경우. https://do-study.tistory.com/78
  mybatis.configuration.vfsImpl=org.mybatis.spring.boot.autoconfigure.SpringBootVFS
  ```