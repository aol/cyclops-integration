# Clojure Integration

v8.4.0 of cyclops uses 1.8.0 of Clojure

## Get cyclops-clojure


* [![Maven Central : cyclops-scala](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-clojure/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-clojure)   [![javadoc.io](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-clojure/badge.svg)](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-clojure)
* [Javadoc for Cyclops Clojure](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-clojure/)

# Features

cyclops-clojure provides Java friendly bindings to the Clojure collections API. We make use of cyclops powerful lazy collection extensions and coerce Clojure persistent collections to the pCollections and JDK Collections interfaces used in cyclops-react.


|  Clojure collection | cyclops-clojure   | pcollections interface   | JDK Interface  | Description  |
|---|---|---|---|---|
| PersistentList   | ClourePStack   | PStack  | List  | LinkedListX  : extended persistent linkedlist |
|  PersistentVector | ClojurePVector  | PVector   | List   | VectorX : extended persistent ArrayList   |
|  PersistentQueue | ClojurePQueue  | PQueue  | Queue  | PersistentQueueX : extended Persistent Queue  |
|  PersistentHashSet | ClojureHashPSet  | PSet  | Set  | PersistentSetX : extended Persistent Set  |
|  PersistentTreeSet | ClojureTreePOrderedSet  | POrderedSet  | SortedSet  | OrderedSetX : extended Persistent Ordered Set  |
|  PersistentArrayMap | ClojureArrayPMap  | PMap  | Map | PersistentMapX : extended Persistent Map  |
|  PersistentTreeMap | ClojureTreePMap  | PMap  | Map | PersistentMapX : extended Persistent Map  |
|  PersistentHashMap | ClojureHashPMap  | PMap  | Map | PersistentMapX : extended Persistent Map  |







