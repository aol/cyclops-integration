# Scala Integration

v8.4.0 of cyclops uses 2.12.0 of Scala

## Get cyclops-scala


* [![Maven Central : cyclops-scala](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-scala/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-scala)
* [mvnrepository link](http://mvnrepository.com/artifact/com.aol.cyclops/cyclops-scala) - Maven Central not always up to date
* [Javadoc for Cyclops Scala](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-scala/)

### Maven

```xml
<dependency>
    <groupId>com.aol.cyclops</groupId>
    <artifactId>cyclops-scala</artifactId>
    <version>8.4.0</version>
</dependency>
``
### Gradle

```groovy
compile group: 'com.aol.cyclops', name: 'cyclops-scala', version: '8.4.0'
```

# Features

cyclops-scala provides Java friendly bindings to the Scala collections API. We make use of cyclops powerful lazy collection extensions and coerce Scala persistent collections to the pCollections and JDK Collections interfaces used in cyclops-react.


|  Scala collection | cyclops-scala   | pcollections interface   | JDK Interface  | Description  |
|---|---|---|---|---|
| List   | ScalaPStack   | PStack  | List  | PStackX  : extended persistent linkedlist |
|  Vector | ScalaPVector  | PVector   | List   | PVectorX : extended persistent ArrayList   |
|  Queue | ScalaPQueue  | PQueue  | Queue  | PQueueX : extended Persistent Queue  |
|  HashSet | ScalaHashPSet  | PSet  | Set  | PSetX : extended Persistent Set  |
|  TreeSet | ScalaTreePOrderedSet  | POrderedSet  | SortedSet  | POrderedSetX : extended Persistent Ordered Set  |
|  BitSet | ScalaBitSetPOrderedSet  | POrderedSet  | SortedSet  | POrderedSetX : extended Persistent Ordered Set  |
|  HashMap | ScalaHashPMap  | PMap  | Map | PMapX : extended Persistent Map  |
|  TreeMap | ScalaTreePMap  | PMap  | Map | PMapX : extended Persistent Map  |

1. Efficiently delegate to underlying collection (particularly when a ScalaPXXXX collection is passed as a parameter)
2. Efficiently implement missing operations (e.g. choose between minus and filter for a List)
 







