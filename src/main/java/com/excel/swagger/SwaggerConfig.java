package com.excel.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	/*
	 * Defining classes That need to be
	 * Documented
	 */
	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select().apis(RequestHandlerSelectors.
						withClassAnnotation(RestController.class))
				.build();
	}
	
	/*
	 * Preparing APIs Information 
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Excel utility Documentation")
				.description("Guide to excel utility ")
				.termsOfServiceUrl("http://127.0.0.135:8080/terms")
				.contact("Prakhar pvt ltd")
				.license("Apache License Version 20")
				.licenseUrl("https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE")
				.version("1.0")
				.build();
	}
}
