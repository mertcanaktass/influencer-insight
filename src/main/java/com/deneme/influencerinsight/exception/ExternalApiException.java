package com.deneme.influencerinsight.exception;

import org.springframework.http.HttpStatusCode;

public class ExternalApiException extends RuntimeException {

    private final String serviceName;
    private final HttpStatusCode statusCode;

    public ExternalApiException(String serviceName, String message, HttpStatusCode statusCode) {
        super(message);
        this.serviceName = serviceName;
        this.statusCode = statusCode;
    }

    public ExternalApiException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
        this.statusCode = null;
    }

    public String getServiceName() {
        return serviceName;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
