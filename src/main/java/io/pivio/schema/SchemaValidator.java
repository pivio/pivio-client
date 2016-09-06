package io.pivio.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.pivio.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class SchemaValidator {

    @Autowired
    Logger log;

    public boolean isValid(JsonNode jsonNode) {
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        try {
            String schemaContent = readFromJARFile("/pivio-schema.json");
            JsonNode schemaNode = new ObjectMapper().readTree(schemaContent);
            final JsonSchema jsonSchema = factory.getJsonSchema(schemaNode);
            ProcessingReport processingReport = jsonSchema.validate(jsonNode);
            for (ProcessingMessage processingMessage : processingReport) {
                String pointer = processingMessage.asJson().get("instance").get("pointer").asText();
                log.output(pointer + " : " + processingMessage.getMessage());
            }
            return processingReport.isSuccess();
        } catch (ProcessingException e) {
            return false;
        } catch (UnsupportedEncodingException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String readFromJARFile(String filename)
            throws IOException {
        InputStream is = getClass().getResourceAsStream(filename);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        isr.close();
        is.close();
        return sb.toString();
    }
}
