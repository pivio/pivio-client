package io.pivio;

public class Logger {

    public void verboseOutput(String text, boolean isVerbose) {
        if (isVerbose) {
            System.out.println(text);
        }
    }

    public void output(String text) {
        System.out.println(text);
    }

}
