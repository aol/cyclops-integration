# Javaslang Integration

v8.3.0 of cyclops-javaslang requires v2.0.5 of Javaslang.

## Get cyclops-javaslang


* [![Maven Central : cyclops-javaslang](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-javaslang/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-javaslang)
* [Javadoc for Cyclops Javaslang](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-javaslang/)

# Features

1. Native for comprehensions for JavaSlang types
2. Monad wrapping via AnyM / AnyMValue / AnyMSeq
3. reactive-streams support for all Javaslang types (via AnyM support)
4. Compatible with cyclops-react pattern matching
5. Ability to use Javaslang types inside cyclops-react monad transformers (as the wrapping type, requires conversion to act as the nested type).
6. Memoize javaslang functions with a configurable Cache (support LRU, or TTL)
7. Stream extensions via AnyMSeq for all Javaslang traversable
10. PCollections / JDK / Cyclops bindings for JavaSlang collections - use cyclops Lazy Extended Collections with your fav JavaSlang collections
8. [Higher Kinded Type encodings](https://github.com/aol/cyclops/tree/master/cyclops-javaslang/src/main/java/com/aol/cyclops/javaslang/hkt) for Javaslang types
9. [Haskell like type classes](https://github.com/aol/cyclops/tree/master/cyclops-javaslang/src/main/java/com/aol/cyclops/javaslang/hkt/typeclasses/instances) for Javaslang types (treat Lazy as a Monad or MonadPlus!)



# Details & Examples

|  JavaSlang collection | cyclops-scala   | pcollections interface   | JDK Interface  | Description  |
|---|---|---|---|---|
| List   | JavaSlangPStack   | PStack  | List  | PStackX  : extended persistent linkedlist |
|  Vector | JavaSlangPVector  | PVector   | List   | PVectorX : extended persistent ArrayList   |
|  Queue | JavaSlangPQueue  | PQueue  | Queue  | PQueueX : extended Persistent Queue  |
|  HashSet | JavaSlangPSet  | PSet  | Set  | PSetX : extended Persistent Set  |
|  TreeSet | JavaSlangPOrderedSet  | POrderedSet  | SortedSet  | POrderedSetX : extended Persistent Ordered Set  |
|  TreeMap | JavaSlangTreePMap  | PMap  | Map | PMapX : extended Persistent Map  |
|  HashMap | JavaSlangHashPMap  | PMap  | Map | PMapX : extended Persistent Map  |


## For Comprehensions

cyclops-javaslang For comprehensions allow subsequent steps to refer to the elements of previous steps.

Javaslang specific for-comprehensions

```java
Value<Integer> option = Javaslang.ForValue.each2(Option.of(10), a-> Option.<Integer>of(a+20), (a,b)->a+b)

//Option[40]
```

```java

  	@Test
	public void tryTest(){
		
		Try<String> result = 	For.iterable(grind("arabica beans"))
							  	   .iterable(ground->heatWater(new Water(25)))
							  	   .iterable(ground ->water-> brew(ground,water))
							  	   .iterable(ground->wqter->espresso->frothMilk("milk"))
							  	   .yield(ground ->water -> espresso->foam-> combine(espresso,foam))
							  	   .unwrap();
		
		System.out.println(result.get());
	}
	
	
	
	Try<String> grind(String beans) {
		 return Try.of(()->"ground coffee of "+ beans);
	}

	Try<Water> heatWater(Water water){
		 return Try.of(()->water.withTemperature(85));
		  
	}

	Try<String> frothMilk(String milk) {
		 return Try.of(()->"frothed " + milk);
	}

	Try<String>	brew(String coffee, Water heatedWater){
		  return Try.of(()->"espresso");
	}
	String combine(String espresso ,String frothedMilk) {
		return "cappuccino";
	}
```
## Schedule emission from  JavaSlang Stream

```java

import static com.aol.cyclops.javaslang.Javaslang.traversable;


traversable(Stream.of(1,2,3)).schedule("* * * * * ?", Executors.newScheduledThreadPool(1))
							 .connect()
							 .forEach(System.out::println)
```

Subscribe to a Javaslang Traversable

```java	
import static com.aol.cyclops.javaslang.Javaslang.traversable;

SeqSubscriber<Integer> subscriber =SeqSubscriber.subscriber();
		
Stream<Integer> stream = Stream.of(1,2,3);
		
traversable(stream).subscribe(subscriber);
		
subscriber.stream()
	 	  .forEachWithError(System.out::println, System.err::println);
```
## AnyM

Use Javaslang.<type> to wrap Javaslang Monads

```java	
assertThat(Javaslang.tryM(Try.of(this::success))
			.map(String::toUpperCase)
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
```



Pacakage com.aol.cyclops.javaslang contains converters for types from various functional libraries for Java

* JDK
* Guava
* Functional Java
* jooλ
* simple-react

Supported Javaslang Monads include

* Try
* Either
* Option
* Stream
* Future
* Lazy
* List
* Array
* Stack
* Queue
* Vector
* HashSet




## Memoization with a Guava cache

Example configuration for Memoization with a Guava cache with TTL of 10 minutes after writing

```java
		Cache<Object, Integer> cache = CacheBuilder.newBuilder()
			       .maximumSize(1000)
			       .expireAfterWrite(10, TimeUnit.MINUTES)
			       .build();
	
		Cacheable<Integer> cacheable = (key,fn)->  { 
					try {
						return cache.get(key,()->fn.apply(key));
					} catch (ExecutionException e) {
						 throw ExceptionSoftener.throwSoftenedException(e);
					}
		};
		
		Function2<Integer,Integer,Integer> s = memoizeBiFunction( (a,b)->a + ++called,
										cacheable);
		assertThat(s.apply(0,1),equalTo(1));
		assertThat(s.apply(0,1),equalTo(1));
		assertThat(s.apply(0,1),equalTo(1));
		assertThat(s.apply(1,1),equalTo(3));
		assertThat(s.apply(1,1),equalTo(3));
```




