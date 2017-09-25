package io.pivio.dependencies;

import io.pivio.Configuration;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GradleDependencyReaderTest {

    private GradleDependencyReader gradleLicenseReader;

    @Before
    public void setUp() throws Exception {
        Configuration configurationMock = mock(Configuration.class);
        gradleLicenseReader = new GradleDependencyReader(configurationMock);
        when(configurationMock.isVerbose()).thenReturn(true);
    }

    @Test
    public void testReadFileContainsTwoLicenses() throws Exception {
        List result = gradleLicenseReader.readFile(new File("src/test/resources/gradle-dependency-license.xml"));
        assertThat(result).hasSize(41);
    }

    @Test
    public void testReadFileContainsOneDependencyWith2Licenses() throws Exception {
        List<Dependency> result = gradleLicenseReader.readFile(new File("src/test/resources/gradle-dependency-license-one-dependency.xml"));
        Assertions.assertThat(result.get(0).getLicenses()).hasSize(2);
    }

    @Test
    public void testReadNonExistingFile() throws Exception {
        try {
            gradleLicenseReader.readFile(new File("src/test/resources/DOESNOTEXIST.xml"));
            fail("Should fail, this file does not exists");
        } catch (IOException e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void testReadNonXMLFile() throws Exception {
        try {
            gradleLicenseReader.readFile(new File("src/test/resources/gradle-license-non-xml-file.txt"));
            fail("Should fail, this file is not xml.");
        } catch (SAXException e) {
            assertThat(e).isNotNull();
        }
    }

    @Test
    public void testReadDependencies() throws Exception {
        List<Dependency> dependencies = gradleLicenseReader.readDependencies("src/test/resources/dependencies/gradle");
        assertThat(dependencies.size()).isEqualTo(41);
    }

    @Test
    public void testReadDependenciesNonExisting() throws Exception {
        List<Dependency> dependencies = gradleLicenseReader.readDependencies("idonot/exists");
        assertThat(dependencies).isEmpty();
    }


}