package com.deneme.influencerinsight.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ExternalApiClientConfig {

    @Bean
    public RestClient externalApiRestClient(
            @Value("${app.http.connect-timeout-ms:5000}") int connectTimeoutMillis,
            @Value("${app.http.read-timeout-ms:15000}") int readTimeoutMillis) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMillis);
        requestFactory.setReadTimeout(readTimeoutMillis);

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}
