package io.pivio.dependencies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class LicenseTest {

    ObjectMapper objectMapper;

    @Before
    public void setup() {
        objectMapper = new ObjectMapper();
    }


    @Test
    public void testIfItIsMappedToJson() throws JsonProcessingException {
        License license = new License("fullName", "url");

        String json = objectMapper.writeValueAsString(license);

        assertThat(json).isEqualTo("{\"fullName\":\"fullName\",\"url\":\"url\"}");
    }

    @Test
    public void testIfItIsMappedInAMap() throws Exception {
        Map<String, Object> document = new HashMap<>();
        List<Dependency> dependencyList = new ArrayList<>();
        ArrayList<License> licenses = new ArrayList<>();
        licenses.add(new License("name1", "url1"));
        licenses.add(new License("name2", "url2"));
        dependencyList.add(new Dependency("software", "1.0", licenses));
        document.put("dependencies", dependencyList);

        String json = objectMapper.writeValueAsString(document);

        assertThat(json).isEqualTo("{\"dependencies\":[{\"name\":\"software\",\"version\":\"1.0\",\"licenses\":[{\"fullName\":\"name1\",\"url\":\"url1\"},{\"fullName\":\"name2\",\"url\":\"url2\"}]}]}");
    }

    @Test
    public void testSortingLicenses() throws Exception {
        ArrayList<License> licenses = new ArrayList<>();
        licenses.add(new License("name2", "url2"));
        licenses.add(new License("name3", "url2"));
        licenses.add(new License("name5", "url2"));
        licenses.add(new License("name1", "url1"));
        Collections.sort(licenses);
        assertThat(licenses.get(0).fullName).isEqualTo("name1");
    }

}
