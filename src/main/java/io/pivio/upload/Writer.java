package io.pivio.upload;

import static io.pivio.Configuration.SWITCH_OUTFILE;
import static io.pivio.Configuration.SWITCH_OUTFILETOPLEVELATTRIBUTES;
import static io.pivio.Configuration.SWITCH_PROXY_HOSTNAME;
import static io.pivio.Configuration.SWITCH_PROXY_PORT;
import static io.pivio.Configuration.SWITCH_PROXY_PWD;
import static io.pivio.Configuration.SWITCH_PROXY_TYPE;
import static io.pivio.Configuration.SWITCH_PROXY_USER;
import static io.pivio.Configuration.SWITCH_SERVICE_URL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivio.Configuration;
import io.pivio.Logger;
import io.pivio.PivioYamlParserException;
import io.pivio.schema.SchemaValidator;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.io.IOException;
import java.net.Proxy.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

@Service
public class Writer {

    Logger log = new Logger();
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
                            log.verboseOutput("Filtering output to file with attribute '" + attribute + "'.",
                                configuration.isVerbose());
                            writeToFile.put(attribute, document.get(attribute));
                        } else {
                            log.verboseOutput("Could not find requested top level attribute '" + attribute + "'.",
                                configuration.isVerbose());
                        }
                    }
                } else {
                    writeToFile = document;
                }
                mapper.writeValue(new File(fileName), writeToFile);
                log.verboseOutput("Wrote output to file '" + fileName + "'.", configuration.isVerbose());
            } catch (IOException e) {
                log.output("Error writing file " + fileName);
                throw new IllegalArgumentException("Could not write to file '" + fileName + "'.");
            }
        }
    }

    private void uploadToServer(String json) {
        if (configuration.hasOption(SWITCH_SERVICE_URL)) {

            String serviceUrl = configuration.getParameter(SWITCH_SERVICE_URL);
            log.verboseOutput("Uploading  to " + serviceUrl + ": " + json, configuration.isVerbose());

            RestTemplate rt = getRestTemplate();
            rt.setErrorHandler(new RestCallErrorHandler());
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            try {
                ResponseEntity<JsonNode> responseEntity =
                    rt.exchange(serviceUrl, HttpMethod.POST, new HttpEntity<>(json, headers), JsonNode.class);
                if (responseEntity.getStatusCode() != HttpStatus.CREATED) {
                    handleNonCreatedStatusCode(serviceUrl, responseEntity, json);
                } else {
                    log.verboseOutput("Upload to " + serviceUrl + " successful.", configuration.isVerbose());
                }
            } catch (ResourceAccessException e) {
                handleConnectionRefused(serviceUrl);
            }
        } else {
            log.verboseOutput("Not uploading to any server since no '" + SWITCH_SERVICE_URL + "' parameter was specified.",
                configuration.isVerbose());
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
        String message = "Error: Upload to " + serverUrl + " with payload '" + payload + "' failed.\nReturn code: "
            + responseEntity.getStatusCode() + " with Message " + responseEntity.getBody().toString() + ".";
        if (configuration.hasOption(Configuration.SWITCH_UPLOAD_FAILS_EXIT1)) {
            throw new RuntimeException(message);
        } else {
            log.output(message);
        }
    }

    private RestTemplate getRestTemplate() {

        String hostname, username = null, password = null; // password could be null
        int port = 8080;
        Type proxyType = Type.HTTP;
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        if (configuration.hasOption(SWITCH_PROXY_HOSTNAME)) {
            hostname = configuration.getParameter(SWITCH_PROXY_HOSTNAME);

            // getting proxy port
            if (configuration.hasOption(SWITCH_PROXY_PORT)) {
                OptionalInt portO = tryParseInt(configuration.getParameter(SWITCH_PROXY_PORT));
                if (!portO.isPresent()) {
                    throw new RuntimeException("Proxy port was wrong");
                } else {
                    port = portO.getAsInt();
                }
            }

            // getting proxy type
            if (configuration.hasOption(SWITCH_PROXY_TYPE)) {
                switch (configuration.getParameter(SWITCH_PROXY_TYPE).toUpperCase()) {
                    case "SOCKET":
                        proxyType = Type.SOCKS;
                        break;
                    case "DIRECT":
                        proxyType = Type.DIRECT;
                        break;
                }
            }

            HttpClientBuilder clientBuilder = HttpClientBuilder.create();
            HttpHost proxy = new HttpHost(hostname, port);
            clientBuilder.setProxy(proxy);

            // check & set proxy credentials
            if (configuration.hasOption(SWITCH_PROXY_USER)) {
                username = configuration.getParameter(SWITCH_PROXY_USER);
                if (!username.isEmpty()) {
                    if (configuration.hasOption(SWITCH_PROXY_PWD)) {
                        password = configuration.getParameter(SWITCH_PROXY_PWD);
                    }
                    // set credentials
                    CredentialsProvider credsProvider = new BasicCredentialsProvider();
                    credsProvider.setCredentials(
                        new AuthScope(hostname, port),
                        new UsernamePasswordCredentials(username, password));
                    clientBuilder.setDefaultCredentialsProvider(credsProvider).disableCookieManagement();
                }
            }

            log.output("Using proxy: ON");
            log.output("\t Url => " + hostname);
            log.output("\t Port => " + String.valueOf(port));
            log.output("\t Type => " + proxyType.toString());
            log.output("\t Username => " + username);

            HttpClient httpClient = clientBuilder.build();
            factory.setHttpClient(httpClient);
        }

        RestTemplate rt = new RestTemplate(factory);
        rt.setErrorHandler(new RestCallErrorHandler());
        return rt;
    }

    private OptionalInt tryParseInt(String inputValue) {
        try {
            return OptionalInt.of(Integer.parseInt(inputValue));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}