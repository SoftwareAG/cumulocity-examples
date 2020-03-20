package com.cumulocity.agent.snmp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SnmpGateway implements CommandLineRunner {

	public static void main(String... args) {
		SpringApplication.run(SnmpGateway.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		Thread.currentThread().join();
	}
}