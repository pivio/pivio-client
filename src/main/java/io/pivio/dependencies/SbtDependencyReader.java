package io.pivio.dependencies;

import io.pivio.Configuration;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
class SbtDependencyReader extends DependencyReaderBase {

    private String sbtLicenseDirectoryName = "target/license-reports/"; // and all *.md files in there
    private int NUMBER_OF_LICENSE_INFO_FIELDS = 4;

    public SbtDependencyReader(Configuration configuration) {
        super(configuration);
    }

    @Override
    public List<Dependency> readDependencies(String sourceRootDirectory) {
        List<Dependency> result = new ArrayList<>();
        File sbtLicenseDirectory = new File(sourceRootDirectory + "/" + sbtLicenseDirectoryName);

        if (sbtLicenseDirectory.exists()) {
            File[] files = getLicenseFiles(sbtLicenseDirectory);
            for (File file : files) {
                List<String> strings = getLinesOfFile(file);
                boolean startParsing = false;
                for (String line : strings) {
                    boolean doWeHaveTheRightNumberOfFields = (line.split("\\|")).length == NUMBER_OF_LICENSE_INFO_FIELDS;
                    if (startParsing && doWeHaveTheRightNumberOfFields) {
                        result.add(getDependency(line));
                    }
                    if (line.contains("--- | --- | --- | ---")) {
                        startParsing = true;
                    }
                }
            }
        }
        return removeDuplicates(result);
    }

    private Dependency getDependency(String line) {
        String[] splitIntoFields = line.split("\\|");
        String[] dependencyInfo = splitIntoFields[2].split("#");
        String dependencyPackage = dependencyInfo[0].trim() + ":" + dependencyInfo[1].trim();
        String dependencyVersion = dependencyInfo[2].trim();
        String licenseName = getCharsBetween(splitIntoFields[1], "\\[", "\\]");
        String licenseUrl = getCharsBetween(splitIntoFields[1], "\\(", "\\)");

        ArrayList<License> licenses = new ArrayList<>();
        licenses.add(new License(licenseName, licenseUrl));
        return new Dependency(dependencyPackage, dependencyVersion, licenses);
    }

    private List<String> getLinesOfFile(File file) {
        List<String> strings;
        try {
            strings = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            return new ArrayList<>();
        }
        return strings;
    }

    private File[] getLicenseFiles(File sbtLicenseDirectory) {
        return sbtLicenseDirectory.listFiles((dir, name) -> {
            return name.toLowerCase().endsWith(".md");
        });
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

    private String getCharsBetween(String line, String char1, String char2) {
        return (line.split(char1)[1]).split(char2)[0];
    }


}
