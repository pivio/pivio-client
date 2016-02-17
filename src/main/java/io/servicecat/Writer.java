package io.servicecat;

import com.fasterxml.jackson.databind.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
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
            throw new IllegalArgumentException("Could not create JSON output.");
        }
    }

    private void uploadToServer(String json) {
        String serverUrl = configuration.getParameter(Configuration.SWITCH_SERVER_URL);
        RestTemplate rt = new RestTemplate();
        if (configuration.isVerbose()) {
            System.out.println("Uploading  to " + serverUrl + ": "+json);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        ResponseEntity<JsonNode> responseEntity = rt.exchange(serverUrl, HttpMethod.POST, new HttpEntity<>(json, headers), JsonNode.class);
        if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
            System.out.println("Upload to " + serverUrl + " failed. Return code: " + responseEntity.getStatusCode() + " with Message " + responseEntity.toString() + ".");
        } else if (configuration.isVerbose()) {
            System.out.println("Upload to " + serverUrl + " successful.");
        }
    }
}