package com.example.batchdemo.batch.reader;

import com.example.batchdemo.dto.Hospital;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

@Slf4j
public class HospitalItemReader implements ItemReader<Hospital> {

    private JobExecution jobExecution;
    private int count = 0 ;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecution = stepExecution.getJobExecution();
        System.out.println(jobExecution);
    }

    @Override
    public Hospital read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String pageNo =  jobExecution.getExecutionContext().get("pageNo").toString();
        count++;
        log.error(String.valueOf(count));
        log.error(pageNo);


        Hospital hp = new Hospital();
        hp.setRnum("1");
        hp.setRnum("testhp");

        String nextPageNo = String.valueOf(Integer.parseInt(pageNo) + 1);
        jobExecution.getExecutionContext().put("pageNo", nextPageNo);

        return hp;
    }
}
