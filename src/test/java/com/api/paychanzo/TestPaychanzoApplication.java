package com.api.paychanzo;

import org.springframework.boot.SpringApplication;

public class TestPaychanzoApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaychanzoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
