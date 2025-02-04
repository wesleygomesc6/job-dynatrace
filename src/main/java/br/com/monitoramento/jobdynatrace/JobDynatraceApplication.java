package br.com.monitoramento.jobdynatrace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@EnableScheduling
@SpringBootApplication
@Slf4j
public class JobDynatraceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobDynatraceApplication.class, args);
	}

}
