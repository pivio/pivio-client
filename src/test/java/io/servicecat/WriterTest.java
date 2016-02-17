package io.servicecat;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;


public class WriterTest {

    @Test
    public void testUTFDeserialize() throws IOException {
        Map<String, Object> document = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> values = new ArrayList<>();
        values.add("Ä");
        values.add("B");
        document.put("demo", values);
        document.put("Ä", "Ö");
        String valueAsString = mapper.writeValueAsString(document);

        Map<String, Object> result = mapper.readValue(valueAsString, Map.class);

        assertThat(result).isEqualTo(document);
    }
}