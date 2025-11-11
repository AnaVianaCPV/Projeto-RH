package com.rhgroup.cadastrosrh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.rhgroup.cadastrosrh", "com.rhgroup.cadastrosrh.controller"})
@SpringBootApplication
public class CadastrosRhApplication {

	public static void main(String[] args) {
		SpringApplication.run(CadastrosRhApplication.class, args);
	}

}
