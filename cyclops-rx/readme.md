# RxJava Integration


## Get cyclops-rx


* [![Maven Central : cyclops-reactor](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-rx/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-rx)   [![javadoc.io](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-rx/badge.svg)](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-rx)
* [Javadoc for cyclops-rx](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-rx)


# cyclops-rx features include

* Native For Comprehensions
* Observable based ReactiveSeq implementation
  * Implement an extended Java 8 Stream using Rx Observable
  * Full integration with cyclops-react Xtended collections
  * Asynchronously populate an Xtended Collection with Rx Observables, materialize / block on first access
* AnyM monad wrapper for Monadic types (with full integration with cyclops-react Monad abstractions such as Kleisli)
    * Monad wrapper uses native Observable operators
    * Xtended Collections backed by Observable operate on Observable directly
* StreamT monad transformer operates directly with Observable    
* Extension Operators for Observable and ReactiveSeq (extend ReactiveSeq with Observable and Observable with ReactiveSeq)
* Companion classes for Obserbables offering :
  * For comprehensions
  * Higher Kinded Typeclasses
  * Helper functions for combining / accumulating and zipping values
  

# Reactive Collections!

In the example below we asynchronously populate an Xtended list using an Rx Java Observable. Additional, reactive operations can be performed on the List asynchronously.
The ListX only blocks on first access to the data.

```java
import static cyclops.collections.mutable.ListX.listX;
import static cyclops.companion.rx.Observables.reactiveSeq;
AtomicBoolean complete = new AtomicBoolean(false);


Observable<Integer> async =  Observables.fromStream(Spouts.async(Stream.of(100,200,300), Executors.newFixedThreadPool(1)))
                                                .doOnCompleted(()->complete.set(true));

ListX<Integer> asyncList = listX(reactiveSeq(async))
                                      .map(i->i+1);

System.out.println("Blocked? " + complete.get());

System.out.println("First value is "  + asyncList.get(0));

System.out.println("Completed? " + complete.get());
```
Which will print

```
Blocked? false
First value is 101
Completed? true
```

# For Comprehensions

```java
 Observable<Integer> result = Observables.forEach(Observable.just(10, 20),
                                                   a -> Observable.<Integer> just(a + 10),
                                                   (a, b) -> a + b);

 result.toList()
       .toBlocking()
        .single()
        //[30, 50]

```

# ReactiveSeq integration

## Extension operators

# AnyM monad abstraction

# StreamT monad transformer

# Higher Kinded Types and Type classes