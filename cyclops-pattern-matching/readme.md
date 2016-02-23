**Cyclops has merged with simple-react**. Please update your bookmarks - https://github.com/aol/cyclops-react 

# Cyclops pattern matching

Powerful Pattern Matching for Java. Use lambda's, hamcrest or scala-like wildcards to match (recursively) on destructured objects!


## Getting cyclops-pattern-matching

* [![Maven Central : cyclops-pattern-matching](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-pattern-matching/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-pattern-matching)


## Gradle

where x.y.z represents the latest version

compile 'com.aol.cyclops:cyclops-pattern-matching:x.y.z'

## Maven

```xml
<dependency>
    <groupId>com.aol.cyclops</groupId>
    <artifactId>cyclops-pattern-matching</artifactId>
    <version>x.y.z</version>
</dependency>
```
# Overview

The primary means of composing Pattern Matching expressions is via the Matchable interface. All other APIs will be deprecated in cyclops 7.4.0 pending eventual (long term) removal).


# Docs
              
* [Javadoc for Cyclops Pattern Matching](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-pattern-matching/5.0.0)
* [Pattern Matching Wiki](https://github.com/aol/cyclops/wiki/Pattern-matching-:-Pattern-Matching-for-Java-8)


## Examples

Given a simple case class

```java
static class MyCase  implements Matchable{ int a; int b; int c;}
```

### Match by value (Objects.equals)

Because MyCase implement Matchable it comes with Pattern Matching methods as standard, we can use these to match by value. 

```java
new MyCase(1,2,3).matches(c->c.hasValues(1,2,3).then(i->"hello"),
                            c->c.hasValues(4,5,6).then(i->"goodbye")
                           );
```
The above examples returns "hello"

### Match with Predicates

If users wish to supply their own equality tests, the api allows users to supply only predicates.

```java
Matchable.of(Arrays.asList(1,2,3))
		 .matches(c->c.hasValuesWhere(t->equals(t,1),Predicates.__,t->equals(t,1)).then(i->"2"));
```

### Match with Hamcrest

The immensely popular Hamcrest library can be used to create composable Matching expressions

```java
Matchable.of(Arrays.asList(1,2,Arrays.asList(3,2,5)))
                .matches(c->c.hasValuesMatching(not(equalTo(1)),any(Integer.class),hasItems(4,5)))
                        .then(i->"2"));
```

Note the hasValues API accepts values, predicates or hamcrest Matchers.

### Match with wildcards

```java
import static com.aol.cyclops.matcher.Predicates.__;
new MyCase(1,2,3).matches(c->c.hasValues(1,__,3).then(i->"hello"),
                            c->c.hasValues(4,__,6).then(i->"goodbye")
                           );    
```

### Recursively destructure a nested hierarchy of classes

Given a nested Object hierarchy we can also destructure and match on contained objects
```java
@Value
static class NestedCase  implements Matchable<MyCase<R>>{
	int a;
	int b;
	List<Integer> list;
}
```

We can recursively destructure a NestedCase object and match on the contents

```java
Matchable.of(new NestedCase(1,2,Arrays.asList(3,4,5)))
	 .matches(c->c.hasValues(1,__,hasValues(3,4,__))
				 	.then(i->"2"));
```

# Creating Case classes
In Java it is possible to create sealed type hierarchies by reducing the visibilty of constructors. E.g. If the type hierarchies are defined in one file super class constructors can be made private and sub classes made final. This will prevent users from creating new classes externally. 
Lombok provides a number of annotations that make creating case classes simpler.

@Value :  see https://projectlombok.org/features/Value.html

## A sealed type hierarchy

An example sealed hierarchy (ValueObject implies both Matchable and Decomposable)
```java
	@AllArgsConstructor(access=AccessLevel.PRIVATE) 
	public static class CaseClass implements ValueObject { } 
	@Value public static class MyCase1 extends CaseClass { int var1; String var2; }
	@Value public static class MyCase2 extends CaseClass { int var1; String var2; }

    CaseClass result;
    return result.match(this::handleBusinessCases);
```														

# Overview

## With the Matchable interface 

```java
Matchable.of(Optional.of(4))
         .mayMatch(
                o-> o.isEmpty().then(i->"empty"),
                o-> o.hasValues(1).then(i->"one"),
                o-> o.hasValues(2).then(i->"two"),
                o-> o.hasValuesWhere(i->i>2).then(i->"many"),
            ).orElse("error")
```

<img width="880" alt="screen shot 2015-07-22 at 10 14 06 pm" src="https://cloud.githubusercontent.com/assets/9964792/8837606/0a2d9368-30bf-11e5-9690-eaa96bb56cc5.png">



![pattern matching](https://cloud.githubusercontent.com/assets/9964792/8334707/3827c1e2-1a91-11e5-87b1-604905a75ecb.png)
  
# Docs
              

* [Javadoc for Cyclops Pattern Matching](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-pattern-matching/5.0.0)
* [Pattern Matching Wiki](https://github.com/aol/cyclops/wiki/Pattern-matching-:-Pattern-Matching-for-Java-8)
* [Pattern Matching in Cyclops](https://medium.com/@johnmcclean/pattern-matching-in-cyclops-for-java-8-21a4912bfe4d)

# Related Modules

[Core Pattern Matching Support](https://github.com/aol/cyclops/blob/master/cyclops-pattern-matching)
[Pattern Matching for Collections](https://github.com/aol/cyclops/tree/master/cyclops-pattern-matching-collections)

## The Matchable interface / trait


Objects that implement Matchable get a number of Pattern Matching helper methods by default.

match : matches must be present - by value, predicate or hamcrest matcher
mayMatch : matches maybe present - by value, predicate or hamcrest matcher



### Interfaces that extend Matchable

* ValueObject
* StreamableValue
* CachedValues, PTuple1-8

## Coercing any Object to a Matchable

```java
    As.asMatchable(myObject).match(this::makeFinancialDecision)
```

com.aol.cyclops.dynamic.As provides a range of methods to dynamically convert types/

# The Decomposable Interface  / Trait

The Decomposable Interface defines an unapply method that is used to convert the implementing Object into an iterable. This can be used to control how Cyclops performs recursive decomposition.

```java
    public <I extends Iterable<?>> I unapply();
```
    
### Interfaces that extend Decomposable

* ValueObject
* StreamableValue
* CachedValues, PTuple1-8

## Coercing any Object to a Decomposable

```java
    As.asDecomposable(myObject).unapply().forEach(System.out::println);
```

com.aol.cyclops.dynamic.As provides a range of methods to dynamically convert types

# Creating Case classes

In Java it is possible to create sealed type hierarchies by reducing the visibilty of constructors. E.g. If the type hierarchies are defined in one file super class constructors can be made private and sub classes made final. This will prevent users from creating new classes externally. 
Lombok provides a number of annotations that make creating case classes simpler.

@Value :  see https://projectlombok.org/features/Value.html

## A sealed type hierarchy

An example sealed hierarchy (ValueObject implies both Matchable and Decomposable)

```java
    @AllArgsConstructor(access=AccessLevel.PRIVATE) 
    public static class CaseClass implements ValueObject { } 
    @Value public static class MyCase1 extends CaseClass { int var1; String var2; }
    @Value public static class MyCase2 extends CaseClass { int var1; String var2; }

    CaseClass result;
    return result.match(this::handleBusinessCases);
```    
    


