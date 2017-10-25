package io.pivio;

import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class Configuration {

    public static final String SWITCH_SOURCE_DIR = "source";
    public static final String SWITCH_GIT_REMOTE = "gitremote";
    public static final String SWITCH_SERVICE_URL = "serviceurl";
    public static final String SWITCH_HELP = "help";
    public static final String SWITCH_USE_THIS_YAML_FILE = "file";
    public static final String SWITCH_DEFAULT_YAML_FILE_NAME = "defaultconfigname";
    public static final String SWITCH_DRY_RUN = "dry";
    public static final String SWITCH_CONFIG = "config";
    public static final String SWITCH_VERBOSE = "verbose";
    public static final String SWITCH_MANUAL_DEPENDENCIES = "manualdependencies";
    public static final String SWITCH_YAML_DIR = "yamldir";
    public static final String SWITCH_VERSION = "version";
    public static final String SWITCH_UPLOAD_FAILS_EXIT1 = "uploadfailexit1";
    public static final String SWITCH_PIVIO_FILE_NOT_FOUND_EXIT0 = "piviofilenotfoundexit0";
    public static final String SWITCH_SOURCE_CODE = "sourcecode";
    public static final String SWITCH_OUTFILE = "out";
    public static final String SWITCH_GENERATE_JSON_SCHEMA = "generatejsonschema";
    public static final String SWITCH_OUTFILETOPLEVELATTRIBUTES = "outattributes";

    @Value(value = "${app.source.dir}")
    private String DEFAULT_VALUE_SOURCE_DIR = ".";
    @Value(value = "${app.git.remote}")
    private String DEFAULT_VALUE_GIT_REMOTE = "origin";
    @Value(value = "${app.server.url}")
    private String DEFAULT_VALUE_SERVICE_URL = "http://localhost:9123";
    @Value(value = "${app.yaml.file}")
    private String DEFAULT_VALUE_YAML_FILE_NAME = "pivio.yaml";
    @Value(value = "${app.config.file}")
    private String DEFAULT_CONFIG_FILE = "/etc/pivio-client.properties";
    @Value(value = "${app.dependencies.manual}")
    private String DEFAULT_MANUAL_DEPENDENCIES = "pivio/dependencies.yaml";
    @Value(value = "${dependencies.blacklist}")
    public String[] BLACKLIST = null;
    @Value(value = "${dependencies.whitelist}")
    public String[] WHITELIST = null;

    private Options options = new Options();
    private CommandLine commandLine;
    private Logger log = new Logger();

    public boolean isVerbose() {
        return commandLine.hasOption(SWITCH_VERBOSE);
    }

    public void setParameter(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public boolean hasOption(String option) {
        log.verboseOutput("Checking for hasOption: '" + option + "' result: '" + commandLine.hasOption(option) + "'.", isVerbose());
        return commandLine.hasOption(option) || (!getValueFromConfigFile(option, "THISISADUMMYVALUE").equals("THISISADUMMYVALUE"));
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
        String defaultValue;
        switch (option) {
            case SWITCH_SOURCE_DIR:
                defaultValue = getValueFromConfigFile(SWITCH_SOURCE_DIR, Paths.get(DEFAULT_VALUE_SOURCE_DIR).toAbsolutePath().normalize().toString());
                result = commandLine.getOptionValue(SWITCH_SOURCE_DIR, defaultValue);
                break;
            case SWITCH_GIT_REMOTE:
                defaultValue = getValueFromConfigFile(SWITCH_GIT_REMOTE, DEFAULT_VALUE_GIT_REMOTE);
                result = commandLine.getOptionValue(SWITCH_GIT_REMOTE, defaultValue);
                break;
            case SWITCH_SERVICE_URL:
                defaultValue = getValueFromConfigFile(SWITCH_SERVICE_URL, DEFAULT_VALUE_SERVICE_URL);
                result = commandLine.getOptionValue(SWITCH_SERVICE_URL, defaultValue);
                break;
            case SWITCH_USE_THIS_YAML_FILE:
                defaultValue = getValueFromConfigFile(SWITCH_USE_THIS_YAML_FILE, DEFAULT_VALUE_YAML_FILE_NAME);
                result = commandLine.getOptionValue(SWITCH_USE_THIS_YAML_FILE, defaultValue);
                break;
            case SWITCH_DEFAULT_YAML_FILE_NAME:
                result = commandLine.getOptionValue(SWITCH_DEFAULT_YAML_FILE_NAME, "pivio.yaml");
                break;
            case SWITCH_YAML_DIR:
                result = commandLine.getOptionValue(SWITCH_YAML_DIR, ".");
                break;
            case SWITCH_MANUAL_DEPENDENCIES:
                result = getValueFromConfigFile(SWITCH_MANUAL_DEPENDENCIES, DEFAULT_MANUAL_DEPENDENCIES);
                break;
            case SWITCH_SOURCE_CODE:
                result = commandLine.getOptionValue(SWITCH_SOURCE_CODE, "");
                if (System.getenv("PIVIO_SOURCECODE") != null && result.equals("")) {
                    result = System.getenv("PIVIO_SOURCECODE");
                    log.verboseOutput("Reading switch '" + SWITCH_SOURCE_CODE + "' from environment variable with value : '" + result + "'.", isVerbose());
                }
                break;
            case SWITCH_OUTFILE:
                result = commandLine.getOptionValue(SWITCH_OUTFILE);
                break;
            case SWITCH_OUTFILETOPLEVELATTRIBUTES:
                result = commandLine.getOptionValue(SWITCH_OUTFILETOPLEVELATTRIBUTES);
                break;
            default:
                break;
        }
        log.verboseOutput("Parameter '" + option + "' was requested and '" + result + "' was returned.", isVerbose());
        return result;
    }

    private String getValueFromConfigFile(String option, String defaultValue) {
        String result = defaultValue;

        String configFileLocation = commandLine.getOptionValue(SWITCH_CONFIG, DEFAULT_CONFIG_FILE);

        File config = new File(configFileLocation);
        boolean configFileViaSwitchDoesNotExist = !config.exists() && !config.getAbsolutePath().equals(DEFAULT_CONFIG_FILE);
        if (configFileViaSwitchDoesNotExist) {
            System.out.println("Provided config '" + configFileLocation + "' file does not exist.");
        } else {
            InputStream in = null;
            try {
                in = new FileInputStream(config);
                Properties prop = new Properties();
                prop.load(in);
                if (prop.containsKey(option)) {
                    result = prop.getProperty(option);
                }
            } catch (IOException ignored) {
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        log.verboseOutput("Option '" + option + "' was requested from config file in " + configFileLocation + "' (default: " + defaultValue + ") and returned '" + result + "'.", isVerbose());
        return result;
    }


    void outputHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("pivio", options);
        System.out.println("\nUsage: java -jar ./pivio.jar -source /home/ci/source/customerservice");
    }

    CommandLine parseCommandLine(String[] args) throws ParseException {
        options.addOption(SWITCH_VERBOSE, false, "Prints more information.");
        options.addOption(SWITCH_HELP, false, "This Help.");
        options.addOption(SWITCH_DRY_RUN, false, "Do a dry run, do not submit anything but output it to stdout.");
        options.addOption(SWITCH_UPLOAD_FAILS_EXIT1, false, "Fail with Exit(1) when document can not be uploaded. Default is 0 in such a case.");
        options.addOption(SWITCH_PIVIO_FILE_NOT_FOUND_EXIT0, false, "Fail with Exit(0) when a pivio document was not found in the source directory. Default is 1 in such as case.");
        options.addOption(SWITCH_GENERATE_JSON_SCHEMA, false, "Outputs the json schema for validation to the current processed yaml file.");
        options.addOption(SWITCH_VERSION, false, "Shows the version of the client and end the client.");

        options.addOption(SWITCH_YAML_DIR, true, "All *.yaml files in this directory will be read and each file is treated as self contained definition of an artefact.");
        options.addOption(SWITCH_SOURCE_DIR, true, "The directory containing the pivio.yaml file. Should be the root directory of the project.");
        options.addOption(SWITCH_CONFIG, true, "Defines the config for all parameters. This is a properties file with some the switches listed here. Default location is /etc/pivio-client.properties.");
        options.addOption(SWITCH_GIT_REMOTE, true, "Uses the given argument as origin for Git VCS remote detection (default: origin). This is useful if you have multiple remotes configured and/or differently named.");
        options.addOption(SWITCH_SERVICE_URL, true, "The url of the pivio service. If this switch is not supplied, no upload will happen.");
        options.addOption(SWITCH_USE_THIS_YAML_FILE, true, "Full path to a file containing the data in yaml format. Does not have to be named pivio.yaml. This overwrites the -source switch and only information in this file will be collected.");
        options.addOption(SWITCH_DEFAULT_YAML_FILE_NAME, true, "Defines the name of your yaml metadata. The suffix '.yaml' will be always appended. Defaults to 'pivio'.");
        options.addOption(SWITCH_MANUAL_DEPENDENCIES, true, "Defines the file which holds manual defined dependencies. Defaults to: pivio/dependencies.yaml.");
        options.addOption(SWITCH_SOURCE_CODE, true, "Defines the directory (or comma-separated directories) your source code with the build file is located in. If it is relative path, it is relative to the pivio.yaml file. This switch can also be defined with the 'PIVIO_SOURCECODE' environment variable.");
        options.addOption(SWITCH_OUTFILE, true, "Output the generated json to this file.");
        options.addOption(SWITCH_OUTFILETOPLEVELATTRIBUTES, true, "Only output these top level attributes to the outfile, e.g. name,id,runtime.");

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    // FIXME: Crazy! The persons that developed something automatic for version display etc. hardcoded this!
    public void outputVersion() {
        String version = "0.2";
        String gitHash = "DEV-VERSION";
        InputStream in = this.getClass().getResourceAsStream("/VERSION");
        if (in != null) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(in))) {
                gitHash = buffer.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                log.output("There was an error getting the version.");
            }
        }
        System.out.println("Pivio client version: " + version + " githash: " + gitHash);

    }
}
