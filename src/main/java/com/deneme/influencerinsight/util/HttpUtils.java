package com.deneme.influencerinsight.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

public final class HttpUtils {

    private HttpUtils() {
    }

    private static final RestTemplate RT = new RestTemplate();

    /**
     * Query param’lı, auth’suz GET – asla null body döndürmez
     */
    public static <T> T get(String url,
                            Map<String, String> queryParams,
                            ParameterizedTypeReference<T> typeRef) {
        UriComponentsBuilder b = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null) queryParams.forEach(b::queryParam);

        ResponseEntity<T> res = RT.exchange(
                b.build(true).toUri(), HttpMethod.GET, HttpEntity.EMPTY, typeRef);

        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("GET failed: " + url + " status=" + res.getStatusCode());
        }
        T body = res.getBody();
        return body != null ? body : emptyFor(typeRef);
    }

    /**
     * Query param’lı, Bearer auth’lu GET – asla null body döndürmez
     */
    public static <T> T getAuthed(String url,
                                  String bearerToken,
                                  Map<String, String> queryParams,
                                  ParameterizedTypeReference<T> typeRef) {
        UriComponentsBuilder b = UriComponentsBuilder.fromUriString(url);
        if (queryParams != null) queryParams.forEach(b::queryParam);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);

        ResponseEntity<T> res = RT.exchange(
                b.build(true).toUri(), HttpMethod.GET, new HttpEntity<>(headers), typeRef);

        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("GET (auth) failed: " + url + " status=" + res.getStatusCode());
        }
        T body = res.getBody();
        return body != null ? body : emptyFor(typeRef);
    }

    /**
     * application/x-www-form-urlencoded POST – asla null body döndürmez
     */
    public static <T> T postForm(String url,
                                 String formEncodedBody,
                                 ParameterizedTypeReference<T> typeRef) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(formEncodedBody, headers);

        ResponseEntity<T> res = RT.exchange(url, HttpMethod.POST, entity, typeRef);

        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("POST form failed: " + url + " status=" + res.getStatusCode());
        }
        T body = res.getBody();
        return body != null ? body : emptyFor(typeRef);
    }

    @SuppressWarnings("unchecked")
    private static <T> T emptyFor(ParameterizedTypeReference<T> typeRef) {
        if (typeRef.getType().getTypeName().startsWith("java.util.Map")) {
            return (T) Map.of();
        }
        throw new IllegalStateException("No default empty value for type: " + typeRef.getType());
    }
}
