# STATUS

This is alpha software.

** DO NOT USE **

# Pivio

A documentation system for microservices.

- You have many microservices? 
- You don't know who is responsbile for which system? 
- You don't know what are the dependencies?
- You have no idea about the used (open source) licenses ?
- You don't know where the build chain is?
- You don't know what the service is doing?

If you answer more than three questions above with yes:

```
       
            PIVIO
        
            is for you
```

S

# Example

See src/test/resources/pivio.yaml as an example and inspiration.

# Building

```

gradle build

```

After that the executable is located in build/libs/client-0.0.1-SNAPSHOT.jar.

Run it with

```

java -jar  build/libs/pivio.jar

```

It will print out the pivio info as json.


# License

http://www.apache.org/licenses/LICENSE-2.0
