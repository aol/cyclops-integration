# Scala Integration

v8.4.0 of cyclops uses 2.12.0 of Scala

## Get cyclops-scala


* [![Maven Central : cyclops-scala](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-scala/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-scala)
* [Javadoc for Cyclops Javaslang](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-scala/)

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







