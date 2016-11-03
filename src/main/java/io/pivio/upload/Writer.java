package io.pivio.upload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.pivio.Configuration;
import io.pivio.Logger;
import io.pivio.PivioYamlParserException;
import io.pivio.schema.SchemaValidator;
import org.codehaus.jackson.schema.JsonSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.pivio.Configuration.*;

@Service
public class Writer {

    @Autowired
    Logger log;
    @Autowired
    SchemaValidator schemaValidator;
    @Autowired
    private Configuration configuration;

    public void write(Map<String, Object> document) throws IllegalArgumentException {
        try {
            String json = new ObjectMapper().writeValueAsString(document);
            JsonNode jsonNode = new ObjectMapper().readTree(json);
            if (schemaValidator.isValid(jsonNode)) {
                if (configuration.hasOption(Configuration.SWITCH_DRY_RUN)) {
                    log.output("\n " + json + "\n");
                } else {
                    outputJsonSchema(json);
                    writeToFile(document);
                    uploadToServer(json);
                }
            } else {
                throw new PivioYamlParserException("Converted Yaml to Json is invalid.");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not create JSON output.", e);
        }
    }

    private void outputJsonSchema(String json) {
        if (configuration.hasOption(SWITCH_GENERATE_JSON_SCHEMA)) {
            ObjectMapper mapper = new ObjectMapper();
            JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
            JsonSchema schema = schemaGen.generateSchema(json);
        }
    }

    void writeToFile(Map<String, Object> document) {
        if (configuration.hasOption(SWITCH_OUTFILE)) {
            ObjectMapper mapper = new ObjectMapper();
            String fileName = configuration.getParameter(SWITCH_OUTFILE);
            try {
                Map<String, Object> writeToFile = new HashMap<>();
                if (configuration.hasOption(SWITCH_OUTFILETOPLEVELATTRIBUTES)) {
                    String attributeParameter = configuration.getParameter(SWITCH_OUTFILETOPLEVELATTRIBUTES);
                    String[] attributes = attributeParameter.split(",");
                    for (String attribute : attributes) {
                        if (document.containsKey(attribute)) {
                            log.verboseOutput("Filtering output to file with attribute '" + attribute + "'.");
                            writeToFile.put(attribute, document.get(attribute));
                        } else {
                            log.verboseOutput("Could not find requested top level attribute '" + attribute + "'.");
                        }
                    }
                } else {
                    writeToFile = document;
                }
                mapper.writeValue(new File(fileName), writeToFile);
                log.verboseOutput("Wrote output to file '" + fileName + "'.");
            } catch (IOException e) {
                log.output("Error writing file " + fileName);
                throw new IllegalArgumentException("Could not write to file '" + fileName + "'.");
            }
        }
    }

    private void uploadToServer(String json) {
        if (configuration.hasOption(SWITCH_SERVICE_URL)) {
            String serviceUrl = configuration.getParameter(SWITCH_SERVICE_URL);
            log.verboseOutput("Uploading  to " + serviceUrl + ": " + json);
            RestTemplate rt = new RestTemplate();
            rt.setErrorHandler(new RestCallErrorHandler());
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            try {
                ResponseEntity<JsonNode> responseEntity = rt.exchange(serviceUrl, HttpMethod.POST, new HttpEntity<>(json, headers), JsonNode.class);
                if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
                    handleNonCreatedStatusCode(serviceUrl, responseEntity, json);
                } else {
                    log.verboseOutput("Upload to " + serviceUrl + " successful.");
                }
            } catch (ResourceAccessException e) {
                handleConnectionRefused(serviceUrl);
            }
        } else {
            log.verboseOutput("Not uploading to any server since no '" + SWITCH_SERVICE_URL + "' parameter was specified.");
        }
    }

    private void handleConnectionRefused(String serverUrl) {
        String message = "Error: Could not contact server at '" + serverUrl + "'.";
        if (configuration.hasOption(Configuration.SWITCH_UPLOAD_FAILS_EXIT1)) {
            throw new RuntimeException(message);
        } else {
            log.output(message);
        }
    }

    private void handleNonCreatedStatusCode(String serverUrl, ResponseEntity<JsonNode> responseEntity, String payload) {
        String message = "Error: Upload to " + serverUrl + " with payload '" + payload + "' failed.\nReturn code: " + responseEntity.getStatusCode() + " with Message " + responseEntity.getBody().toString() + ".";
        if (configuration.hasOption(Configuration.SWITCH_UPLOAD_FAILS_EXIT1)) {
            throw new RuntimeException(message);
        } else {
            log.output(message);
        }
    }
}