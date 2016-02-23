# ServiceCat 

A documentation system for microservices.

- You have many microservices? 
- You don't know who is responsbile for which system? 
- You don't know what are the dependencies?
- You have no idea about the used (open source) licenses ?
- You don't know where the build chain is?
- You don't know what the service is doing?

If you answer more than three questions above with yes:

```
       
            SERVICECAT
        
            is for you
```

ServiceCatalog collects all the information about your software artefact and uploads it to the ServiceCatalog Server.
The idea is that a team can document the key characteristics about their software within their IDE. All the information
  which does not change that often should be included in this file. But when it changes it is easy to do so. We came up
  with a set of attributes which fit our needs. We think this is suitable for others as well. ServiceCatalog does not
  force anything. You can use these attributes but you don't have to. They are a suggestion.
  
## The Client


The client is intended to run in the buildchain of your software. Every time if something is committed which triggers
your buildchain your data will be updated. For that the software needs to have a 'servicecatalog.yaml' file in the root
folder of it. 
  
  
The client does several things:

- reads the servicecatalog.yaml file
- Tries to guess the 


# Example

See src/test/resources/servicecat.yaml as an example and inspiration.

# Building

```

gradle build

```

After that the executable is located in build/libs/client-0.0.1-SNAPSHOT.jar.

Run it with

```

java -jar  build/libs/servicecat.jar

```

It will print out the servicecat info as json.


# License

http://www.apache.org/licenses/LICENSE-2.0
