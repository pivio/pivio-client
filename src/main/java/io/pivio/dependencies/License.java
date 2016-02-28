package io.pivio.dependencies;

import org.codehaus.jackson.annotate.JsonProperty;

public class License {
    public String name;
    public String url;

    public License(@JsonProperty("name") String name, @JsonProperty("url") String url) {
        this.name = name;
        this.url = url;
    }
}
