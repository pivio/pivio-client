package io.pivio.dependencies;

import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class DependencyTest {

    @Test
    public void testSorting() {
        ArrayList<License> licenses = new ArrayList<>();
        licenses.add(new License("name2", "url2"));
        licenses.add(new License("name3", "url2"));
        licenses.add(new License("name5", "url2"));
        licenses.add(new License("name1", "url1"));

        Dependency dependency = new Dependency("softwarename", "versionstring", licenses);

        assertThat(dependency.getLicences().get(0).name).isEqualTo("name1");
    }

}