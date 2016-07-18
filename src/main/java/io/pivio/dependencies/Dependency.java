package io.pivio.dependencies;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Dependency implements Comparable {
    public String name;
    public String version;
    @JsonProperty("licenses")
    private final List<License> licences = new ArrayList<>();

    public Dependency(String name, String version, List<License> licenses) {
        this.name = name;
        this.version = version;
        this.licences.addAll(licenses);
        Collections.sort(this.licences);
    }

    public List<License> getLicences() {
        return licences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dependency that = (Dependency) o;

        if (!name.equals(that.name)) return false;
        return version.equals(that.version);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public int compareTo(Object o) {
        return name.compareTo(((Dependency)o).name);
    }
}
