# Jackson Data Bindings for Cyclops 

## Get cyclops-jackson


* [![Maven Central : cyclops-jackson-integration](https://maven-badges.herokuapp.com/maven-central/com.oath.cyclops/cyclops-jackson-integration/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.oath.cyclops/cyclops-jackson-integration)  
* [Javadoc for cyclops-jackson-integration](http://www.javadoc.io/doc/com.oath.cyclops/cyclops-jackson-integration)


JSON Serialiazation and Deserialization of Cyclops Control and Data Types

```java
import com.oath.cyclops.jackson.CyclopsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

ObjectMapper mapper = new ObjectMapper();

mapper.registerModule(new CyclopsModule());
```
