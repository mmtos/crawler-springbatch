package com.example.batchdemo.config.batch;

import com.example.batchdemo.JobCompletionNotificationListener;
import com.example.batchdemo.dto.Person;
import com.example.batchdemo.PersonItemProcessor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class PersonBatchConfig {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public SqlSessionFactory sqlSessionFactoryBean;


    @Bean
    @StepScope
    public FlatFileItemReader<Person> reader(){
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName","lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>(){
                    {setTargetType(Person.class);}
                })
                .build();
    }

    @Bean
    @StepScope
    public PersonItemProcessor processor(){
        return new PersonItemProcessor();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (person_id, first_name, last_name) VALUES (people_seq.NEXTVAL,:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }
    @Bean
    @StepScope
    public MyBatisBatchItemWriter<Person> mybatisWriter(){
        return new MyBatisBatchItemWriterBuilder<Person>()
                .sqlSessionFactory(sqlSessionFactoryBean)
                .statementId("Person.insertPerson")
                .build();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener,DataSource dataSource) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1())
                .next(step1())
                .build();
    }

    @Bean
    @JobScope
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(mybatisWriter())
                .build();
    }
}
