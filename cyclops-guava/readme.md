# Guava Integration

## Getting cyclops-guava

* [![Maven Central : cyclops-guava](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-guava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-guava)   [![javadoc.io](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-guava/badge.svg)](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-guava)


## Gradle

where x.y.z represents the latest version

compile 'com.aol.cyclops:cyclops-guava:x.y.z'

## Maven

```xml
<dependency>
    <groupId>com.aol.cyclops</groupId>
    <artifactId>cyclops-guava</artifactId>
    <version>x.y.z</version>
</dependency>
```

# cyclops-guava features 

1. Extensions Optional and FluentIterable
2. For comprehensions 
3. Type conversions across popular functional libraries
4. Kotlin style sequence generators
5. Advanced functional typeclasses

Currently requires Guava 22.0 or above

Use Guava.<type> to create wrapped Guava Monads.

## For Comprehensions

```java
import cyclops.monads.GuavaWitness.fluentIterable.forEach2;
String s = forEach2(FluentIterable.of(1, 2, 3),
                                                 a -> FluentIterable.of(a + 10), Tuple::tuple)
                                          .toString();

assertThat(s, equalTo("[(1, 11), (2, 12), (3, 13)]"));
```


# Higher Kinded Typeclasses

cyclops provides the following Typeclasses and implementations for Guava FluentIterables and Optionals Types

* Unit / pure
* Functor
* Applicative
* Monad
* MonadZero
* MonadPlus
* Comonad
* Foldable
* Unfoldable
* Eq
* Traverse

Type class instances are accessible through the Companion Object for each type.


## Using Typeclasses

### Directly 

Typeclasses can be used directly (although this results in verbose and somewhat cumbersome code)
e.g. using the Pure and Functor typeclasses for FluentIterables

```java

   Pure<fluentIterable> pure = FluentIterables.Instances.unit();
   Functor<fluentIterable> functor = FluentIterables.Instances.functor();
        
   FluentIterableKind<Integer> stream = pure.unit("hello")
                                  .applyHKT(h->functor.map((String v) ->v.length(), h))
                                  .convert(FluentIterableKind::narrowK);

        
   assertThat(list,equalTo(FluentIterabl.of("hello".length())));
```

### Via Active

The Active class represents a Higher Kinded encoding of a Guava (or cyclops-react/ JDK/ reactor / Vavr/ rx etc) type *and* it's associated type classes

The code above which creates a new FluentIterable containing a single element "hello" and transforms it to FluentIterable of Integers (the length of each word), can be written much more succintly with Active

```java

Active<fluentIterable,Integer> active = FluentIterables.allTypeClasses(Stream.empty());

Active<fluentIterable,Integer> hello = active.unit("hello")
                                     .map(String::length);

FluentIterable<Integer> stream = FluentIterableKind.narrow(hello.getActive());

```

### Via Nested

The Nested class represents a Nested data structure, for example a FluentIterable of Optionals *and* the associated typeclass instances for both types.

```java
import cyclops.companion.guava.Optionals.OptionalNested;

Nested<optional,fluentIterable,Integer> optList  = OptionalNested.list(Option.some(FluentIterable.of(1,10,2,3)))
                                                                       .map(i -> i * 20);

Optional<Integer> opt  = optList.foldsUnsafe()
                                .foldLeft(Monoids.intMax)
                                .convert(OptionKind::narrowK);


//[200]

```

### Via Coproduct

Coproduct is a Sum type for HKT encoded types that also stores the associated type classes

```java
import static 
Coproduct<FluentIterables,optional,Integer> nums = Optionals.coproduct(10,FluentIterables.Instances.definitions());


int value = nums.map(i->i*2)
                .foldUnsafe()
                .foldLeft(0,(a,b)->a+b);

//20

```



## Subscribe to a Guava FluentIterable



```java	
import static com.aol.cyclops.javaslang.guava.fluentIterable;

SeqSubscriber<Integer> subscriber =SeqSubscriber.subscriber();
		
FluentIterable<Integer> stream = FluentIterable.from(Arrays.asList(1,2,3));
		
fluentIterable(stream).subscribe(subscriber);
		
subscriber.stream()
	 	  .forEachWithError(System.out::println, System.err::println);
```

# AnyM

## AnyM : a type safe abstraction across Any Monadic type in Java.

Define ulta-generic code that can be used by types across Guava, Reactor, cyclops-react, Vavr, JDK, Functional Java, RxJava. 

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


AnyMSeq<fluentIterable,Integer> fi = FluentIterables.anyM(FluentIterable.of(0,1,2,3,4,5,6,7,8,9,10));
AnyMSeq<fluentIterable,Integer> summedFi = sumAdjacent(fi);
FluentIterable<Integer> backToVavr = GuavaWitness.fluentIterable(summedFi);


```

Or RxJava

```java
import static cyclops.monads.Rx2Witness.observable;
import cyclops.companion.rx.Observables;

AnyMSeq<observable,Integer> rxObservable = Observables.anyM(Observable.range(0, 10));
AnyMSeq<observable,Integer> summedRx = sumAdjacent(rxObservable);
Observable<Integer> backToRx = RxWitness.observable(summedRx);


```


## Schedule emission from  Gauva FluentIterable

```java

import static cyclops.companion.guava.FluentIterable.anyM;


anyM(FluentIterable.of(1,2,3)).schedule("* * * * * ?", Executors.newScheduledThreadPool(1))
							 .connect()
							 .forEach(System.out::println);
```




# Kotlin style sequence generators

```java

import static cyclops.stream.Generator.suspend;
import static cyclops.stream.Generator.times;

i = 100;
k = 9999;

FluentIterable<Integer> fi = FluentIterable.from(suspend((Integer i) -> i != 4, s -> {

                         Generator<Integer> gen1 = suspend(times(2), s2 -> s2.yield(i++));
                         Generator<Integer> gen2 = suspend(times(2), s2 -> s2.yield(k--));

                         return s.yieldAll(gen1.stream(), gen2.stream());
                  }
               ));


//(100, 101, 9999, 9998, 102)
```
