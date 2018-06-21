package io.pivio.dependencies;

import io.pivio.Configuration;
import io.pivio.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
class MavenParentPomDependencyReader extends DependencyReaderBase {

    private String defaultLicenseFile = "target/generated-resources/licenses.xml";
    private List<String> nonMavenDirs = new ArrayList<>();

    private final MavenDependencyReader mavenDependencyReader;
    private final Logger log = new Logger();

    @Autowired
    MavenParentPomDependencyReader(MavenDependencyReader mavenDependencyReader, Configuration configuration) {
        super(configuration);
        nonMavenDirs.add("src");
        nonMavenDirs.add("target");
        nonMavenDirs.add(".svn");
        nonMavenDirs.add(".git");
        nonMavenDirs.add("vagrant");
        this.mavenDependencyReader = mavenDependencyReader;
    }

    @Override
    public List<Dependency> readDependencies(String sourceRootDirectory) {
        List<Dependency> dependencies = new ArrayList<>();
        try {
            String licenseFile = sourceRootDirectory + "/" + defaultLicenseFile;
            dependencies = mavenDependencyReader.readDependencies(new File(licenseFile));
            dependencies.addAll(readDependenciesFromSubmodules(sourceRootDirectory));
        } catch (Exception e) {
            log.output("The file " + defaultLicenseFile + " could not be read.");
        }
        List<Dependency> noDuplicates = removeDuplicates(dependencies);
        return applyFilterLists(noDuplicates);
    }

    private List<Dependency> readDependenciesFromSubmodules(String sourceDir) {
        File directory = new File(sourceDir);
        File[] files = directory.listFiles();
        List<Dependency> allSubDependencies = new ArrayList<>();
        for (File file : files) {
            if (isPotentialMavenSubModuleDirectory(file)) {
                List<Dependency> subDependencies = mavenDependencyReader.readDependencies(new File(file.getAbsolutePath() + "/" + defaultLicenseFile));
                allSubDependencies.addAll(subDependencies);
            }
        }
        return allSubDependencies;
    }

    private boolean isPotentialMavenSubModuleDirectory(File file) {
        return file.isDirectory() && !nonMavenDirs.contains(file.getName());
    }

    List<Dependency> removeDuplicates(List<Dependency> dependencies) {
        List<Dependency> clean = new ArrayList<>();
        dependencies.forEach(dependency -> {
            if (!clean.contains(dependency)) {
                clean.add(dependency);
            }
        });
        Collections.sort(clean);
        return clean;
    }
}
