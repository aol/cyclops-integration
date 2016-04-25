# Guava Integration

## Getting cyclops-guava

* [![Maven Central : cyclops-guava](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-guava/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-guava)


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

# cyclops-guava features include

1. Native for comprehensions for Guava FluentIterable and Optional types
2. Monad wrapping via AnyM / AnyMValue / AnyMSeq
3. Compatible with cyclops-react pattern matching
4. Ability to use Observables inside cyclops-react monad transformers (as the wrapping type, requires conversion to act as the nested type).
# Features

Currently requires Guava 19.0 or above

Use Guava.<type> to create wrapped Guava Monads.


## Examples

Optional in a for comprehension

```java
Optional<Integer> optional = Guava.ForOptional.each2(Optional.of(10), a->Optional.absent(), (a,b)->"failed")
//Optional.absent
```

FluentIterable in a for comphrension

```java
Guava.ForFluentIterable.each2(FluentIterable.from(ListX.of(1,2,3)),
												a->FluentIterable.<Integer>from(ListX.of(a+10)), 
												Tuple::tuple).toString()

//[(1, 11), (2, 12), (3, 13)]
```

Subscribe to a Guava FluentIterable

```java	
import static com.aol.cyclops.javaslang.guava.fluentIterable;

SeqSubscriber<Integer> subscriber =SeqSubscriber.subscriber();
		
FluentIterable<Integer> stream = FluentIterable.from(Arrays.asList(1,2,3));
		
fluentIterable(stream).subscribe(subscriber);
		
subscriber.stream()
	 	  .forEachWithError(System.out::println, System.err::println);
```

Pacakage com.aol.cyclops.guava contains converters for types from various functional libraries for Java

* JDK
* Javaslang
* Functional Java
* jooλ
* simple-react

Supported Guava Monads include

* FluentIterable
* Optional

These are available in Cyclops Comprehensions, or via Cyclops AnyM.

