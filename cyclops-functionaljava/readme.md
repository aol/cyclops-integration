# Functional Java Integration

v8.0.0 of cyclops-functionaljava and above is built using v4.5 of FunctionalJava.

## Get cyclops-functionaljava


* [![Maven Central : cyclops-functionaljava](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-functionaljava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-functionaljava)
* [Javadoc for cyclops-functionaljava](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-functionaljava)

# cyclops-functionaljava features include

1. Native for comprehensions for FunctionalJava types
2. Monad wrapping via AnyM / AnyMValue / AnyMSeq
3. reactive-streams support for all FunctionalJava types (via AnyM support)
4. Compatible with cyclops-react pattern matching
5. Ability to use FunctionalJava types inside cyclops-react monad transformers (as the wrapping type, requires conversion to act as the nested type).



Use FJ.<type> to create wrapped FunctionalJava Monads.

Package com.aol.cyclops.functionaljava contains converters for types from various functional libraries for Java

* JDK
* Guava
* Javaslang
* jooλ
* cyclops-react

Supported Functional Java Monads include

* IO
* Either
* Option
* Stream
* List
* State
* Reader
* Writer
* Trampoline
* Validation




## Schedule emission from  FunctionalJava Stream

```java

import static com.aol.cyclops.functionaljava.FJ.stream;


stream(Stream.stream(1,2,3)).schedule("* * * * * ?", Executors.newScheduledThreadPool(1))
							.connect()
							.forEach(System.out::println)
									
```

## Example map and convert to a Java list

```java
	FJ.list(List.list("hello world"))
				.map(String::toUpperCase)
			    .toList()
 ```
 
 ## Use a FuctionalJava type inside a ListTransformer
 
 ```java
 
 ListTSeq<Integer> listT = ListT.of(FJ.list(List.list(1,2,3).map(a->Arrays.asList(a))));
 
 listT.map(a->a*2);
```	
	
## For comprehensions with Option

 ```java
FJ.ForOption.each2(Option.some(10), a->Option.none(), (a,b)->"failed")

//Option.none
 ```
 
## Use a Functional Java List as a reactive-streams publisher
 
 ```java

import static com.aol.cyclops.functionaljava.FJ.list;

SeqSubscriber<Integer> sub = SeqSubscriber.subscriber();
list(List.list(1,2,3)).subscribe(sub);
sub.stream()
    .forEachWithError(System.out::println, System.err::println);
```
			

