package io.pivio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

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

    private Logger log = new Logger();

    Map<String, Object> readYamlFile(String yamlfile) throws FileNotFoundException {
        File file = new File(yamlfile);
        if (file.exists()) {
            InputStream input = new FileInputStream(file);
            Yaml yaml = new Yaml();
            Object data;
            try {
                data = yaml.load(input);
            } catch (YAMLException ye) {
                throw new PivioYamlParserException("Data in " + yamlfile + " is not valid: "+ye.getMessage());
            }
            if (data instanceof Map) {
                Map<String,Object> result = makeLowerCaseKeys((Map<String, Object>) data);
                if (configuration.hasOption(Configuration.SWITCH_ADD_FIELD)) {
                    String parameterInput = configuration.getParameter(Configuration.SWITCH_ADD_FIELD);
                    if (!parameterInput.contains("=")) {
                        throw new PivioYamlParserException("Data in CommandLine input is not valid: SWITCH_ADD_FIELD " + parameterInput);
                    }
                    result.put(parameterInput.split("=")[0],parameterInput.split("=")[1]);
                }
                return result;
            } else {
                throw new PivioYamlParserException("Data in " + yamlfile + " is not valid.");
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
        log.verboseOutput("Yaml file has " + result.size() + " entries.", configuration.isVerbose());
        return result;
    }


}
