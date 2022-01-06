package com.example.batchdemo;

import com.example.batchdemo.batch.config.PersonBatchConfig;
import com.example.batchdemo.batch.listener.JobCompletionNotificationListener;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest(classes = {PersonBatchConfig.class, TestBatchConfig.class, JobCompletionNotificationListener.class})
public class BatchIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Test
    public void PERSON_추가_통합테스트() throws Exception{
        JobExecution je = jobLauncherTestUtils.launchJob();
        assertThat(je.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

}
