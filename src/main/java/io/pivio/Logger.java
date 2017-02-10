package io.pivio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Logger {

    private final Configuration configuration;

    @Autowired
    public Logger(Configuration configuration) {
        this.configuration = configuration;
    }

    public void verboseOutput(String text) {
        if (configuration.isVerbose()) {
            System.out.println(text);
        }
    }

    public void output(String text) {
        System.out.println(text);
    }

}
