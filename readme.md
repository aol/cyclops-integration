<img width="820" alt="screen shot 2016-02-22 at 8 44 42 pm" src="https://cloud.githubusercontent.com/assets/9964792/13232030/306b0d50-d9a5-11e5-9706-d44d7731790d.png">

# The cyclops-react integration modules.

There are a number of integration modules for cyclops-react, they are

* [cyclops-rx](https://github.com/aol/cyclops/tree/master/cyclops-rx)
* [cyclops-reactor](https://github.com/aol/cyclops/tree/master/cyclops-reactor)
* [cyclops-guava](https://github.com/aol/cyclops/tree/master/cyclops-guava)
* [cyclops-functionaljava](https://github.com/aol/cyclops/tree/master/cyclops-functionaljava)
* [cyclops-javaslang](https://github.com/aol/cyclops/tree/master/cyclops-javaslang)

This screencast gives an overview of how cyclops can help integrate and provide abstractions across the datatypes in the above libraries. [Unifying the cambrian explosion with cyclops-react ] (https://www.youtube.com/watch?v=YgzvpMbxiRo)

## Integration module features

1. reactive-streams support
2. for-comprehensions
3. type conversion 
4. AnyM, AnyMValue and AnyMSeq support

### Reactive Streams support and AnyM support

Closely linked to cyclops-react AnyM functionality, the integration modules allow appropriate types from FunctionalJava, Javaslang, Guava, RxJava and Reactor to be wrapped in an 'AnyM' wrapper. The AnyM wrappers all act as reactive-streams publishers. So if you would like a Javaslang Array or FunctionalJava Writer or List to behave as a reactive-streams publisher, simply call the appropriate method in our Javaslang or FJ class and subscribe to the returned AnyM type.

#### Examples 

##### Convert a Javaslang List into a reactive-stream

```java

import static com.aol.cyclops.javaslang.Javaslang.traversable;

SeqSubscriber<Integer> sub = SeqSubscriber.subscriber();
traversable(List.of(1,2,3)).subscribe(sub);
sub.stream()
    .forEachWithError(System.out::println, System.err::println);
```

##### Schedule emission from  FunctionalJava Stream

```java

import static com.aol.cyclops.functionaljava.FJ.stream;


stream(Stream.stream(1,2,3)).schedule("* * * * * ?", Executors.newScheduledThreadPool(1))
							.connect()
							.forEach(System.out::println)
									
```

Cyclops AnyM and AnyMValue / AnyMSeq interfaces allow any 'monad' type (a type with map / flatMap methods like JDK 8 Streams) to be wrapped and manipulated via a common interface. 

AnyMValue represents monads such as Optional, CompletableFuture, Eval (cyclops-react), Either, Try, Writer, Reader etc that resolve to a single value.

AnyMSeq represents monads such as List, Stream, Array, Set etc that resolve to a sequence of values. 

### for-comprehensions

All cyclops integration modules offer full strength for-comprehensions, allowing for example a Javaslang Stream generator to access each element in a preceeding Stream.

#### Examples 

##### Working with Pivotal Reactor Fluxes

```java

import static com.aol.cyclops.reactor.Reactor.ForFlux;

Flux<Tuple2<Integer,Integer>> stream = ForFlux.each2(Flux.range(1,10), i->Flux.range(i, 10), Tuple::tuple);


```

##### Programming 'monadically' with FunctionalJava Writer's
```java
import static  com.aol.cyclops.functionaljava.FJ.ForWriter;

Writer<String,String> writer = ForWriter.each2(Writer.unit("lower", "", Monoid.stringMonoid),
												a->Writer.unit(a+"hello",Monoid.stringMonoid),(a,b)->a.toUpperCase() + b);

writer.value(); //"LOWERlowerhello"												
```

### type conversions

cyclops integration modules support conversion between FunctionalJava, Guava, Javaslang and JDK types. (Observables, Flux, Mono from RxJava and Reactor can be converted via AnyM/ reactive-streams support or by iterables where supported).

## Getting cyclops-react

* [![Maven Central : cyclops-react](https://maven-badges.herokuapp.com/maven-central/com.aol.simplereact/cyclops-react/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.simple-react/cyclops-react)

## Gradle

where x.y.z represents the latest version

compile 'com.aol.simplereact:cyclops-react:x.y.z'

## Maven

```xml
<dependency>
    <groupId>com.aol.simplereact</groupId>
    <artifactId>cyclops-react</artifactId>
    <version>x.y.z</version>
</dependency>
```

