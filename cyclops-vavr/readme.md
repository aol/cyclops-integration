# Vavr Integration

v9.0.0 of cyclops-javaslang requires v0.9.0 of Vavr.

## Get cyclops-javaslang


* [![Maven Central : cyclops-vavr](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-vavr/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-vavr)   [![javadoc.io](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-vavr/badge.svg)](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-vavr)
* [Javadoc for Cyclops Vavr](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-vavr/)

# Features

* Fast, lazy Xtended Collection support for Vavr Persistent collections (see [Faster Purely Functional Data Structues](https://medium.com/@johnmcclean/the-rise-and-rise-of-java-functional-data-structures-63782436f93b) )
   * Async / reactive collections with optional non-blocking backpressure via Spouts 
* Conversion between Vavr types and other Functional libraries (including Vavr Future to reactive Maybe, Either, Eval types)
* Native for comprehensions for Vavr types with an intuitive and clean API
* AnyM monad wrapper for Monadic types (with full integration with cyclops-react Monad abstractions such as Kleisli)
  * Mix and match functional libraries at will taking full advantage of the features of each. E.g. Mix reactive Observables, pushing asynchronously to Vavr collections 
* Companion classes for Vavr types offering :
  * For comprehensions
  * Higher Kinded Typeclasses
  * Semigroups and Monoids
  * Helper functions for combining / accumulating and zipping values



# Xtended Collections

Create and convert a Vavr Vector via Xtended Collections

```java
 Vector<Integer> vector = VectorX.of(1, 2, 3)
                                .type(VavrTypes.vector())
                                .map(i -> i * 2)
                                .to(VavrConverters::Vector);

```

Asynchronously populate a Vavr Vector

```java
 VectorX<Integer> vectorX = Spouts.reactive(Stream.of(1, 2, 3),Executors.newFixedThreadPool(1))
                                  .to()
                                  .vectorX(LAZY)
                                  .type(VavrTypes.vector())
                                  .map(this::expensiveOp);

/** Continue processing **/

vectorX.get(1); //blocking operation until data loaded

//or unwrap back to Vavr

Vector<Integer> vavr = vector.to(VavrConverters::Vector);

```

# Monad Transformers
```java
 ListT<option,Integer> vectorInOption = ListT.ofList(Vavr.option(Option.some(VavrVectorX.of(10))));


        ListT<option,Integer> doubled = vectorInOption.map(i->i*2);
        ListT<option,Integer> repeated = doubled.cycle(3);

        System.out.println(repeated);
    
        //ListT[Some([20, 20, 20])]
        
        Option<Vector<Integer>> list = option(vectorInOption.unwrap()).map(s -> s.to()
                                                                      .vectorX(LAZY)
                                                                      .to(VavrConverters::Vector));
```

# For Comprehensions

```java
Try<String> result = Trys.forEach4(grind("arabica beans"),
                                    ground -> heatWater(new Water( 25)),
                                    (ground, water) -> brew(ground, water),
                                    (ground, water ,espresso) -> frothMilk("milk"),
                                    (ground , water ,espresso , foam) -> combine(espresso, foam));

        System.out.println(result.get());
        //cappuccino
        
        Try<String> grind(String beans) {  return Try.of(() -> "ground coffee of " + beans);}
        
        Try<Water> heatWater(Water water) { return Try.of(() -> water.withTemperature(85)); }
        
        Try<String> frothMilk(String milk) { return Try.of(() -> "frothed " + milk);}
        
        Try<String> brew(String coffee, Water heatedWater) { return Try.of(() -> "espresso");}
        
        String combine(String espresso, String frothedMilk) { return "cappuccino"; }
```

# Reactive Either / Maybe / Eval (from cyclops-react)

Asynchronously push data into a cyclops-react either from a Vavr Future
```java
import static cyclops.conversion.vavr.ToCyclopsReact.either;

Either<Throwable,String> result = either(Future.of(Executors.newFixedThreadPool(1),this::loadData));
```

# AnyM

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


## Schedule emission from  JavaSlang Stream

```java

import static cyclops.monads.Vavr.stream;


stream(Stream.ofAll(1,2,3)).schedule("* * * * * ?", Executors.newScheduledThreadPool(1))
							 .connect()
							 .forEach(System.out::println);
```



Use Javaslang.<type> to wrap Javaslang Monads

```java	
assertThat(Javaslang.tryM(Try.of(this::success))
			.map(String::toUpperCase)
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
```

# Kotlin style sequence generators

```java
import static com.aol.cyclops2.types.foldable.ConvertableSequence.Conversion.LAZY;
import static cyclops.stream.Generator.suspend;
import static cyclops.stream.Generator.times;

i = 100;
k = 9999;

Vector<Integer> vec = suspend((Integer i) -> i != 4, s -> {

                         Generator<Integer> gen1 = suspend(times(2), s2 -> s2.yield(i++));
                         Generator<Integer> gen2 = suspend(times(2), s2 -> s2.yield(k--));

                         return s.yieldAll(gen1.stream(), gen2.stream());
                  }
               ).to()
                .vectorX(LAZY)
                .take(5)
                .to(VavrConverters::Vector);

System.out.println(vec);
//Vector(100, 101, 9999, 9998, 102)
```

# Higher Kinded Typeclasses

If you really want / or need to program at a much higher level of abstraction cyclops-vavr provided psuedo Higher Kinded encordings and typeclasses for Vavr types

e.g. using the Pure and Functor typeclasses for Vavr Streams

```java

   Pure<stream> pure = Streams.Instances.unit();
   Functor<stream> functor = Streams.Instances.functor();
        
   StreamKind<Integer> list = pure.unit("hello")
                                  .apply(h->functor.map((String v) ->v.length(), h))
                                  .convert(StreamKind::narrowK);

        
   assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
```






