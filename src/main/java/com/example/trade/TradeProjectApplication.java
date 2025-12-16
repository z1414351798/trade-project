package com.example.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.trade.mapper")
@SpringBootApplication
public class TradeProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeProjectApplication.class, args);
	}

}
