package com.deneme.influencerinsight.integration;

import com.deneme.influencerinsight.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExternalApiClient {

    private final RestClient externalApiRestClient;

    public <T> T get(String serviceName,
                     URI uri,
                     String bearerToken,
                     ParameterizedTypeReference<T> responseType) {
        try {
            RestClient.RequestHeadersSpec<?> request = externalApiRestClient.get().uri(uri);
            if (bearerToken != null && !bearerToken.isBlank()) {
                request.headers(headers -> headers.setBearerAuth(bearerToken));
            }
            return execute(serviceName, request, responseType);
        } catch (RestClientException ex) {
            throw new ExternalApiException(serviceName, "External request failed.", ex);
        }
    }

    public <T> T get(String serviceName,
                     String url,
                     Map<String, String> queryParams,
                     String bearerToken,
                     ParameterizedTypeReference<T> responseType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        queryParams.forEach(uriBuilder::queryParam);
        return get(serviceName, uriBuilder.build(true).toUri(), bearerToken, responseType);
    }

    public <T> T postForm(String serviceName,
                          URI uri,
                          String formBody,
                          ParameterizedTypeReference<T> responseType) {
        try {
            RestClient.RequestHeadersSpec<?> request = externalApiRestClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formBody);
            return execute(serviceName, request, responseType);
        } catch (RestClientException ex) {
            throw new ExternalApiException(serviceName, "External request failed.", ex);
        }
    }

    public <T> T postForm(String serviceName,
                          URI uri,
                          MultiValueMap<String, String> formBody,
                          ParameterizedTypeReference<T> responseType) {
        try {
            RestClient.RequestHeadersSpec<?> request = externalApiRestClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formBody);
            return execute(serviceName, request, responseType);
        } catch (RestClientException ex) {
            throw new ExternalApiException(serviceName, "External request failed.", ex);
        }
    }

    public <T> T postForm(String serviceName,
                          String url,
                          String formBody,
                          ParameterizedTypeReference<T> responseType) {
        return postForm(serviceName, URI.create(url), formBody, responseType);
    }

    public <T> T postJson(String serviceName,
                          URI uri,
                          String bearerToken,
                          Object requestBody,
                          Class<T> responseType) {
        try {
            RestClient.RequestHeadersSpec<?> request = externalApiRestClient.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody);
            if (bearerToken != null && !bearerToken.isBlank()) {
                request.headers(headers -> headers.setBearerAuth(bearerToken));
            }
            return execute(serviceName, request, responseType);
        } catch (RestClientException ex) {
            throw new ExternalApiException(serviceName, "External request failed.", ex);
        }
    }

    private <T> T execute(String serviceName,
                          RestClient.RequestHeadersSpec<?> request,
                          ParameterizedTypeReference<T> responseType) {
        T body = request.retrieve()
                .onStatus(HttpStatusCode::isError, (responseRequest, response) -> {
                    throw new ExternalApiException(serviceName,
                            "External service returned an error response.",
                            response.getStatusCode());
                })
                .body(responseType);
        if (body == null) {
            throw new ExternalApiException(serviceName,
                    "External service returned an empty response.",
                    (HttpStatusCode) null);
        }
        return body;
    }

    private <T> T execute(String serviceName,
                          RestClient.RequestHeadersSpec<?> request,
                          Class<T> responseType) {
        T body = request.retrieve()
                .onStatus(HttpStatusCode::isError, (responseRequest, response) -> {
                    throw new ExternalApiException(serviceName,
                            "External service returned an error response.",
                            response.getStatusCode());
                })
                .body(responseType);
        if (body == null) {
            throw new ExternalApiException(serviceName,
                    "External service returned an empty response.",
                    (HttpStatusCode) null);
        }
        return body;
    }
}
