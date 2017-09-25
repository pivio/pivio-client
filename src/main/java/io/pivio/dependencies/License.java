package io.pivio.dependencies;

import com.fasterxml.jackson.annotation.JsonProperty;

public class License implements Comparable {
    public String fullName;
    public String url;

    public License(@JsonProperty("fullName") String name, @JsonProperty("url") String url) {
        this.fullName = name;
        this.url = url;
    }

    @Override
    public int compareTo(Object o) {
        if (o != null && o instanceof License) {
            License otherLicense = (License) o;
            String thisInfo = fullName + url;
            String otherInfo = otherLicense.fullName + otherLicense.url;
            return thisInfo.compareTo(otherInfo);
        } else {
            return 0;
        }
    }
}
