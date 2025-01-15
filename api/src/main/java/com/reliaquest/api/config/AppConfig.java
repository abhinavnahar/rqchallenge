package com.reliaquest.api.config;

import com.reliaquest.api.exceptions.ApiException;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Retry retry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(5)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(2000, 2))
                .retryOnException(e -> {
                    if (e instanceof HttpClientErrorException) {
                        HttpClientErrorException httpException = (HttpClientErrorException) e;
                        return httpException.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS;
                    }
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        return apiException.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value();
                    }
                    return false;
                })
                .build();

        return Retry.of("employeeServiceRetry", retryConfig);
    }

    @Bean
    public String baseUrl(@Value("${externalservie.url}") String url) {
        return url;
    }
}
