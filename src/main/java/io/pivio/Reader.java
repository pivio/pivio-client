package io.pivio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
class Reader {

    @Autowired
    Configuration configuration;

    @Autowired
    Logger log;

    Map<String, Object> makeLowerCaseKeys(final Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        Set<String> keys = map.keySet();
        keys.forEach(key -> {
            Object value = map.get(key);
            result.put(key.toLowerCase(), value);
        });
        log.verboseOutput("Yaml file has " + result.size() + " entries.");
        return result;
    }

    Map<String, Object> readYamlFile(String yamlfile) throws FileNotFoundException {
        InputStream input = new FileInputStream(new File(yamlfile));
        Yaml yaml = new Yaml();
        Object data = yaml.load(input);
        if (data instanceof Map) {
            return makeLowerCaseKeys((Map<String, Object>) data);
        } else {
            return Collections.emptyMap();
        }
    }
}
