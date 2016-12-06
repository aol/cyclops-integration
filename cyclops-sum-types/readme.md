# Cyclops Sum Types

* [![Maven Central : cyclops-sum-types](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-sum-types/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-sum-types)

cyclops-sum-types defines Either implementations from Either, Either3, Either4, to Either5 (to be extended).

All Either implementations have the following features


1. Right biased for ease of use
1. Rich API
1. Totally lazy
1. Tail recursive map / flatMap methods
1. Applicative combine
1. coflatMap & nest
1. Fluent custom operators (via Either<X>#to)
1. Interopability via Reactive Streams Publisher - all Eithers implement Publisher
1. Accept Publisher in API calls (flatMapPublisher)
1. Interopability via Iterable - all Eithers implement Iterable
1. Accept Iterable in API calls (flatMapIterable)
1. For Comprehensions (forEach2-4)
1. sequence, traverse, accumulate operators


Either extends cyclops-react Xor, providing a lazy and tail call optimized alternative.

# Naming conventions

### Either 
Left,Right

### Either3
Left1,Left2,Right

### Either4
Left1,Left2,Left3,Right

### Either5
Left1,Left2,Left3,Left5,Right


# Use cases

## Pattern matching against sub-types

![OS Model](https://cloud.githubusercontent.com/assets/9964792/20805590/3b3384e6-b7ef-11e6-8542-10d934d15ddf.png)

```java

interface OS {
	Either3<iOS,Windows,Linux> match();
}
OS os;

os.match()
  .visit(this::handleiOS,this::handleWindows,this::handleLinux);
  
```
Of course iOs, Windows and Linux don't even have to inherit from a common super-type for this technique to work, we just need to define a method that returns an Either with an exhaustive set of cases we would like the user to handle.

### Execute a method against any type

```java

interface OS {
	Either3<iOS,Windows,Linux> match();
}
OS os;

os.match()
  .to(e->Either3.visitAny(e,System.out::println));
  
 //prints OS details
  
```



