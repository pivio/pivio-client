package io.pivio;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
class Reader {

    @Autowired
    Configuration configuration;

    Map<String, Object> readYamlFile(String yamlFile) throws FileNotFoundException,  UnsupportedEncodingException {
        if (configuration.isVerbose()) {
            System.out.println("Loading Yaml file " + yamlFile);
        }
        File file = new File(yamlFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        YamlReader yamlReader = new YamlReader(in);
        Object object = null;
        try {
            object = yamlReader.read();
        } catch (YamlException e) {
            System.out.println("Error: Yamlfile "+yamlFile);
            e.printStackTrace();
        }

        if (object instanceof Map) {
            return makeLowerCaseKeys((Map<String, Object>) object);
        } else {
            return Collections.emptyMap();
        }
    }

    Map<String, Object> makeLowerCaseKeys(final Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        Set<String> keys = map.keySet();
        keys.forEach(key -> {
            Object value = map.get(key);
            result.put(key.toLowerCase(), value);
        });
        if (configuration.isVerbose()) {
            System.out.println("Yaml file has " + result.size() + " entries.");
        }
        return result;
    }
}
