# Clojure Integration

## Get cyclops-clojure


* [![Maven Central : cyclops-scala](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-clojure/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-clojure)   [![javadoc.io](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-clojure/badge.svg)](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-clojure)
* [Javadoc for Cyclops Clojure](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-clojure/)

# Features

cyclops-clojure provides Java friendly bindings to the Clojure collections API. We make use of cyclops powerful lazy collection extensions and coerce Clojure persistent collections to the pCollections and JDK Collections interfaces used in cyclops-react.


|  Clojure collection | cyclops-clojure factories  |  JDK Interface  | Description  |
|---|---|---|---|
| PersistentList   | CloureListX   |  List  | LinkedListX  : extended persistent linkedlist |
|  PersistentVector | ClojurePVector  |  List   | VectorX : extended persistent ArrayList   |
|  PersistentQueue | ClojureQueueX  |  Queue  | PersistentQueueX : extended Persistent Queue  |
|  PersistentHashSet | ClojureHashSetX  |  Set  | PersistentSetX : extended Persistent Set  |
|  PersistentTreeSet | ClojureTreeSetX  |  SortedSet  | OrderedSetX : extended Persistent Ordered Set  |
|  PersistentArrayMap | ClojureArrayMapX  | Map | PersistentMapX : extended Persistent Map  |
|  PersistentTreeMap | ClojureTreeMapX  | Map | PersistentMapX : extended Persistent Map  |
|  PersistentHashMap | ClojureHashMapX  | Map | PersistentMapX : extended Persistent Map  |







