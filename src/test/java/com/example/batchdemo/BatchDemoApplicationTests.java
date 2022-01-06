package com.example.batchdemo;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BatchDemoApplicationTests {
	@Autowired
	private StringEncryptor jasyptStringEncryptor;

	@Test
	void contextLoads() {
	}


}
