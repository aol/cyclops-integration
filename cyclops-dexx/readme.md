# Dexx Integration

Dexx is a libary for Persistent Collections for Java Ported from Scala

v8.4.0 of cyclops uses 0.6 of Dexx

## Get cyclops-dexx


* [![Maven Central : cyclops-dexx](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-dexx/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-dexx)
* [Javadoc for Cyclops Javaslang](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-dexx/)

# Features

cyclops-dexx provides a powerful API over the Dexx Scala Persistent Collections Port. We make use of cyclops powerful lazy collection extensions and coerce Dexx persistent collections to the pCollections and JDK Collections interfaces used in cyclops-react.


* Also see [Scala and cyclops collections](https://github.com/aol/cyclops-react/wiki/Scala-%26-cyclops-collections)
* Also see [JavaSlang and cyclops collections](https://github.com/aol/cyclops-react/wiki/JavaSlang-and-cyclops-collections)


|  Dexx collection | cyclops-scala   | pcollections interface   | JDK Interface  | Description  |
|---|---|---|---|---|
| List   | DexxPStack   | PStack  | List  | PStackX  : extended persistent linkedlist |
|  Vector | DexxPVector  | PVector   | List   | PVectorX : extended persistent ArrayList   |
|  HashSet | DexxPSet  | PSet  | Set  | PSetX : extended Persistent Set  |
|  TreeSet | DexxPOrderedSet  | POrderedSet  | SortedSet  | POrderedSetX : extended Persistent Ordered Set  |






