package com.example.batchdemo;

import com.example.batchdemo.dto.Person;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void beforeJob(JobExecution jobExecution){

    }
    @Override
    public void afterJob(JobExecution jobExecution){

        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            log.info("!!! JOB FINISHED! verify the results");
            jdbcTemplate.query("SELECT first_name, last_name FROM people",(rs, rowNum) -> new Person(rs.getString(1),rs.getString(2)))
                    .forEach(person -> log.info("FOUND < " + person + " > in the db" ));
        }
    }
}
