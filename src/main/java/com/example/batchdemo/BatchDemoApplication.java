package com.example.batchdemo;

import com.example.batchdemo.dao.PersonDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BatchDemoApplication {

	@Autowired
	private PersonDAO dao;

	public static void main(String[] args) throws Exception {
//		System.exit(SpringApplication.exit(SpringApplication.run(BatchDemoApplication.class, args)));
		SpringApplication.run(BatchDemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(String[] arg){
		return args -> {
			System.out.println(dao.getAllPerson());
		};
	}

}
