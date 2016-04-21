package io.pivio.upload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivio.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Service
public class Writer {

    @Autowired
    private Configuration configuration;

    public void write(Map<String, Object> document) throws IllegalArgumentException {
        try {
            String json = new ObjectMapper().writeValueAsString(document);
            if (configuration.hasOption(Configuration.SWITCH_DRY_RUN)) {
                System.out.println("\n " + json + "\n");
            } else {
                uploadToServer(json);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not create JSON output.", e);
        }
    }

    private void uploadToServer(String json) {
        String serverUrl = configuration.getParameter(Configuration.SWITCH_SERVICE_URL);
        if (configuration.isVerbose()) {
            System.out.println("Uploading  to " + serverUrl + ": " + json);
        }

        RestTemplate rt = new RestTemplate();
        rt.setErrorHandler(new RestCallErrorHandler());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        try {
            ResponseEntity<JsonNode> responseEntity = rt.exchange(serverUrl, HttpMethod.POST, new HttpEntity<>(json, headers), JsonNode.class);
            if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
                handleNonCreatedStatusCode(serverUrl, responseEntity, json);
            } else if (configuration.isVerbose()) {
                System.out.println("Upload to " + serverUrl + " successful.");
            }
        } catch (ResourceAccessException e) {
            handleConnectionRefused(serverUrl);
        }
    }

    private void handleConnectionRefused(String serverUrl) {
        String message = "Error: Could not contact server at '" + serverUrl + "'.";
        if (configuration.hasOption(Configuration.SWITCH_UPLOAD_FAILS_EXIT1)) {
            throw new RuntimeException(message);
        } else {
            System.out.println(message);
        }
    }

    private void handleNonCreatedStatusCode(String serverUrl, ResponseEntity<JsonNode> responseEntity, String payload) {
        String message = "Error: Upload to " + serverUrl + " with payload '" + payload + "' failed.\nReturn code: " + responseEntity.getStatusCode() + " with Message " + responseEntity.getBody().toString() + ".";
        if (configuration.hasOption(Configuration.SWITCH_UPLOAD_FAILS_EXIT1)) {
            throw new RuntimeException(message);
        } else {
            System.out.println(message);
        }
    }
}