# Scala Integration

v8.4.0 of cyclops uses 1.8.0 of Clojure

## Get cyclops-clojure


* [![Maven Central : cyclops-scala](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-clojure/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-clojure)
* [Javadoc for Cyclops Javaslang](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-clojure/)

# Features

cyclops-clojure provides Java friendly bindings to the Clojure collections API. We make use of cyclops powerful lazy collection extensions and coerce Clojure persistent collections to the pCollections and JDK Collections interfaces used in cyclops-react.


|  Scala collection | cyclops-scala   | pcollections interface   | JDK Interface  | Description  |
|---|---|---|---|---|
| List   | ClourePStack   | PStack  | List  | PStackX  : extended persistent linkedlist |
|  Vector | ClojurePVector  | PVector   | List   | PVectorX : extended persistent ArrayList   |
|  Queue | ClojurePQueue  | PQueue  | Queue  | PQueueX : extended Persistent Queue  |
|  HashSet | ClojureHashPSet  | PSet  | Set  | PSetX : extended Persistent Set  |
|  TreeSet | ClojureTreePOrderedSet  | POrderedSet  | SortedSet  | POrderedSetX : extended Persistent Ordered Set  |
|  BitSet | ClojureBitSetPOrderedSet  | POrderedSet  | SortedSet  | POrderedSetX : extended Persistent Ordered Set  |
|  HashMap | ClojureHashPMap  | PMap  | Map | PMapX : extended Persistent Map  |







