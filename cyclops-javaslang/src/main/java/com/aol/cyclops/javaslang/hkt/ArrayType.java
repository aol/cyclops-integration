package com.aol.cyclops.javaslang.hkt;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.aol.cyclops.hkt.alias.Higher;

import javaslang.Function1;
import javaslang.Tuple1;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.collection.Array;
import javaslang.collection.CharSeq;
import javaslang.collection.IndexedSeq;
import javaslang.collection.Iterator;
import javaslang.collection.Queue;
import javaslang.collection.Seq;
import javaslang.collection.Stack;
import javaslang.collection.Tree;
import javaslang.collection.Vector;
import javaslang.control.Either;
import javaslang.control.Option;
import javaslang.control.Try;
import javaslang.control.Try.CheckedSupplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Array's
 * 
 * ArrayType is a Array and a Higher Kinded Type (ArrayType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Array
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class ArrayType<T> implements Higher<ArrayType.µ, T> ,IndexedSeq<T>{
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    public static <T> ArrayType<T> of(T element) {
        return  widen(Array.of(element));
    }

   
    @SafeVarargs
    public static <T> ArrayType<T> of(T... elements) {
        return widen(Array.of(elements));
    }
    /**
     * Convert a Array to a simulated HigherKindedType that captures Array nature
     * and Array element data type separately. Recover via @see ArrayType#narrow
     * 
     * If the supplied Array implements ArrayType it is returned already, otherwise it
     * is wrapped into a Array implementation that does implement ArrayType
     * 
     * @param list Array to widen to a ArrayType
     * @return ArrayType encoding HKT info about Arrays
     */
    public static <T> ArrayType<T> widen(final Array<T> list) {
        
        return new ArrayType<>(list);
    }
    /**
     * Widen a ArrayType nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a Array to widen
     * @return HKT encoded type with a widened Array
     */
    public static <C2,T> Higher<C2, Higher<ArrayType.µ,T>> widen2(Higher<C2, ArrayType<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<ArrayType.µ,T> must be a ArrayType
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for Array types into the ArrayType type definition class
     * 
     * @param list HKT encoded list into a ArrayType
     * @return ArrayType
     */
    public static <T> ArrayType<T> narrowK(final Higher<ArrayType.µ, T> list) {
       return (ArrayType<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a Array into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return ArrayX from Higher Kinded Type
     */
    public static <T> Array<T> narrow(final Higher<ArrayType.µ, T> list) {
        return ((ArrayType)list).narrow();
       
    }

    private final Array<T> boxed;

    /**
     * @return This back as a ArrayX
     */
    public Array<T> narrow() {
        return (Array) (boxed);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return boxed.hashCode();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return boxed.equals(obj);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ArrayType [" + boxed + "]";
    }
    /**
     * @param zero
     * @param combine
     * @return
     * @see javaslang.collection.Foldable#fold(java.lang.Object, java.util.function.BiFunction)
     */
    public  T fold(T zero, BiFunction<? super T, ? super T, ? extends T> combine) {
        return boxed.fold(zero, combine);
    }
    /**
     * @return
     * @see javaslang.λ#isMemoized()
     */
    public  boolean isMemoized() {
        return boxed.isMemoized();
    }
    /**
     * @param op
     * @return
     * @see javaslang.collection.Foldable#reduce(java.util.function.BiFunction)
     */
    public  T reduce(BiFunction<? super T, ? super T, ? extends T> op) {
        return boxed.reduce(op);
    }
    /**
     * @param that
     * @return
     * @see javaslang.collection.IndexedSeq#endsWith(javaslang.collection.Seq)
     */
    public  boolean endsWith(Seq<? extends T> that) {
        return boxed.endsWith(that);
    }
    /**
     * @param op
     * @return
     * @see javaslang.collection.Foldable#reduceOption(java.util.function.BiFunction)
     */
    public  Option<T> reduceOption(BiFunction<? super T, ? super T, ? extends T> op) {
        return boxed.reduceOption(op);
    }
    /**
     * @param index
     * @return
     * @see javaslang.collection.Seq#apply(java.lang.Integer)
     */
    public  T apply(Integer index) {
        return boxed.apply(index);
    }
    /**
     * @param predicate
     * @param from
     * @return
     * @see javaslang.collection.IndexedSeq#indexWhere(java.util.function.Predicate, int)
     */
    public  int indexWhere(Predicate<? super T> predicate, int from) {
        return boxed.indexWhere(predicate, from);
    }
    /**
     * @return
     * @see javaslang.Function1#arity()
     */
    public  int arity() {
        return boxed.arity();
    }
    /**
     * @return
     * @see javaslang.Function1#curried()
     */
    public  Function1<Integer, T> curried() {
        return boxed.curried();
    }
    /**
     * @param element
     * @return
     * @see javaslang.Value#contains(java.lang.Object)
     */
    public  boolean contains(T element) {
        return boxed.contains(element);
    }
    /**
     * @return
     * @see javaslang.Function1#tupled()
     */
    public  Function1<Tuple1<Integer>, T> tupled() {
        return boxed.tupled();
    }
    /**
     * @return
     * @see javaslang.Function1#reversed()
     */
    public  Function1<Integer, T> reversed() {
        return boxed.reversed();
    }
    /**
     * @return
     * @see javaslang.collection.IndexedSeq#last()
     */
    public  T last() {
        return boxed.last();
    }
    /**
     * @return
     * @see javaslang.Function1#memoized()
     */
    public  Function1<Integer, T> memoized() {
        return boxed.memoized();
    }
    /**
     * @param that
     * @param predicate
     * @return
     * @see javaslang.Value#corresponds(java.lang.Iterable, java.util.function.BiPredicate)
     */
    public  <U> boolean corresponds(Iterable<U> that, BiPredicate<? super T, ? super U> predicate) {
        return boxed.corresponds(that, predicate);
    }
    /**
     * @param that
     * @param end
     * @return
     * @see javaslang.collection.IndexedSeq#lastIndexOfSlice(java.lang.Iterable, int)
     */
    public  int lastIndexOfSlice(Iterable<? extends T> that, int end) {
        return boxed.lastIndexOfSlice(that, end);
    }
    /**
     * @param predicate
     * @param end
     * @return
     * @see javaslang.collection.IndexedSeq#lastIndexWhere(java.util.function.Predicate, int)
     */
    public  int lastIndexWhere(Predicate<? super T> predicate, int end) {
        return boxed.lastIndexWhere(predicate, end);
    }
    /**
     * @param that
     * @return
     * @see javaslang.collection.Seq#containsSlice(java.lang.Iterable)
     */
    public  boolean containsSlice(Iterable<? extends T> that) {
        return boxed.containsSlice(that);
    }
    /**
     * @param after
     * @return
     * @see javaslang.Function1#andThen(java.util.function.Function)
     */
    public  <V> Function1<Integer, V> andThen(Function<? super T, ? extends V> after) {
        return boxed.andThen(after);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#average()
     */
    public  Option<Double> average() {
        return boxed.average();
    }
    /**
     * @param o
     * @return
     * @see javaslang.Value#eq(java.lang.Object)
     */
    public  boolean eq(Object o) {
        return boxed.eq(o);
    }
    /**
     * @return
     * @see javaslang.collection.Seq#crossProduct()
     */
    public  Iterator<Tuple2<T, T>> crossProduct() {
        return boxed.crossProduct();
    }
    /**
     * @param before
     * @return
     * @see javaslang.Function1#compose(java.util.function.Function)
     */
    public  <V> Function1<V, T> compose(Function<? super V, ? extends Integer> before) {
        return boxed.compose(before);
    }
    /**
     * @return
     * @see javaslang.collection.IndexedSeq#reverseIterator()
     */
    public  Iterator<T> reverseIterator() {
        return boxed.reverseIterator();
    }
    /**
     * @param that
     * @return
     * @see javaslang.collection.Seq#crossProduct(java.lang.Iterable)
     */
    public  <U> Iterator<Tuple2<T, U>> crossProduct(Iterable<? extends U> that) {
        return boxed.crossProduct(that);
    }
    /**
     * @param predicate
     * @param from
     * @return
     * @see javaslang.collection.IndexedSeq#segmentLength(java.util.function.Predicate, int)
     */
    public  int segmentLength(Predicate<? super T> predicate, int from) {
        return boxed.segmentLength(predicate, from);
    }
    /**
     * @param elements
     * @return
     * @see javaslang.collection.Traversable#containsAll(java.lang.Iterable)
     */
    public  boolean containsAll(Iterable<? extends T> elements) {
        return boxed.containsAll(elements);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.Value#exists(java.util.function.Predicate)
     */
    public  boolean exists(Predicate<? super T> predicate) {
        return boxed.exists(predicate);
    }
    /**
     * @param that
     * @param offset
     * @return
     * @see javaslang.collection.IndexedSeq#startsWith(java.lang.Iterable, int)
     */
    public  boolean startsWith(Iterable<? extends T> that, int offset) {
        return boxed.startsWith(that, offset);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Traversable#count(java.util.function.Predicate)
     */
    public  int count(Predicate<? super T> predicate) {
        return boxed.count(predicate);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.Value#forAll(java.util.function.Predicate)
     */
    public  boolean forAll(Predicate<? super T> predicate) {
        return boxed.forAll(predicate);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Seq#indexOf(java.lang.Object)
     */
    public  int indexOf(T element) {
        return boxed.indexOf(element);
    }
    /**
     * @param action
     * @see javaslang.Value#forEach(java.util.function.Consumer)
     */
    public  void forEach(Consumer<? super T> action) {
        boxed.forEach(action);
    }
    /**
     * @return
     * @see javaslang.Value#getOption()
     */
    public  Option<T> getOption() {
        return boxed.getOption();
    }
    /**
     * @param that
     * @return
     * @see javaslang.collection.Seq#indexOfSlice(java.lang.Iterable)
     */
    public  int indexOfSlice(Iterable<? extends T> that) {
        return boxed.indexOfSlice(that);
    }
    /**
     * @param other
     * @return
     * @see javaslang.Value#getOrElse(java.lang.Object)
     */
    public  T getOrElse(T other) {
        return boxed.getOrElse(other);
    }
    /**
     * @param supplier
     * @return
     * @see javaslang.Value#getOrElse(java.util.function.Supplier)
     */
    public  T getOrElse(Supplier<? extends T> supplier) {
        return boxed.getOrElse(supplier);
    }
    /**
     * @param that
     * @param from
     * @return
     * @see javaslang.collection.Seq#indexOfSlice(java.lang.Iterable, int)
     */
    public  int indexOfSlice(Iterable<? extends T> that, int from) {
        return boxed.indexOfSlice(that, from);
    }
    /**
     * @param supplier
     * @return
     * @throws X
     * @see javaslang.Value#getOrElseThrow(java.util.function.Supplier)
     */
    public  <X extends Throwable> T getOrElseThrow(Supplier<X> supplier) throws X {
        return boxed.getOrElseThrow(supplier);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.IndexedSeq#search(java.lang.Object)
     */
    public  int search(T element) {
        return boxed.search(element);
    }
    /**
     * @param supplier
     * @return
     * @see javaslang.Value#getOrElseTry(javaslang.control.Try.CheckedSupplier)
     */
    public  T getOrElseTry(CheckedSupplier<? extends T> supplier) {
        return boxed.getOrElseTry(supplier);
    }
    /**
     * @param p
     * @return
     * @see javaslang.collection.Seq#indexWhere(java.util.function.Predicate)
     */
    public  int indexWhere(Predicate<? super T> p) {
        return boxed.indexWhere(p);
    }
    /**
     * @param element
     * @param comparator
     * @return
     * @see javaslang.collection.IndexedSeq#search(java.lang.Object, java.util.Comparator)
     */
    public  int search(T element, Comparator<? super T> comparator) {
        return boxed.search(element, comparator);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Traversable#existsUnique(java.util.function.Predicate)
     */
    public  boolean existsUnique(Predicate<? super T> predicate) {
        return boxed.existsUnique(predicate);
    }
    /**
     * @param out
     * @see javaslang.Value#out(java.io.PrintStream)
     */
    public  void out(PrintStream out) {
        boxed.out(out);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Traversable#find(java.util.function.Predicate)
     */
    public  Option<T> find(Predicate<? super T> predicate) {
        return boxed.find(predicate);
    }
    /**
     * @param index
     * @return
     * @see javaslang.collection.Seq#iterator(int)
     */
    public  Iterator<T> iterator(int index) {
        return boxed.iterator(index);
    }
    /**
     * @param writer
     * @see javaslang.Value#out(java.io.PrintWriter)
     */
    public  void out(PrintWriter writer) {
        boxed.out(writer);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Traversable#findLast(java.util.function.Predicate)
     */
    public  Option<T> findLast(Predicate<? super T> predicate) {
        return boxed.findLast(predicate);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Seq#lastIndexOf(java.lang.Object)
     */
    public  int lastIndexOf(T element) {
        return boxed.lastIndexOf(element);
    }
    /**
     * 
     * @see javaslang.Value#stderr()
     */
    public  void stderr() {
        boxed.stderr();
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Seq#lastIndexWhere(java.util.function.Predicate)
     */
    public  int lastIndexWhere(Predicate<? super T> predicate) {
        return boxed.lastIndexWhere(predicate);
    }
    /**
     * @param zero
     * @param f
     * @return
     * @see javaslang.collection.Traversable#foldLeft(java.lang.Object, java.util.function.BiFunction)
     */
    public  <U> U foldLeft(U zero, BiFunction<? super U, ? super T, ? extends U> f) {
        return boxed.foldLeft(zero, f);
    }
    /**
     * 
     * @see javaslang.Value#stdout()
     */
    public  void stdout() {
        boxed.stdout();
    }
    /**
     * @return
     * @see javaslang.Value#toArray()
     */
    public  Array<T> toArray() {
        return boxed.toArray();
    }
    /**
     * @return
     * @see javaslang.Value#toCharSeq()
     */
    public  CharSeq toCharSeq() {
        return boxed.toCharSeq();
    }
    /**
     * @param that
     * @return
     * @see javaslang.collection.Seq#lastIndexOfSlice(java.lang.Iterable)
     */
    public  int lastIndexOfSlice(Iterable<? extends T> that) {
        return boxed.lastIndexOfSlice(that);
    }
    /**
     * @param factory
     * @return
     * @see javaslang.Value#toJavaCollection(java.util.function.Supplier)
     */
    public  <C extends Collection<T>> C toJavaCollection(Supplier<C> factory) {
        return boxed.toJavaCollection(factory);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#get()
     */
    public  T get() {
        return boxed.get();
    }
    /**
     * @return
     * @see javaslang.Value#toJavaArray()
     */
    public  Object[] toJavaArray() {
        return boxed.toJavaArray();
    }
    /**
     * @param componentType
     * @return
     * @see javaslang.Value#toJavaArray(java.lang.Class)
     */
    public  T[] toJavaArray(Class<T> componentType) {
        return boxed.toJavaArray(componentType);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Array#append(java.lang.Object)
     */
    public Array<T> append(T element) {
        return boxed.append(element);
    }
    /**
     * @param elements
     * @return
     * @see javaslang.collection.Array#appendAll(java.lang.Iterable)
     */
    public Array<T> appendAll(Iterable<? extends T> elements) {
        return boxed.appendAll(elements);
    }
    /**
     * @return
     * @see javaslang.collection.Array#hasDefiniteSize()
     */
    public boolean hasDefiniteSize() {
        return boxed.hasDefiniteSize();
    }
    /**
     * @return
     * @see javaslang.collection.Array#isTraversableAgain()
     */
    public boolean isTraversableAgain() {
        return boxed.isTraversableAgain();
    }
    /**
     * @return
     * @see javaslang.Value#toJavaList()
     */
    public  List<T> toJavaList() {
        return boxed.toJavaList();
    }
    /**
     * @return
     * @see javaslang.collection.Array#iterator()
     */
    public Iterator<T> iterator() {
        return boxed.iterator();
    }
    /**
     * @param factory
     * @return
     * @see javaslang.Value#toJavaList(java.util.function.Supplier)
     */
    public  <LIST extends List<T>> LIST toJavaList(Supplier<LIST> factory) {
        return boxed.toJavaList(factory);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Seq#prefixLength(java.util.function.Predicate)
     */
    public  int prefixLength(Predicate<? super T> predicate) {
        return boxed.prefixLength(predicate);
    }
    /**
     * @return
     * @see javaslang.collection.Array#combinations()
     */
    public Array<Array<T>> combinations() {
        return boxed.combinations();
    }
    /**
     * @param k
     * @return
     * @see javaslang.collection.Array#combinations(int)
     */
    public Array<Array<T>> combinations(int k) {
        return boxed.combinations(k);
    }
    /**
     * @param f
     * @return
     * @see javaslang.Value#toJavaMap(java.util.function.Function)
     */
    public  <K, V> Map<K, V> toJavaMap(Function<? super T, ? extends Tuple2<? extends K, ? extends V>> f) {
        return boxed.toJavaMap(f);
    }
    /**
     * @param power
     * @return
     * @see javaslang.collection.Array#crossProduct(int)
     */
    public Iterator<Array<T>> crossProduct(int power) {
        return boxed.crossProduct(power);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#headOption()
     */
    public  Option<T> headOption() {
        return boxed.headOption();
    }
    /**
     * @param index
     * @return
     * @see javaslang.collection.Array#get(int)
     */
    public T get(int index) {
        return boxed.get(index);
    }
    /**
     * @param factory
     * @param f
     * @return
     * @see javaslang.Value#toJavaMap(java.util.function.Supplier, java.util.function.Function)
     */
    public  <K, V, MAP extends Map<K, V>> MAP toJavaMap(Supplier<MAP> factory,
            Function<? super T, ? extends Tuple2<? extends K, ? extends V>> f) {
        return boxed.toJavaMap(factory, f);
    }
    /**
     * @return
     * @see javaslang.collection.Array#distinct()
     */
    public Array<T> distinct() {
        return boxed.distinct();
    }
    /**
     * @param comparator
     * @return
     * @see javaslang.collection.Array#distinctBy(java.util.Comparator)
     */
    public Array<T> distinctBy(Comparator<? super T> comparator) {
        return boxed.distinctBy(comparator);
    }
    /**
     * @param keyExtractor
     * @return
     * @see javaslang.collection.Array#distinctBy(java.util.function.Function)
     */
    public <U> Array<T> distinctBy(Function<? super T, ? extends U> keyExtractor) {
        return boxed.distinctBy(keyExtractor);
    }
    /**
     * @param n
     * @return
     * @see javaslang.collection.Array#drop(long)
     */
    public Array<T> drop(long n) {
        return boxed.drop(n);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#isSingleValued()
     */
    public  boolean isSingleValued() {
        return boxed.isSingleValued();
    }
    /**
     * @param n
     * @return
     * @see javaslang.collection.Array#dropRight(long)
     */
    public Array<T> dropRight(long n) {
        return boxed.dropRight(n);
    }
    /**
     * @return
     * @see javaslang.Value#toJavaOptional()
     */
    public  Optional<T> toJavaOptional() {
        return boxed.toJavaOptional();
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#dropUntil(java.util.function.Predicate)
     */
    public Array<T> dropUntil(Predicate<? super T> predicate) {
        return boxed.dropUntil(predicate);
    }
    /**
     * @return
     * @see javaslang.Value#toJavaSet()
     */
    public  Set<T> toJavaSet() {
        return boxed.toJavaSet();
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#dropWhile(java.util.function.Predicate)
     */
    public Array<T> dropWhile(Predicate<? super T> predicate) {
        return boxed.dropWhile(predicate);
    }
    /**
     * @param factory
     * @return
     * @see javaslang.Value#toJavaSet(java.util.function.Supplier)
     */
    public  <SET extends Set<T>> SET toJavaSet(Supplier<SET> factory) {
        return boxed.toJavaSet(factory);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#filter(java.util.function.Predicate)
     */
    public Array<T> filter(Predicate<? super T> predicate) {
        return boxed.filter(predicate);
    }
    /**
     * @return
     * @see javaslang.Value#toJavaStream()
     */
    public  Stream<T> toJavaStream() {
        return boxed.toJavaStream();
    }
    /**
     * @param mapper
     * @return
     * @see javaslang.collection.Array#flatMap(java.util.function.Function)
     */
    public <U> Array<U> flatMap(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return boxed.flatMap(mapper);
    }
    /**
     * @param right
     * @return
     * @see javaslang.Value#toLeft(java.util.function.Supplier)
     */
    public  <R> Either<T, R> toLeft(Supplier<? extends R> right) {
        return boxed.toLeft(right);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#lastOption()
     */
    public  Option<T> lastOption() {
        return boxed.lastOption();
    }
    /**
     * @param classifier
     * @return
     * @see javaslang.collection.Array#groupBy(java.util.function.Function)
     */
    public <C> javaslang.collection.Map<C, Array<T>> groupBy(Function<? super T, ? extends C> classifier) {
        return boxed.groupBy(classifier);
    }
    /**
     * @param right
     * @return
     * @see javaslang.Value#toLeft(java.lang.Object)
     */
    public  <R> Either<T, R> toLeft(R right) {
        return boxed.toLeft(right);
    }
    /**
     * @param size
     * @return
     * @see javaslang.collection.Array#grouped(long)
     */
    public Iterator<Array<T>> grouped(long size) {
        return boxed.grouped(size);
    }
    /**
     * @return
     * @see javaslang.collection.Array#head()
     */
    public T head() {
        return boxed.head();
    }
    /**
     * @return
     * @see javaslang.Value#toList()
     */
    public  javaslang.collection.List<T> toList() {
        return boxed.toList();
    }
    /**
     * @param element
     * @param from
     * @return
     * @see javaslang.collection.Array#indexOf(java.lang.Object, int)
     */
    public int indexOf(T element, int from) {
        return boxed.indexOf(element, from);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#max()
     */
    public  Option<T> max() {
        return boxed.max();
    }
    /**
     * @param f
     * @return
     * @see javaslang.Value#toMap(java.util.function.Function)
     */
    public  <K, V> javaslang.collection.Map<K, V> toMap(
            Function<? super T, ? extends Tuple2<? extends K, ? extends V>> f) {
        return boxed.toMap(f);
    }
    /**
     * @return
     * @see javaslang.collection.Array#init()
     */
    public Array<T> init() {
        return boxed.init();
    }
    /**
     * @return
     * @see javaslang.collection.Array#initOption()
     */
    public Option<Array<T>> initOption() {
        return boxed.initOption();
    }
    /**
     * @return
     * @see javaslang.collection.Array#isEmpty()
     */
    public boolean isEmpty() {
        return boxed.isEmpty();
    }
    /**
     * @param comparator
     * @return
     * @see javaslang.collection.Traversable#maxBy(java.util.Comparator)
     */
    public  Option<T> maxBy(Comparator<? super T> comparator) {
        return boxed.maxBy(comparator);
    }
    /**
     * @return
     * @see javaslang.Value#toOption()
     */
    public  Option<T> toOption() {
        return boxed.toOption();
    }
    /**
     * @param index
     * @param element
     * @return
     * @see javaslang.collection.Array#insert(int, java.lang.Object)
     */
    public Array<T> insert(int index, T element) {
        return boxed.insert(index, element);
    }
    /**
     * @return
     * @see javaslang.Value#toQueue()
     */
    public  Queue<T> toQueue() {
        return boxed.toQueue();
    }
    /**
     * @param left
     * @return
     * @see javaslang.Value#toRight(java.util.function.Supplier)
     */
    public  <L> Either<L, T> toRight(Supplier<? extends L> left) {
        return boxed.toRight(left);
    }
    /**
     * @param f
     * @return
     * @see javaslang.collection.Traversable#maxBy(java.util.function.Function)
     */
    public  <U extends Comparable<? super U>> Option<T> maxBy(Function<? super T, ? extends U> f) {
        return boxed.maxBy(f);
    }
    /**
     * @param index
     * @param elements
     * @return
     * @see javaslang.collection.Array#insertAll(int, java.lang.Iterable)
     */
    public Array<T> insertAll(int index, Iterable<? extends T> elements) {
        return boxed.insertAll(index, elements);
    }
    /**
     * @param left
     * @return
     * @see javaslang.Value#toRight(java.lang.Object)
     */
    public  <L> Either<L, T> toRight(L left) {
        return boxed.toRight(left);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Array#intersperse(java.lang.Object)
     */
    public Array<T> intersperse(T element) {
        return boxed.intersperse(element);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#min()
     */
    public  Option<T> min() {
        return boxed.min();
    }
    /**
     * @return
     * @see javaslang.Value#toSet()
     */
    public  javaslang.collection.Set<T> toSet() {
        return boxed.toSet();
    }
    /**
     * @return
     * @see javaslang.Value#toStack()
     */
    public  Stack<T> toStack() {
        return boxed.toStack();
    }
    /**
     * @param element
     * @param end
     * @return
     * @see javaslang.collection.Array#lastIndexOf(java.lang.Object, int)
     */
    public int lastIndexOf(T element, int end) {
        return boxed.lastIndexOf(element, end);
    }
    /**
     * @param that
     * @return
     * @see javaslang.collection.Seq#startsWith(java.lang.Iterable)
     */
    public  boolean startsWith(Iterable<? extends T> that) {
        return boxed.startsWith(that);
    }
    /**
     * @return
     * @see javaslang.Value#toStream()
     */
    public  javaslang.collection.Stream<T> toStream() {
        return boxed.toStream();
    }
    /**
     * @return
     * @see javaslang.collection.Array#length()
     */
    public int length() {
        return boxed.length();
    }
    /**
     * @param mapper
     * @return
     * @see javaslang.collection.Array#map(java.util.function.Function)
     */
    public <U> Array<U> map(Function<? super T, ? extends U> mapper) {
        return boxed.map(mapper);
    }
    /**
     * @param comparator
     * @return
     * @see javaslang.collection.Traversable#minBy(java.util.Comparator)
     */
    public  Option<T> minBy(Comparator<? super T> comparator) {
        return boxed.minBy(comparator);
    }
    /**
     * @return
     * @see javaslang.Value#toTry()
     */
    public  Try<T> toTry() {
        return boxed.toTry();
    }
    /**
     * @param length
     * @param element
     * @return
     * @see javaslang.collection.Array#padTo(int, java.lang.Object)
     */
    public Array<T> padTo(int length, T element) {
        return boxed.padTo(length, element);
    }
    /**
     * @param ifEmpty
     * @return
     * @see javaslang.Value#toTry(java.util.function.Supplier)
     */
    public  Try<T> toTry(Supplier<? extends Throwable> ifEmpty) {
        return boxed.toTry(ifEmpty);
    }
    /**
     * @param from
     * @param that
     * @param replaced
     * @return
     * @see javaslang.collection.Array#patch(int, java.lang.Iterable, int)
     */
    public Array<T> patch(int from, Iterable<? extends T> that, int replaced) {
        return boxed.patch(from, that, replaced);
    }
    /**
     * @param f
     * @return
     * @see javaslang.collection.Traversable#minBy(java.util.function.Function)
     */
    public  <U extends Comparable<? super U>> Option<T> minBy(Function<? super T, ? extends U> f) {
        return boxed.minBy(f);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#partition(java.util.function.Predicate)
     */
    public Tuple2<Array<T>, Array<T>> partition(Predicate<? super T> predicate) {
        return boxed.partition(predicate);
    }
    /**
     * @return
     * @see javaslang.Value#toTree()
     */
    public  Tree<T> toTree() {
        return boxed.toTree();
    }
    /**
     * @return
     * @see javaslang.Value#toVector()
     */
    public  Vector<T> toVector() {
        return boxed.toVector();
    }
    /**
     * @param action
     * @return
     * @see javaslang.collection.Array#peek(java.util.function.Consumer)
     */
    public Array<T> peek(Consumer<? super T> action) {
        return boxed.peek(action);
    }
    /**
     * @return
     * @see javaslang.collection.Array#permutations()
     */
    public Array<Array<T>> permutations() {
        return boxed.permutations();
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#mkString()
     */
    public  String mkString() {
        return boxed.mkString();
    }
    /**
     * @param delimiter
     * @return
     * @see javaslang.collection.Traversable#mkString(java.lang.CharSequence)
     */
    public  String mkString(CharSequence delimiter) {
        return boxed.mkString(delimiter);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Array#prepend(java.lang.Object)
     */
    public Array<T> prepend(T element) {
        return boxed.prepend(element);
    }
    /**
     * @param elements
     * @return
     * @see javaslang.collection.Array#prependAll(java.lang.Iterable)
     */
    public Array<T> prependAll(Iterable<? extends T> elements) {
        return boxed.prependAll(elements);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Array#remove(java.lang.Object)
     */
    public Array<T> remove(T element) {
        return boxed.remove(element);
    }
    /**
     * @param prefix
     * @param delimiter
     * @param suffix
     * @return
     * @see javaslang.collection.Traversable#mkString(java.lang.CharSequence, java.lang.CharSequence, java.lang.CharSequence)
     */
    public  String mkString(CharSequence prefix, CharSequence delimiter, CharSequence suffix) {
        return boxed.mkString(prefix, delimiter, suffix);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#removeFirst(java.util.function.Predicate)
     */
    public Array<T> removeFirst(Predicate<T> predicate) {
        return boxed.removeFirst(predicate);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#removeLast(java.util.function.Predicate)
     */
    public Array<T> removeLast(Predicate<T> predicate) {
        return boxed.removeLast(predicate);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#nonEmpty()
     */
    public  boolean nonEmpty() {
        return boxed.nonEmpty();
    }
    /**
     * @param index
     * @return
     * @see javaslang.collection.Array#removeAt(int)
     */
    public Array<T> removeAt(int index) {
        return boxed.removeAt(index);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Array#removeAll(java.lang.Object)
     */
    public Array<T> removeAll(T element) {
        return boxed.removeAll(element);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#product()
     */
    public  Number product() {
        return boxed.product();
    }
    /**
     * @param elements
     * @return
     * @see javaslang.collection.Array#removeAll(java.lang.Iterable)
     */
    public Array<T> removeAll(Iterable<? extends T> elements) {
        return boxed.removeAll(elements);
    }
    /**
     * @param currentElement
     * @param newElement
     * @return
     * @see javaslang.collection.Array#replace(java.lang.Object, java.lang.Object)
     */
    public Array<T> replace(T currentElement, T newElement) {
        return boxed.replace(currentElement, newElement);
    }
    /**
     * @param currentElement
     * @param newElement
     * @return
     * @see javaslang.collection.Array#replaceAll(java.lang.Object, java.lang.Object)
     */
    public Array<T> replaceAll(T currentElement, T newElement) {
        return boxed.replaceAll(currentElement, newElement);
    }
    /**
     * @param op
     * @return
     * @see javaslang.collection.Traversable#reduceLeft(java.util.function.BiFunction)
     */
    public  T reduceLeft(BiFunction<? super T, ? super T, ? extends T> op) {
        return boxed.reduceLeft(op);
    }
    /**
     * @param zero
     * @param f
     * @return
     * @see javaslang.collection.Seq#foldRight(java.lang.Object, java.util.function.BiFunction)
     */
    public  <U> U foldRight(U zero, BiFunction<? super T, ? super U, ? extends U> f) {
        return boxed.foldRight(zero, f);
    }
    /**
     * @param elements
     * @return
     * @see javaslang.collection.Array#retainAll(java.lang.Iterable)
     */
    public Array<T> retainAll(Iterable<? extends T> elements) {
        return boxed.retainAll(elements);
    }
    /**
     * @param op
     * @return
     * @see javaslang.collection.Traversable#reduceLeftOption(java.util.function.BiFunction)
     */
    public  Option<T> reduceLeftOption(BiFunction<? super T, ? super T, ? extends T> op) {
        return boxed.reduceLeftOption(op);
    }
    /**
     * @return
     * @see javaslang.collection.Array#reverse()
     */
    public Array<T> reverse() {
        return boxed.reverse();
    }
    /**
     * @param op
     * @return
     * @see javaslang.collection.Traversable#reduceRight(java.util.function.BiFunction)
     */
    public  T reduceRight(BiFunction<? super T, ? super T, ? extends T> op) {
        return boxed.reduceRight(op);
    }
    /**
     * @param zero
     * @param operation
     * @return
     * @see javaslang.collection.Array#scan(java.lang.Object, java.util.function.BiFunction)
     */
    public Array<T> scan(T zero, BiFunction<? super T, ? super T, ? extends T> operation) {
        return boxed.scan(zero, operation);
    }
    /**
     * @param zero
     * @param operation
     * @return
     * @see javaslang.collection.Array#scanLeft(java.lang.Object, java.util.function.BiFunction)
     */
    public <U> Array<U> scanLeft(U zero, BiFunction<? super U, ? super T, ? extends U> operation) {
        return boxed.scanLeft(zero, operation);
    }
    /**
     * @param op
     * @return
     * @see javaslang.collection.Traversable#reduceRightOption(java.util.function.BiFunction)
     */
    public  Option<T> reduceRightOption(BiFunction<? super T, ? super T, ? extends T> op) {
        return boxed.reduceRightOption(op);
    }
    /**
     * @param zero
     * @param operation
     * @return
     * @see javaslang.collection.Array#scanRight(java.lang.Object, java.util.function.BiFunction)
     */
    public <U> Array<U> scanRight(U zero, BiFunction<? super T, ? super U, ? extends U> operation) {
        return boxed.scanRight(zero, operation);
    }
    /**
     * @param beginIndex
     * @param endIndex
     * @return
     * @see javaslang.collection.Array#slice(long, long)
     */
    public Array<T> slice(long beginIndex, long endIndex) {
        return boxed.slice(beginIndex, endIndex);
    }
    /**
     * @param size
     * @return
     * @see javaslang.collection.Array#sliding(long)
     */
    public Iterator<Array<T>> sliding(long size) {
        return boxed.sliding(size);
    }
    /**
     * @param size
     * @param step
     * @return
     * @see javaslang.collection.Array#sliding(long, long)
     */
    public Iterator<Array<T>> sliding(long size, long step) {
        return boxed.sliding(size, step);
    }
    /**
     * @return
     * @see javaslang.collection.Array#sorted()
     */
    public Array<T> sorted() {
        return boxed.sorted();
    }
    /**
     * @param comparator
     * @return
     * @see javaslang.collection.Array#sorted(java.util.Comparator)
     */
    public Array<T> sorted(Comparator<? super T> comparator) {
        return boxed.sorted(comparator);
    }
    /**
     * @param mapper
     * @return
     * @see javaslang.collection.Array#sortBy(java.util.function.Function)
     */
    public <U extends Comparable<? super U>> Array<T> sortBy(Function<? super T, ? extends U> mapper) {
        return boxed.sortBy(mapper);
    }
    /**
     * @param comparator
     * @param mapper
     * @return
     * @see javaslang.collection.Array#sortBy(java.util.Comparator, java.util.function.Function)
     */
    public <U> Array<T> sortBy(Comparator<? super U> comparator, Function<? super T, ? extends U> mapper) {
        return boxed.sortBy(comparator, mapper);
    }
    /**
     * @param n
     * @return
     * @see javaslang.collection.Array#splitAt(long)
     */
    public Tuple2<Array<T>, Array<T>> splitAt(long n) {
        return boxed.splitAt(n);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#splitAt(java.util.function.Predicate)
     */
    public Tuple2<Array<T>, Array<T>> splitAt(Predicate<? super T> predicate) {
        return boxed.splitAt(predicate);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#splitAtInclusive(java.util.function.Predicate)
     */
    public Tuple2<Array<T>, Array<T>> splitAtInclusive(Predicate<? super T> predicate) {
        return boxed.splitAtInclusive(predicate);
    }
    /**
     * @return
     * @see javaslang.collection.Array#spliterator()
     */
    public Spliterator<T> spliterator() {
        return boxed.spliterator();
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#span(java.util.function.Predicate)
     */
    public Tuple2<Array<T>, Array<T>> span(Predicate<? super T> predicate) {
        return boxed.span(predicate);
    }
    /**
     * @param beginIndex
     * @return
     * @see javaslang.collection.Array#subSequence(int)
     */
    public Array<T> subSequence(int beginIndex) {
        return boxed.subSequence(beginIndex);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#size()
     */
    public  int size() {
        return boxed.size();
    }
    /**
     * @param beginIndex
     * @param endIndex
     * @return
     * @see javaslang.collection.Array#subSequence(int, int)
     */
    public Array<T> subSequence(int beginIndex, int endIndex) {
        return boxed.subSequence(beginIndex, endIndex);
    }
    /**
     * @return
     * @see javaslang.collection.Array#tail()
     */
    public Array<T> tail() {
        return boxed.tail();
    }
    /**
     * @return
     * @see javaslang.collection.Array#tailOption()
     */
    public Option<Array<T>> tailOption() {
        return boxed.tailOption();
    }
    /**
     * @param n
     * @return
     * @see javaslang.collection.Array#take(long)
     */
    public Array<T> take(long n) {
        return boxed.take(n);
    }
    /**
     * @param n
     * @return
     * @see javaslang.collection.Array#takeRight(long)
     */
    public Array<T> takeRight(long n) {
        return boxed.takeRight(n);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#takeUntil(java.util.function.Predicate)
     */
    public Array<T> takeUntil(Predicate<? super T> predicate) {
        return boxed.takeUntil(predicate);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Array#takeWhile(java.util.function.Predicate)
     */
    public Array<T> takeWhile(Predicate<? super T> predicate) {
        return boxed.takeWhile(predicate);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#sum()
     */
    public  Number sum() {
        return boxed.sum();
    }
    /**
     * @param f
     * @return
     * @see javaslang.collection.Array#transform(java.util.function.Function)
     */
    public <U> U transform(Function<? super Array<T>, ? extends U> f) {
        return boxed.transform(f);
    }
    /**
     * @param iterable
     * @return
     * @see javaslang.collection.Array#unit(java.lang.Iterable)
     */
    public <U> Array<U> unit(Iterable<? extends U> iterable) {
        return boxed.unit(iterable);
    }
    /**
     * @param unzipper
     * @return
     * @see javaslang.collection.Array#unzip(java.util.function.Function)
     */
    public <T1, T2> Tuple2<Array<T1>, Array<T2>> unzip(
            Function<? super T, Tuple2<? extends T1, ? extends T2>> unzipper) {
        return boxed.unzip(unzipper);
    }
    /**
     * @param unzipper
     * @return
     * @see javaslang.collection.Array#unzip3(java.util.function.Function)
     */
    public <T1, T2, T3> Tuple3<Array<T1>, Array<T2>, Array<T3>> unzip3(
            Function<? super T, Tuple3<? extends T1, ? extends T2, ? extends T3>> unzipper) {
        return boxed.unzip3(unzipper);
    }
    /**
     * @param index
     * @param element
     * @return
     * @see javaslang.collection.Array#update(int, java.lang.Object)
     */
    public Array<T> update(int index, T element) {
        return boxed.update(index, element);
    }
    /**
     * @param that
     * @return
     * @see javaslang.collection.Array#zip(java.lang.Iterable)
     */
    public <U> Array<Tuple2<T, U>> zip(Iterable<? extends U> that) {
        return boxed.zip(that);
    }
    /**
     * @param that
     * @param thisElem
     * @param thatElem
     * @return
     * @see javaslang.collection.Array#zipAll(java.lang.Iterable, java.lang.Object, java.lang.Object)
     */
    public <U> Array<Tuple2<T, U>> zipAll(Iterable<? extends U> that, T thisElem, U thatElem) {
        return boxed.zipAll(that, thisElem, thatElem);
    }
    /**
     * @return
     * @see javaslang.collection.Array#zipWithIndex()
     */
    public Array<Tuple2<T, Long>> zipWithIndex() {
        return boxed.zipWithIndex();
    }
    /**
     * @return
     * @see javaslang.collection.Array#stringPrefix()
     */
    public String stringPrefix() {
        return boxed.stringPrefix();
    }

      
}
