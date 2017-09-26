<img width="820" alt="screen shot 2016-02-22 at 8 44 42 pm" src="https://cloud.githubusercontent.com/assets/9964792/13232030/306b0d50-d9a5-11e5-9706-d44d7731790d.png">

# Documentation

* [cyclops user guide](https://github.com/aol/cyclops-react/wiki)

## Latest Articles

* [DSLs with the Free Monad in Java 8 : Part I](https://medium.com/@johnmcclean/dsls-with-the-free-monad-in-java-8-part-i-701408e874f8)
* [Cross Library Stream Benchmarking : Playing Scrabble with Shakespeare](https://medium.com/@johnmcclean/cross-library-stream-benchmarking-playing-scrabble-with-shakespeare-8dd1d1654717)

# A common API across the functional landscape

cyclops provides a common set of APIs across the major functional libraries for Java. It does this via :

1. A common abstraction layer (AnyM short for AnyMonad)
2. Companion classes (Optionals, Streams, CompletableFutures, Options, Eithers, Lists, Trys etc) that provide common functionality such as For Comprehensions and Higher Kinded Haskell like type classes
3. Provided conversion classes between types via FromXXX and ToXXX classes
4. Allows collection types from all major functional libraries to be substituted behind cyclops-react lazy & reactive collection APIs

## AnyM : a type safe abstraction across Any Monadic type in Java.

Define ulta-generic code that can be used by types across Vavr, Reactor, cyclops-react, Guava, JDK, Functional Java, RxJava. 

```java
public <W extends WitnessType<W>> AnyMSeq<W,Integer> sumAdjacent(AnyMSeq<W,Integer> sequence){
     return sequence.sliding(1)
                    .map(t->t.sum(i->i).get())
}
```

Use them with Vavr 

```java
import static cyclops.monads.VavrWitness.list;
import cyclops.companion.vavr.Lists;


AnyMSeq<list,Integer> vavrList = Lists.anyM(List.range(0, 10));
AnyMSeq<list,Integer> summedVavr = sumAdjacent(vavrList);
List<Integer> backToVavr = VavrWitness.list(summedVavr);


```

Or RxJava

```java
import static cyclops.monads.Rx2Witness.observable;
import cyclops.companion.rx.Observables;

AnyMSeq<observable,Integer> rxObservable = Observables.anyM(Observable.range(0, 10));
AnyMSeq<observable,Integer> summedRx = sumAdjacent(rxObservable);
Observable<Integer> backToRx = RxWitness.observable(summedRx);


```

## Common functionality in Companion classes

For comprehensions

```java
import static cyclops.companion.vavr.Trys.*;

        
Try<String> result = Trys.forEach4(grind("arabica beans"),
                                   ground -> heatWater(new Water(25)),
                                   (ground, water) -> brew(ground, water),
                                   (ground, water ,espresso) -> frothMilk("milk"),
                                   (ground , water ,espresso , foam) -> combine(espresso, foam));

System.out.println(result.get());


Try<String> grind(String beans) {
    return Try.of(() -> "ground coffee of " + beans);
}

Try<Water> heatWater(Water water) {
    return Try.of(() -> water.withTemperature(85));
}

Try<String> frothMilk(String milk) {
    return Try.of(() -> "frothed " + milk);
}

Try<String> brew(String coffee, Water heatedWater) {
    return Try.of(() -> "espresso");
}

String combine(String espresso, String frothedMilk) {
        return "cappuccino";
}
```

```java
import static cyclops.companion.reactor.Fluxs.*;

Flux<Integer> result = Fluxs.forEach(Flux.just(10, 20), a -> Flux.<Integer> just(a + 10), (a, b) -> a + b);
result.collectList()
       .block(),
//List[30, 50];
```
## Lazy and Reactive Collection APIs

cyclops & cyclops-react allow collection types from all major functional libraries to be used behind fast lazy & reactive collection apis.

### What do we mean by fast lazy & reactive APIs

Most Collection APIs provided by the major functional libraries are eager. That means when a map transformation is invoked on a Functional Java or Vavr List it is executed immediately. Performing multiple chained operations often results in the the collection being processed (traversed) multiple different times. With cyclops map and other functional operations become lazy, and the your chain of commands are only executed when data within the collection is exected for the first time. This allows cyclops to process the entire chain of operations in a single traversal of the data. The resultant transformed collection is then stored in the underlying structure of your favourite functional collection API.

#### The Performance benefit

Leveraging cyclops can significantly improve the execution times of operations on collections.

The code for this performance testing speeding up the execution of functional operations on Javaslang Vectors is available [here](https://gist.githubusercontent.com/johnmcclean-aol/f6d9216ae179fa619d4a6ae67eea3802/raw/740fc413c13784529f30b375b219b0a6c9004865/cyclops-javaslang-benchmark.java)
![cyclops-speed-up](https://cdn-images-1.medium.com/max/1600/1*ocbwTrrsjPmEp22SliKvFQ.png)

##### Reactive APIs

cyclops also allows data to be pushed asynchronously into collection types from the major functional libraries. For example to asynchronously populate a JavaSlang / Vavr Vector we can write

```java
VectorX<Integer> asyncPopulated = JavaSlangPVector.fromStream(Spouts.publishOn(ReactiveSeq.of(1,2,3),
                Executors.newFixedThreadPool(1));
```

## Type safe, higher kinded typeclassess

E.g. Using a functor and applicative type classes on FunctionalJava Lists (Higher Kinded encoding via ListKind type)

```java

import static com.aol.cyclops.functionaljava.ListKind;
import static cyclops.companion.functionaljava.Lists.Instances.functor;
import static cyclops.companion.functionaljava.Lists.Instances.zippingApplicative;

ListKind<Fn1<Integer,Integer>> listFn = ListKind.widen(List.list((Lambda.λ((Integer i) ->i*2))
                                                .convert(ListKind::narrowK);
        
List<Integer> list =  zippingApplicative().ap(listFn,functor().map((String v)->v.length(),
                                                     widen(List.list("hello"))))
                                          .convert(ListKind::narrow);
        
//List.list("hello".length()*2))
```

There are a number of integration modules for cyclops-react, they are

* [cyclops-reactor](https://github.com/aol/cyclops/tree/master/cyclops-reactor)
* [cyclops-rxjava2](https://github.com/aol/cyclops/tree/master/cyclops-rxjava2)
* [cyclops-rx](https://github.com/aol/cyclops/tree/master/cyclops-rx)
* [cyclops-guava](https://github.com/aol/cyclops/tree/master/cyclops-guava)
* [cyclops-functionaljava](https://github.com/aol/cyclops/tree/master/cyclops-functionaljava)
* [cyclops-vavr](https://github.com/aol/cyclops/tree/master/cyclops-vavr)

This screencast gives an overview of how cyclops can help integrate and provide abstractions across the datatypes in the above libraries. [Unifying the cambrian explosion with cyclops-react ] (https://www.youtube.com/watch?v=YgzvpMbxiRo)


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

