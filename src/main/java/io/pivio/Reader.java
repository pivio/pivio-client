package io.pivio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
class Reader {

    @Autowired
    Configuration configuration;

    @Autowired
    Logger log;

    Map<String, Object> readYamlFile(String yamlfile) throws FileNotFoundException {
        File file = new File(yamlfile);
        if (file.exists()) {
            InputStream input = new FileInputStream(file);
            Yaml yaml = new Yaml();
            Object data = yaml.load(input);
            if (data instanceof Map) {
                return makeLowerCaseKeys((Map<String, Object>) data);
            } else {
                throw new FileNotFoundException("Data in " + yamlfile + " is not valid.");
            }
        } else {
            throw new FileNotFoundException("Could not find " + yamlfile + ".");
        }
    }

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


}
