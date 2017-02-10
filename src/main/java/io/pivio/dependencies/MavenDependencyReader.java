package io.pivio.dependencies;

import io.pivio.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.joox.JOOX.$;

@Service
class MavenDependencyReader  {

    final Logger log;

    @Autowired
    public MavenDependencyReader(Logger log) {
        this.log = log;
    }

    public List<Dependency> readDependencies(File licenceFile)  {
        List<Dependency> result = new ArrayList<>();
        try {
            $(licenceFile).children().each(dependencies -> {
                $(dependencies).children().each(dependency -> {
                    String depText = $(dependency).child("groupId").text() + ":" +
                            $(dependency).child("artifactId").text();
                    String version = $(dependency).child("version").text();
                    List<License> licenses = new ArrayList<>();
                    $(dependency).child("licenses").children("license").each(license -> {
                        licenses.add(new License($(license).child("name").text(), $(license).child("url").text()));
                    });
                    Collections.sort(licenses);
                    result.add(new Dependency(depText, version, licenses));
                });
            });
        } catch (Exception e) {
            log.output("The file " + licenceFile + " could not be read.");
        }
        return result;
    }
}
