package com.devops.rest;

import com.devops.rest.error.CorrelationIdFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RestExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FailingExternalController())
                .setControllerAdvice(new RestExceptionHandler())
                .addFilters(new CorrelationIdFilter())
                .build();
    }

    @Test
    void mapsExternalAuthenticationFailureWithoutLeakingDetails() throws Exception {
        mockMvc.perform(get("/external-auth-failure"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("EXTERNAL_AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.message").value("External service authentication failed"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    void mapsExternalAvailabilityFailureWithoutLeakingDetails() throws Exception {
        mockMvc.perform(get("/external-unavailable"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("EXTERNAL_SERVICE_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value("External service unavailable"))
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @RestController
    private static class FailingExternalController {

        @GetMapping("/external-auth-failure")
        void externalAuthFailure() {
            throw HttpClientErrorException.create(
                    HttpStatusCode.valueOf(401),
                    "Unauthorized",
                    HttpHeaders.EMPTY,
                    "password=secret".getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8
            );
        }

        @GetMapping("/external-unavailable")
        void externalUnavailable() {
            throw new RestClientException("http://user:secret@example.invalid");
        }
    }
}
