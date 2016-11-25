# Project Reactor Integration

v8.2.0 of cyclops-reactor and above is built using v3.0.2.RELEASE of Project Reactor

## Get cyclops-reactor


* [![Maven Central : cyclops-reactor](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-reactor/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-reactor)
* [Javadoc for Cyclops-Reactor](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-reactor)


# cyclops-reactor features include

1. Lazy eXtended collections (backed by Flux)
2. FluxSource -> for pushing data into Flux and Mono types
3. Native for comprehensions for Reactor types
4. Native Monad Tranformer for Flux and Mono. FluxT also has native for comprehensions
5. Monad wrapping via AnyM / AnyMValue / AnyMSeq
6. Compatible with cyclops-react pattern matching
7. Ability to use Reactor types inside cyclops-react monad transformers (as the wrapping type, requires conversion to act as the nested type).
8. Fluxes, Monos and Publishers companion classes for working with Flux, Mono and general Publishers
9. [Higher Kinded Type encodings](https://github.com/aol/cyclops/tree/master/cyclops-reactor/src/main/java/com/aol/cyclops/reactor/hkt) for Reactor types
10. [Haskell like type classes](https://github.com/aol/cyclops/tree/master/cyclops-reactor/src/main/java/com/aol/cyclops/reactor/hkt/typeclasses/instances) for Reactor types

# Lazy extended Collections

Standard JDK collections

1. [LazyListX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/standard/LazyListX.html)
2. [LazyDequeX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/standard/LazyDequeX.html)
3. [LazyQueueX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/standard/LazyQueueX.html)
4. [LazySetX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/standard/LazySetX.html)
5. [LazySortedX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/standard/LazySortedX.html)

Persistent collections

1. [LazyPStackX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/persistent/LazyPStackX.html)          (A persistent LinkedList)
2. [LazyPVectorX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/persistent/LazyPVectorX.html)         (A persistent Vector - an ArrayList analogue)
3. [LazyPQueueX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/persistent/LazyPQueueX.html)          (A persistent Queue)
4. [LazyPSetX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/persistent/LazyPSetX.html)            (A persistent Set)
5. [LazyPOrderedSetX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/persistent/LazyPOrderedSetX.html)     (A persistent OrderedSet)
6. [LazyPBagX](http://static.javadoc.io/com.aol.cyclops/cyclops-reactor/8.2.1/com/aol/cyclops/reactor/collections/extensions/persistent/LazyPBagX.html)            (A persistent Bag)


### Notes : 

1. Lazy collections can not contain nulls (extended operations will result in NullPointerException), use ListX from cyclops-react for an extended List that can contain nulls
2. Data access / modifications operations are eager (transformations are lazy)
3. A Lazy Collection is not a Stream, eager operations result in the materialization of the entire list (there is no short circuiting, for example)

## LazyListX

LazyListX extends [ListX](http://static.javadoc.io/com.aol.simplereact/cyclops-react/1.0.1/com/aol/cyclops/data/collections/extensions/standard/ListX.html) from cyclops-react (and JDK java.util.List). 

```java

	ListX<Integer> lazy = LazyListX.fromIterable(myIterable);
	
	//lazily define operations
	ListX<ListX<Integer>> transformed = lazy.map(i->i*2)
											.filter(i->i<100)
		 									.grouped(2);

	//operations performed when data is accessed
	transformed.get(0).reduce(0,(a,b)->a+b);
	
```	

### Notes :  (repeated for LazyListX only - holds for all)

1. LazyListX can not contain nulls (extended operations will result in NullPointerException), use ListX from cyclops-react for an extended List that can contain nulls
2. Data access / modifications operations are eager (transformations are lazy)
3. A LazyList is not a Stream, eager operations result in the materialization of the entire list (there is no short circuiting, for example)

## LazyDequeX

LazyDequeX extends DequeX from cyclops-react (and JDK java.util.Deque). 

```java

	DequeX<Integer> lazy = LazyDequeX.fromIterable(myIterable);
	
	//lazily define operations
	DequeX<ListX<Integer>> transformed = lazy.map(i->i*2)
											.filter(i->i<100)
		 									.grouped(2);

	//operations performed when data is accessed
	transformed.get(0).reduce(0,(a,b)->a+b);
	
```	

## LazyQueueX

LazyQueueX extends QueueX from cyclops-react (and JDK java.util.Deque). 

```java

	QueueX<Integer> lazy = LazyQueueX.fromIterable(myIterable);
	
	//lazily define operations
	LazyQueueX<ListX<Integer>> transformed = lazy.map(i->i*2)
											 	 .filter(i->i<100)
		 									 	 .sliding(2,1);

	//operations performed when data is accessed
	transformed.get(0).reduce(0,(a,b)->a+b);
	
```	

# FluxSource

For pushing data into Flux and Mono types

```java
	PushableFlux<Integer> pushable = FluxSource.ofUnbounded();
	pushable.getQueue()
	        .offer(1);
	        
	//on a separate thread
	pushable.getFlux()
	        .map(i->i*2)
		    .subscribe(System.out::println);
		    
	//then push data into your Flux
	pushable.getQueue()
	        .offer(2);
	        
	//close the transfer Queue
	 pushable.getQueue()
	         .close();
```

Documentation for StreamSource (cyclops-react / extended JDK  analogue of FluxSource)

* [StreamSource wiki](https://github.com/aol/cyclops-react/wiki/StreamSource)

Blog post on [pushing data into Java 8 Streams](http://jroller.com/ie/entry/pushing_data_into_java_8)

Documentation for working with Queues

* [Queues explained](https://github.com/aol/cyclops-react/wiki/Queues-explained)
* [Agrona wait free Queues](https://github.com/aol/cyclops-react/wiki/Agrona-Wait-Free-Queues)
* [Working with wait free Queues](https://github.com/aol/cyclops-react/wiki/Wait-Strategies-for-working-with-Wait-Free-Queues)

# Joining Streams with ReactorPipes

ReactorPipes provides an API for flexible joining of multple different Stream types.


```java

	ReactorPipes<String,Integer> pipes = ReactorPipes.of();
	
	//store a transfer Queue with a max size of 1,000 entries
	pipes.register("transfer1",QueueFactories.boundedQueue(1_000));
	
	//connect a Flux to transfer1
	Maybe<Flux<Integer>> connected = pipes.flux("transfer1");
	Flux<Integer> stream = connected.get();
	
	//Setup a producing Stream
	ReactiveSeq seq = ReactiveSeq.generate(this::loadData)
			   					 .map(this::processData);
			   
    
    pipes.publishToAsync("transfer1",seq);
    
    stream.map(e->handleNextElement(e))
    	  .subscribe(this::save);
	
```

# Monad abstractions

Use Fluxes.anyM or Mono.anyM to create wrapped Reactor Monads.


Supported Reactor Monads include

* Flux
* Mono


## Example for comprehensions with Flux

```java
import static com.aol.cyclops.reactor.Fluxes.forEach;

Flux<Integer> result = forEach(Flux.just(10,20),a->Flux.<Integer>just(a+10)
                                             ,(a,b)->a+b);
	
//Flux[30,50]
 ```

## Example for comprehensions with Mono

```java
import static com.aol.cyclops.reactor.Monos.forEach;

Mono<Integer> result = forEach(Mono.just(10),a->Mono.<Integer>just(a+10)
                                          ,(a,b)->a+b);

//Mono[30]
 ```
 
## FluxT monad transformer
 
```java
import static com.aol.cyclops.reactor.FluxTs.fluxT;

FluxTSeq<Integer> nested = fluxT(Flux.just(Flux.just(1,2,3),Flux.just(10,20,30)));
FluxTSeq<Integer> mapped = nested.map(i->i*3);

//mapped = [Flux[Flux[3,6,9],Flux[30,60,90]]
```
## MonoT monad transformer

```java
import static com.aol.cyclops.reactor.MonoTs.monoT;

MonoTSeq<Integer> nestedFuture = monoT(Flux.just(Mono.just(1),Mono.just(10)));
mapped = nested.map(i->i*3);

//mapped =  [Flux[Mono[3],Mono[30]]
```
 		
