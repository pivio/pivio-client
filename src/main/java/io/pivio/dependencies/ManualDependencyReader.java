package io.pivio.dependencies;

import io.pivio.Configuration;
import io.pivio.Logger;
import io.pivio.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.pivio.Configuration.SWITCH_MANUAL_DEPENDENCIES;

@Service
class ManualDependencyReader implements DependencyReader {

    public final Configuration configuration;
    private final Logger log = new Logger();

    @Autowired
    public ManualDependencyReader(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public List<Dependency> readDependencies(String sourceRootDirectory) {
        File dependency = new File(sourceRootDirectory + "/" + configuration.getParameter(SWITCH_MANUAL_DEPENDENCIES));
        return readFile(dependency);
    }

    List<Dependency> readFile(File file) {
        List<Dependency> result = new ArrayList<>();
        try (InputStream input = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Object object = yaml.load(input);
            if (object instanceof ArrayList) {
                ArrayList entries = (ArrayList) object;
                for (Object o : entries) {
                    if (o instanceof HashMap) {
                        Map map = (Map) o;
                        String depName = getValueFromHash(map, "name");
                        String depVersion = getValueFromHash(map, "version");
                        String licName = getValueFromHash(map, "license");
                        String licUrl = getValueFromHash(map, "url");
                        ArrayList<License> licenses = new ArrayList<>();
                        licenses.add(new License(licName, licUrl));
                        Dependency dependency = new Dependency(depName, depVersion, licenses);
                        result.add(dependency);
                    }
                }
            }
        } catch (IOException e) {
            log.output("File '" + file.getAbsolutePath() + "' could not be found.");
        }

        return result;
    }

    private String getValueFromHash(Map map, String key) {
        if (map.containsKey(key)) {
            return map.get(key).toString();
        }
        return "";
    }
}
