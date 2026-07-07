package com.devops.rest;

import com.devops.domain.exception.RepositoryNotFoundException;
import com.devops.domain.exception.ReferenceNotFoundException;
import com.devops.domain.exception.TagNotFoundException;
import com.devops.rest.error.ApiError;
import com.devops.rest.error.ApiErrorCode;
import com.devops.rest.error.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(RepositoryNotFoundException.class)
    public ResponseEntity<ApiError> handleRepositoryNotFound(
            RepositoryNotFoundException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, ApiErrorCode.REPOSITORY_NOT_FOUND, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(ReferenceNotFoundException.class)
    public ResponseEntity<ApiError> handleReferenceNotFound(
            ReferenceNotFoundException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, ApiErrorCode.REFERENCE_NOT_FOUND, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<ApiError> handleTagNotFound(
            TagNotFoundException exception,
            HttpServletRequest request
    ) {
        return error(HttpStatus.NOT_FOUND, ApiErrorCode.TAG_NOT_FOUND, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, ApiErrorCode.BAD_REQUEST, exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        ApiErrorCode code = status == HttpStatus.NOT_FOUND ? ApiErrorCode.NOT_FOUND : ApiErrorCode.BAD_REQUEST;
        return error(status, code, exception.getReason(), request, List.of());
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiError> handleExternalServiceResponseError(
            RestClientResponseException exception,
            HttpServletRequest request
    ) {
        if (exception.getStatusCode().value() == 401 || exception.getStatusCode().value() == 403) {
            LOGGER.warn(
                    "External service authentication failed for path {} with status {}",
                    request.getRequestURI(),
                    exception.getStatusCode().value()
            );
            return error(
                    HttpStatus.BAD_GATEWAY,
                    ApiErrorCode.EXTERNAL_AUTHENTICATION_FAILED,
                    "External service authentication failed",
                    request,
                    List.of()
            );
        }

        LOGGER.warn(
                "External service returned an error for path {} with status {}",
                request.getRequestURI(),
                exception.getStatusCode().value()
        );
        return error(
                HttpStatus.BAD_GATEWAY,
                ApiErrorCode.EXTERNAL_SERVICE_UNAVAILABLE,
                "External service unavailable",
                request,
                List.of()
        );
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiError> handleExternalServiceError(RestClientException exception, HttpServletRequest request) {
        LOGGER.warn("External service call failed for path {} with exception type {}", request.getRequestURI(), exception.getClass().getName());
        return error(
                HttpStatus.BAD_GATEWAY,
                ApiErrorCode.EXTERNAL_SERVICE_UNAVAILABLE,
                "External service unavailable",
                request,
                List.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedError(Exception exception, HttpServletRequest request) {
        LOGGER.error("Unexpected error for path {}", request.getRequestURI(), exception);
        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ApiErrorCode.UNEXPECTED_ERROR,
                "Unexpected error",
                request,
                List.of()
        );
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status,
            ApiErrorCode code,
            String message,
            HttpServletRequest request,
            List<String> details
    ) {
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                request.getRequestURI(),
                correlationId(),
                details
        ));
    }

    private String correlationId() {
        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        return correlationId == null ? "" : correlationId;
    }
}
