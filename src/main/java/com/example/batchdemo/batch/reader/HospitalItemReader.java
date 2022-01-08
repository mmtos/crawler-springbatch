package com.example.batchdemo.batch.reader;

import com.example.batchdemo.dto.Hospital;
import com.example.batchdemo.exception.Not200Exception;
import com.example.batchdemo.util.HttpConnectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class HospitalItemReader implements ItemReader<Hospital> {

    private JobExecution jobExecution;

    @Value("${openapi.data.go.kr.key}")
    private String serviceKey;

    @Value("${openapi.data.go.kr.hospital.url}")
    private String url;

    private String numOfRows = "1000";

    private String batchJobName = "HospitalBatchJob";

    private static final String SUCCESS_CODE = "00";

    private boolean checkRestCall = false;
    private int nextIndex = 0;

    private List<Hospital> resultArray = new ArrayList<>();

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecution = stepExecution.getJobExecution();
    }

    @Override
    public Hospital read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String pageNo =  jobExecution.getExecutionContext().get("pageNo").toString();

        if (checkRestCall == false){//한번도 호출 않았는지 체크
            URI uri = new URIBuilder(url)
                    .addParameter("ServiceKey",serviceKey)
                    .addParameter("numOfRows",numOfRows)
                    .addParameter("pageNo",String.valueOf(pageNo))
                    .build();
            Map<String,Object> bodyMap = null;
            try{
                HttpGet getMethod = new HttpGet(uri);
                String response = HttpConnectUtil.getResponseText(getMethod);

                log.debug(response.toString());

                ObjectMapper parser = new XmlMapper();
                Map<String,Object> resultMap = parser.readValue(response.toString(),Map.class);
                Map<String,Object> headerMap = (Map<String,Object>) resultMap.get("header");
                String resultCode = (String) headerMap.get("resultCode");

                if(SUCCESS_CODE.equals(resultCode)){
                    bodyMap = (Map<String,Object>) resultMap.get("body");
                    if(bodyMap.get("items") instanceof String){
                        log.error("items가 누락됨 내용이 없음");
                        log.error(response.toString());
                    }else {
                        bodyMap = (Map<String,Object>) bodyMap.get("items");
                    }
                    List<Map<String,String>> items = (List<Map<String,String>>) bodyMap.get("item");
                    for(Map<String,String> item : items){
                        Hospital hp =  new Hospital();
                        BeanUtils.populate(hp, item);
                        resultArray.add(hp);
                    }
                }
            }catch(Not200Exception e){
                log.error(e + "\n stackTrace : " + Arrays.stream(e.getStackTrace()).collect(Collectors.toList()));
                throw new Exception(e);
            }
            log.info("{} : {} 페이지 처리됨",batchJobName,pageNo);
            log.debug(resultArray.toString());
            Thread.sleep(3000);
            checkRestCall = true;//다음 read() 부터는 재호출 방지하기 위해 true로 변경
        }

        Hospital nextHospital = null; //ItemReader는 반복문으로 동작한다. 하나씩 Writer로 전달해야 한다.
        if (nextIndex < resultArray.size()) {//전체 리스트에서 하나씩 추출해서, 하나씩 Writer로 전달
            nextHospital = resultArray.get(nextIndex);
            nextIndex++;
        }
        //return nextCollect;//DTO 하나씩 반환한다. Rest 호출시 데이터가 없으면 null로 반환.
        return nextHospital;
    }
}
