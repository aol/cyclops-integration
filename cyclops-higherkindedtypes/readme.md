# Cyclops Higher Kinded Types

cyclops-higher-kinded-types integrates with [derive4j HKT](https://github.com/derive4j/hkt) to provide, [HighJ](https://github.com/highj/highj) compatible, type safe higher kinded type classes for core JDK types including

* Stream
* Optional
* CompletableFuture
* List
* Deque
* Set
* SortedSet
* Queue

cyclops-higher-kinded-types also provided more Java friendly semantic alias' for HKT type definitions and fluent apis for chainining calls using HKTs.

This is / will be a core dependency of cyclops-react. HKT type classes will be provided for 3rd party types in the integration modules.

## What are Higher Kinded Types?

Higher kinded types are a way of making a generic type more generic. For example we can define a method that takes a List of one generic type T and returns a List of another generic type R.

```java
public <T,R> List<R> transformList(Function<? super T, ? extends R> mapper, List<T> list);
```

Can we generalize this further, for example to transform any generic type? 

The code below is not valid Java code

```java
public <C,T,R> C<R> transformGeneric(Function<? super T, ? extends R> mapper, C<T> genericType);
```
In the absence of a common generic interface across all the type we'd like to abstract over, we need to separate the generic type being mapped (e.g. List) from the generic type of it's data (e.g. T). That would give us 

```java
Higher<List<?>,T>
```

``List<?>`` is not an ideal type parameter both because it is generic and also because there is nothing to stop any class falsely claiming to be a ``Higher<List<?>`` when it is not.

The functional library HighJ pioneered a technique of using non-generic 'witness' types to define the high kinded types core type. HighJ now builds on top of derive4j HKT which uses Java's annotation processor facility to enforce the correctness of witness types at compile time.

In cyclops-higher-kinded-type we define a ListType that encodes a List's type parameters in way that allows us to abstract over them in a much more generic / general way.

ListType's definition starts like this.

```java
public interface ListType<T> extends Higher<ListType.µ, T>, List<T> {
    /**
     * Witness type
     * 
     *
     */
    public static class µ {
    }
```


ListType.µ is the witness type that indicates we have a List. Only ListType can declare it's self as extends ``Higher<ListType.µ, T>`` and as ListType also implements List it follows that ``Higher<ListType.µ, T>`` can only ever be a List. You can implement ListType in you own libraries (as we do in cyclops-react) or use it to encode existing List implementations as higher-kinded Lists.

SetType does something very similar for sets.

Now we can write

```java

public interface Functor<CRE> {
    
     public <T,R> Higher<CRE,R> transformGeneric(Function<? super T, ? extends R> mapper, Higher<CRE,T> genericType);

}
```

Where CRE represents the 'core' type's witness (such as ListType.µ or SetType.µ). This allows us to write much more generic code, such as in the above example where we can abstract over the transformation of any unrestricted generic type to another.

### But how do we go from Higher back to List?

The Higher interface (an alias for derive4j HKT __ type) also provides some nice fluent apis, including a convert method.

```java

Functor<ListType.µ> listFunctor;

ListType<Integer> listOfInts = ListType.widen(Arrays.asList(1,2,3)); //widen the generic type to the HKT encoding for Lists    

Higher<ListType.µ,String> higherStrings  = listFunctor.transformGeneric(i->"hello:"+i, listOfInts);

ListType<String> listOfStrings = higherStrings.convert(ListType::narrow);

//["hello:1","hello:2","hello:3"]

listOfStrings.add("world");

//["hello:1","hello:2","hello:3","world"]

List<String> justAList = listOfStrings;

}
```

cyclops-typeclasses will contain a small number of type classes for manipulating higher kinded types in a very generic way, with a goal of being accessible to the broad Java development community.
