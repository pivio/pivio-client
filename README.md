# Status

[![Build Status](https://travis-ci.org/pivio/pivio-client.svg?branch=master)](https://travis-ci.org/pivio/pivio-client)

# Docs

- [Pivio Client documentation](http://pivio.io/docs/#_client)
- [Pivio documentation](http://pivio.io/docs/)


# Requirements

Build: Java SDK > 8.x
Run: Java RE > 8.x

# Build

```
./gradlew build
```

or if you have gradle installed

```
gradle
```

The final fat jar will be in `build/libs/pivio.jar`.

# Run

In the simplest case you would just use `java -jar build/libs/pivio.jar`.
Without parameters it will look for a `pivio.yaml` in the current directory.

Pivio has some options to configure its use. 

```
usage: pivio
 -config <arg>               Defines the config for all parameters. This
                             is a properties file with some the switches
                             listed here. Default location is
                             /etc/pivio-client.properties.
 -defaultconfigname <arg>    Defines the name of your yaml metadata. The
                             suffix '.yaml' will be always appended.
                             Defaults to 'pivio'.
 -dry                        Do a dry run, do not submit anything but
                             output it to stdout.
 -file <arg>                 Full path to a file containing the data in
                             yaml format. Does not have to be named
                             pivio.yaml. This overwrites the -source
                             switch and only information in this file will
                             be collected.
 -generatejsonschema         Outputs the json schema for validation to the
                             current processed yaml file.
 -gitremote <arg>            Uses the given argument as origin for Git VCS
                             remote detection (default: origin). This is
                             useful if you have multiple remotes
                             configured and/or differently named.
 -help                       This Help.
 -manualdependencies <arg>   Defines the file which holds manual defined
                             dependencies. Defaults to:
                             pivio/dependencies.yaml.
 -out <arg>                  Output the generated json to this file.
 -outattributes <arg>        Only output these top level attributes to the
                             outfile, e.g. name,id,runtime.
 -piviofilenotfoundexit0     Fail with Exit(0) when a pivio document was
                             not found in the source directory. Default is
                             1 in such as case.
 -serviceurl <arg>           The URL of the pivio service. If this switch
                             is not supplied, no upload will happen.
                             Needs to end with `/document`.
 -source <arg>               The directory containing the pivio.yaml file.
                             Should be the root directory of the project.
 -sourcecode <arg>           Defines the directory (or comma-separated
                             directories) your source code with the build
                             file is located in. If it is relative path,
                             it is relative to the pivio.yaml file. This
                             switch can also be defined with the
                             'PIVIO_SOURCECODE' environment variable.
 -uploadfailexit1            Fail with Exit(1) when document can not be
                             uploaded. Default is 0 in such a case.
 -verbose                    Prints more information.
 -yamldir <arg>              All *.yaml files in this directory will be
                             read and each file is treated as self
                             contained definition of an artefact.

Usage: java -jar ./pivio.jar -source /home/ci/source/customerservice
```

You can define certain defaults in a properties file which will be used if it exists. The default location for this is 
`/etc/pivio.properties` (can be configured via -config switch).

You can configure the following values:
- source
- gitremote
- serviceurl
- file
- defaultfile
- yamldir
- manualdependencies

The format is key:value as in Java properties files.

# Exit codes

If a Yaml file is not a valid yaml file the client will exit with code 1.

# Format

The format is defined in https://github.com/pivio/documentation/tree/master/dataformat. At the moment this client only 
supports a definition in a single file and not a single definition splitted over multiple files.
