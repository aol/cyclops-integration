# Scala Integration


## Get cyclops-scala


* [![Maven Central : cyclops-scala](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-scala/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-scala)  [![javadoc.io](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-scala/badge.svg)](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-scala)
* [mvnrepository link](http://mvnrepository.com/artifact/com.aol.cyclops/cyclops-scala) - Maven Central not always up to date
* [Javadoc for Cyclops Scala](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-scala/)

### Maven

```xml
<dependency>
    <groupId>com.aol.cyclops</groupId>
    <artifactId>cyclops-scala</artifactId>
    <version>9.0.0-MI2</version>
</dependency>
```

### Gradle

```groovy
compile group: 'com.aol.cyclops', name: 'cyclops-scala', version: '9.0.0-MI2'
```

# Features

cyclops-scala provides Java friendly bindings to the Scala collections API. We make use of cyclops powerful lazy collection extensions and coerce Scala persistent collections to the pCollections and JDK Collections interfaces used in cyclops-react.


|  Scala collection | cyclops-scala   |  JDK Interface  | Description  |
|---|---|---|---|
| List   | ScalaListX   | List  | LinkedListX  : extended persistent linkedlist |
|  Vector | ScalaVectorX  |  List   | VectorX : extended persistent ArrayList   |
|  Queue | ScalaQueueX   | Queue  | PersistentQueueX : extended Persistent Queue  |
|  HashSet | ScalaHashSetX  |  Set  | PersistentSetX : extended Persistent Set  |
|  TreeSet | ScalaTreeSetX  |  SortedSet  | OrderedSetX : extended Persistent Ordered Set  |
|  BitSet | ScalaBitSetX  |  SortedSet  | OrderedSetX : extended Persistent Ordered Set  |
|  HashMap | ScalaHashMapX  | Map | PersistentMapX : extended Persistent Map  |
|  TreeMap | ScalaTreeMapX  |  Map | PersistentMapX : extended Persistent Map  |

1. Efficiently delegate to underlying collection (particularly when a ScalaPXXXX collection is passed as a parameter)
2. Efficiently implement missing operations (e.g. choose between minus and filter for a List)
 







