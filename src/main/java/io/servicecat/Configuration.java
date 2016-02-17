package io.servicecat;

import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

@Service
public class Configuration {

    public final static String SWITCH_SOURCE_DIR = "source";
    public final static String SWITCH_GIT_REMOTE = "gitremote";
    public final static String SWITCH_SERVER_URL = "server";
    public final static String SWITCH_HELP = "help";
    public final static String SWITCH_USE_THIS_YAML_FILE = "file";
    public final static String SWITCH_DEFAULT_YAML_FILE_NAME = "defaultfile";
    public final static String SWITCH_DRY_RUN = "dry";
    public final static String SWITCH_CONFIG = "config";
    public final static String SWITCH_VERBOSE = "verbose";
    public final static String SWITCH_MANUAL_DEPENDENCIES = "manualdependencies";
    public final static String SWITCH_YAML_DIR = "yamldir";

    @Value(value = "${app.source.dir}")
    private String DEFAULT_VALUE_SOURCE_DIR = ".";
    @Value(value = "${app.git.remote}")
    private String DEFAULT_VALUE_GIT_REMOTE = "origin";
    @Value(value = "${app.server.url}")
    private String DEFAULT_VALUE_SERVER_URL = "http://localhost:9123";
    @Value(value = "${app.yaml.file}")
    private String DEFAULT_VALUE_YAML_FILE_NAME = "servicecat.yaml";
    @Value(value = "${app.config.file}")
    private String DEFAULT_CONFIG_FILE = "/etc/servicecat.properties";
    @Value(value = "${app.dependencies.manual}")
    private String DEFAULT_MANUAL_DEPENDENCIES = "servicecat/dependencies.yaml";

    private Options options = new Options();

    private CommandLine commandLine;

    public boolean isVerbose() {
        return commandLine.hasOption(SWITCH_VERBOSE);
    }

    public void setParameter(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public boolean hasOption(String option) {
        return commandLine.hasOption(option);
    }

    public String getYamlFilePath() {
        String result = Paths.get(getParameter(SWITCH_DEFAULT_YAML_FILE_NAME)).toAbsolutePath().toString();
        if (commandLine.hasOption(SWITCH_SOURCE_DIR)) {
            result = getParameter(SWITCH_SOURCE_DIR) + "/" + getParameter(SWITCH_DEFAULT_YAML_FILE_NAME);
        }
        if (commandLine.hasOption(SWITCH_USE_THIS_YAML_FILE)) {
            result = Paths.get(commandLine.getOptionValue(SWITCH_USE_THIS_YAML_FILE)).toAbsolutePath().toString();
        }
        return result;
    }

    public String getParameter(String option) {
        String result = "";
        String defaultValue = "";
        switch (option) {
            case SWITCH_SOURCE_DIR:
                defaultValue = getValueFromConfigFile(SWITCH_SOURCE_DIR, Paths.get(DEFAULT_VALUE_SOURCE_DIR).toAbsolutePath().normalize().toString());
                result = commandLine.getOptionValue(SWITCH_SOURCE_DIR, defaultValue);
                break;
            case SWITCH_GIT_REMOTE:
                defaultValue = getValueFromConfigFile(SWITCH_GIT_REMOTE, DEFAULT_VALUE_GIT_REMOTE);
                result = commandLine.getOptionValue(SWITCH_GIT_REMOTE, defaultValue);
                break;
            case SWITCH_SERVER_URL:
                defaultValue = getValueFromConfigFile(SWITCH_SERVER_URL, DEFAULT_VALUE_SERVER_URL);
                result = commandLine.getOptionValue(SWITCH_SERVER_URL, defaultValue);
                break;
            case SWITCH_USE_THIS_YAML_FILE:
                defaultValue = getValueFromConfigFile(SWITCH_USE_THIS_YAML_FILE, DEFAULT_VALUE_YAML_FILE_NAME);
                result = commandLine.getOptionValue(SWITCH_USE_THIS_YAML_FILE, defaultValue);
                break;
            case SWITCH_DEFAULT_YAML_FILE_NAME:
                result = commandLine.getOptionValue(SWITCH_DEFAULT_YAML_FILE_NAME, "servicecat.yaml");
                break;
            case SWITCH_YAML_DIR:
                result = commandLine.getOptionValue(SWITCH_YAML_DIR, ".");
                break;
            case SWITCH_MANUAL_DEPENDENCIES:
                result = getValueFromConfigFile(SWITCH_MANUAL_DEPENDENCIES, DEFAULT_MANUAL_DEPENDENCIES);
                break;
            default:
                break;
        }
        return result;
    }

    private String getValueFromConfigFile(String option, String defaultValue) {
        String result = defaultValue;

        String configFileLocation = commandLine.getOptionValue(SWITCH_CONFIG, DEFAULT_CONFIG_FILE);

        File config = new File(configFileLocation);
        boolean configFileViaSwitchDoesNotExist = !config.exists() && !config.getAbsolutePath().equals(DEFAULT_CONFIG_FILE);
        if (configFileViaSwitchDoesNotExist) {
            System.out.println("Provided config '" + configFileLocation + "' file does not exist.");
        }
        try {
            InputStream in = new FileInputStream(config);
            Properties prop = new Properties();
            prop.load(in);
            if (prop.containsKey(option)) {
                result = prop.getProperty(option);
            }
        } catch (IOException ignored) {
        }

        return result;
    }

    public void outputHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("servicecat", options);
        System.out.println("\nUsage: java -jar ./servicecat.jar -source /home/ci/source/customerservice");
    }

    public CommandLine parseCommandLine(String[] args) throws ParseException {
        options.addOption(SWITCH_VERBOSE, false, "Prints more information.");
        options.addOption(SWITCH_HELP, false, "This Help.");
        options.addOption(SWITCH_DRY_RUN, false, "Do a dry run, do not submit anything but output it to stdout.");

        options.addOption(SWITCH_YAML_DIR, true, "All *.yaml files in this directory will be read.");
        options.addOption(SWITCH_SOURCE_DIR, true, "The directory with the sources containing the servicecat.yaml file.");
        options.addOption(SWITCH_CONFIG, true, "Defines the config for all parameters. This is a properties file with all the switched listed here.");
        options.addOption(SWITCH_GIT_REMOTE, true, "Uses the given argument as origin for Git VCS remote detection (default: origin). This is useful if you have multiple remotes configured and/or differently named.");
        options.addOption(SWITCH_SERVER_URL, true, "The location of the servicecat server (default: " + DEFAULT_VALUE_SERVER_URL + ").");
        options.addOption(SWITCH_USE_THIS_YAML_FILE, true, "Full path to a file containing the data in yaml format. Does not have to be named servicecat.yaml. This overwrites the -source switch and only information in this file will be collected.");
        options.addOption(SWITCH_DEFAULT_YAML_FILE_NAME, true, "Defines the filename of your yaml metadata.");
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }
}
