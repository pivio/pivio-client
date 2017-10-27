package io.pivio.dependencies;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivio.Configuration;
import io.pivio.Logger;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class NpmDependencyReader implements DependencyReader {

    private final Logger log = new Logger();
    private final Configuration configuration;

    @Autowired
    public NpmDependencyReader(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public List<Dependency> readDependencies(String sourceRootDirectory) {
        String dependenciesFilePath = sourceRootDirectory + "/dependencies.json";
        try {
            Map<String, Object> dependencies = new ObjectMapper().readerFor(Map.class).readValue(new File(dependenciesFilePath));
            return applyBlackList(applyWhiteList(convertToDependencies(dependencies)));
        } catch (IOException e) {
            log.output("The file " + dependenciesFilePath + " could not be read.");
            return Collections.emptyList();
        }
    }

    List<Dependency> convertToDependencies(Map<String, Object> source) {
        return source.entrySet().stream()
                .map(entry ->
                        convertToDependency(entry.getKey(), entry.getValue())
                )
                .collect(Collectors.toList());
    }

    private Dependency convertToDependency(String key, Object value) {
        String name;
        String version;
        if(key.contains("@")) {
            name = key.substring(0, key.lastIndexOf("@"));
            version = key.substring(key.lastIndexOf("@") + 1);
        } else {
            name = key;
            version = "UNKNOWN";
        }
        return new Dependency(name, version, this.collectLicenses(value));
    }

    private List<License> collectLicenses(Object value) {
        if (value instanceof Map) {
            Object licenses = ((Map) value).get("licenses");
            if (licenses != null) {
                if (licenses instanceof List) {
                    return (List<License>) ((List) licenses).stream()
                            .map(license -> new License(license.toString(), getLicenseUrl(license.toString())))
                            .sorted()
                            .collect(Collectors.toList());
                } else {
                    return Arrays.asList(new License(licenses.toString(), getLicenseUrl(licenses.toString())));
                }
            }
        }
        return Collections.emptyList();
    }

    private String getLicenseUrl(String license) {
        return "http://choosealicense.com/licenses/" + license;
    }

    private List<Dependency> applyWhiteList(List<Dependency> dependencies) {
        List<Dependency> result = new ArrayList<>();
        if (configuration.WHITELIST.length > 0) {
            for (Dependency dependency : dependencies) {
                boolean onWhiteList = false;
                for (String regex : configuration.WHITELIST) {
                    if (dependency.name.matches(regex)){
                        onWhiteList = true;
                        continue;
                    }
                }
                if (onWhiteList) {
                    result.add(dependency);
                }
            }
        } else {
            result = dependencies;
        }
        return result;
    }

    private List<Dependency> applyBlackList(List<Dependency> dependencies) {
        List<Dependency> result = new ArrayList<>();
        if (configuration.BLACKLIST.length > 0) {
            for (Dependency dependency : dependencies) {
                boolean onBlackList = false;
                for (String regex : configuration.BLACKLIST) {
                    if (dependency.name.matches(regex)){
                        onBlackList = true;
                        continue;
                    }
                }
                if (!onBlackList) {
                    result.add(dependency);
                }
            }
        } else {
            result = dependencies;
        }
        return result;
    }



}
