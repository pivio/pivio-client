package io.servicecat.dependencies;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import io.servicecat.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.servicecat.Configuration.SWITCH_MANUAL_DEPENDENCIES;

@Service
public class ManualDependencyReader implements DependencyReader {

    @Autowired
    public Configuration configuration;

    @Override
    public List<Dependency> readDependencies(String sourceRootDirectory) {
        File dependency = new File(sourceRootDirectory + "/" + configuration.getParameter(SWITCH_MANUAL_DEPENDENCIES));
        return readFile(dependency);
    }

    List<Dependency> readFile(File file) {
        List<Dependency> result = new ArrayList<>();
        try {
            YamlReader yamlReader = new YamlReader(new FileReader(file));
            Object object = yamlReader.read();
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
        } catch (FileNotFoundException e) {
            System.out.println("File '" + file.getAbsolutePath() + "' could not be found.");
        } catch (YamlException e) {
            System.out.println("File '" + file.getAbsolutePath() + "' could not be parsed. Is it in YAML format?");
        }

        return result;
    }

    private String getValueFromHash(Map map, String key) {
        if (map.containsKey(key)) {
            return (String) map.get(key);
        }
        return "";
    }
}
