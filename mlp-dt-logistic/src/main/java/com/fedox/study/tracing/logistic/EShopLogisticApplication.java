package com.fedox.study.tracing.logistic;

import io.jaegertracing.Configuration;
import io.opentracing.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableAspectJAutoProxy
@PropertySource("classpath:application.yaml")
public class EShopLogisticApplication {

	public static void main(String[] args) {
		SpringApplication.run(EShopLogisticApplication.class, args);
	}

	@Value("${tracer.trace.name}")
	private String tracerName;

	@Bean
	public Tracer tracer() {
		return Configuration.fromEnv(tracerName).getTracer();
	}

}
