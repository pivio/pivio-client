package io.pivio.dependencies;

import io.pivio.Configuration;
import io.pivio.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.joox.JOOX.$;

@Service
class GradleDependencyReader implements DependencyReader {

    final Configuration configuration;
    final Logger log;

    @Autowired
    public GradleDependencyReader(Configuration configuration, Logger log) {
        this.configuration = configuration;
        this.log = log;
    }

    public List<Dependency> readDependencies(String sourceRootDirectory) {
        String defaultLicenseFile = "build/reports/license/dependency-license.xml";
        try {
            return readFile(new File(sourceRootDirectory + "/" + defaultLicenseFile));
        } catch (Exception e) {
            log.verboseOutput("The file " + defaultLicenseFile + " could not be read.");
        }
        return new ArrayList<>();
    }

    List<Dependency> readFile(File licenceFile) throws Exception {
        List<Dependency> result = new ArrayList<>();
        $(licenceFile).children("dependency").each(dependency -> {
            String depName = $(dependency).attr("name").split(":")[0] + ":" + $(dependency).attr("name").split(":")[1];
            String depVersion = $(dependency).attr("name").split(":")[2];
            ArrayList<License> licensesForDependency = new ArrayList<>();
            $(dependency).children("license").each(license -> {
                licensesForDependency.add(new License($(license).attr("name"), $(license).attr("url")));
            });
            Collections.sort(licensesForDependency);
            result.add(new Dependency(depName, depVersion, licensesForDependency));
        });
        log.verboseOutput("Found " + result.size() + " dependencies.");
        return result;
    }
}
