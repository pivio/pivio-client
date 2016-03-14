package io.pivio.dependencies;

import com.fasterxml.jackson.annotation.JsonProperty;

public class License {
    public String name;
    public String url;

    public License(@JsonProperty("name") String name, @JsonProperty("url") String url) {
        this.name = name;
        this.url = url;
    }
}
