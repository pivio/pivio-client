package io.pivio.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivio.Configuration;
import io.pivio.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class SchemaValidatorTest {

    SchemaValidator validator;
    ObjectMapper objectMapper;
    Logger loggerMock;

    @Before
    public void setUp() throws Exception {
        Configuration configurationMock = mock(Configuration.class);
        validator = new SchemaValidator(configurationMock);
        objectMapper = new ObjectMapper();
        loggerMock = mock(Logger.class);
        validator.log = loggerMock;
    }

    @Test
    public void validateMinimalJson() throws Exception {
        JsonNode jsonNode = getJsonNodeFromFile("src/test/resources/schema/minimal-ok.json");

        boolean validResult = validator.isValid(jsonNode);
        assertThat(validResult).isTrue();
    }

    @Test
    public void validateWithMissingRequieredAttributes() throws Exception {
        JsonNode jsonNode = getJsonNodeFromFile("src/test/resources/schema/minimal-error.json");

        boolean validResult = validator.isValid(jsonNode);
        assertThat(validResult).isFalse();
    }

    @Test
    public void validateWithWrongServiceDependsOnStructure() throws Exception {
        JsonNode jsonNode = getJsonNodeFromFile("src/test/resources/schema/violating-schema-in-depends-on.json");

        boolean validResult = validator.isValid(jsonNode);
        assertThat(validResult).isFalse();
    }

    @Test
    public void validateLogOutput() throws Exception {
        JsonNode jsonNode = getJsonNodeFromFile("src/test/resources/schema/validate-log.json");

        validator.isValid(jsonNode);
        verify(loggerMock, times(1)).output("/service/depends_on : instance type (array) does not match any allowed primitive type (allowed: [\"object\"])");
    }

    private JsonNode getJsonNodeFromFile(String testFile) throws IOException {
        File file = new File(testFile);
        return objectMapper.readTree(file);
    }
}