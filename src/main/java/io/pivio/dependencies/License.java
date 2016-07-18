package io.pivio.dependencies;

import com.fasterxml.jackson.annotation.JsonProperty;

public class License implements Comparable {
    public String name;
    public String url;

    public License(@JsonProperty("name") String name, @JsonProperty("url") String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof License) {
            License otherLicense = (License) o;
            String thisInfo = name + url;
            String otherInfo = otherLicense.name + otherLicense.url;
            return thisInfo.compareTo(otherInfo);
        } else {
            return 0;
        }
    }
}
