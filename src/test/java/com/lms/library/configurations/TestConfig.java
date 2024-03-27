package com.lms.library.configurations;

import java.io.IOException;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.library.models.ModelUser;

@TestConfiguration
public class TestConfig {
	
	public static int port = 8080;
	
	public static String baseUrl = "http://localhost:" + port;
	
	private static ObjectMapper objectMapper = new ObjectMapper();

    public static ModelUser convertJsonToModelUser(String json) throws IOException {
        return objectMapper.readValue(json, ModelUser.class);
    }
	
	@Bean
    public TestRestTemplate testRestTemplate(RestTemplateBuilder builder) {
		builder.additionalMessageConverters(new MappingJackson2HttpMessageConverter());
		//TestRestTemplate restTemplate = new TestRestTemplate(builder);
        return new TestRestTemplate(builder);
    }
	
	@Bean
	public RestTemplate restTemplate() {
	    RestTemplate restTemplate = new RestTemplate();
	    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	    return restTemplate;
	}
}
