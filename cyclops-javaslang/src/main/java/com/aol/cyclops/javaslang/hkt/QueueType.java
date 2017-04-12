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


import com.aol.cyclops2.hkt.Higher;
import javaslang.Function1;
import javaslang.Tuple1;
import javaslang.Tuple2;
import javaslang.Tuple3;
import javaslang.collection.Array;
import javaslang.collection.CharSeq;
import javaslang.collection.Iterator;
import javaslang.collection.LinearSeq;
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
 * Simulates Higher Kinded Types for Queue's
 * 
 * QueueType is a Queue and a Higher Kinded Type (QueueType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Queue
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class QueueType<T> implements Higher<QueueType.µ, T>, LinearSeq<T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    public static <T> QueueType<T> of(T element) {
        return  widen(Queue.of(element));
    }

   
    @SafeVarargs
    public static <T> QueueType<T> of(T... elements) {
        return widen(Queue.of(elements));
    }
    /**
     * Convert a Queue to a simulated HigherKindedType that captures Queue nature
     * and Queue element data type separately. Recover via @see QueueType#narrow
     * 
     * If the supplied Queue implements QueueType it is returned already, otherwise it
     * is wrapped into a Queue implementation that does implement QueueType
     * 
     * @param list Queue to widen to a QueueType
     * @return QueueType encoding HKT info about Queues
     */
    public static <T> QueueType<T> widen(final Queue<T> list) {
        
        return new QueueType<>(list);
    }
    /**
     * Widen a QueueType nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a Queue to widen
     * @return HKT encoded type with a widened Queue
     */
    public static <C2,T> Higher<C2, Higher<QueueType.µ,T>> widen2(Higher<C2, QueueType<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<QueueType.µ,T> must be a QueueType
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for Queue types into the QueueType type definition class
     * 
     * @param list HKT encoded list into a QueueType
     * @return QueueType
     */
    public static <T> QueueType<T> narrowK(final Higher<QueueType.µ, T> list) {
       return (QueueType<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a Queue into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return QueueX from Higher Kinded Type
     */
    public static <T> Queue<T> narrow(final Higher<QueueType.µ, T> list) {
        return ((QueueType)list).narrow();
       
    }


    private final Queue<T> boxed;

    /**
     * @return This back as a QueueX
     */
    public Queue<T> narrow() {
        return (Queue) (boxed);
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
        return "QueueType [" + boxed + "]";
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
     * @param predicate
     * @param from
     * @return
     * @see javaslang.collection.LinearSeq#indexWhere(java.util.function.Predicate, int)
     */
    public  int indexWhere(Predicate<? super T> predicate, int from) {
        return boxed.indexWhere(predicate, from);
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
     * @param that
     * @param end
     * @return
     * @see javaslang.collection.LinearSeq#lastIndexOfSlice(java.lang.Iterable, int)
     */
    public  int lastIndexOfSlice(Iterable<? extends T> that, int end) {
        return boxed.lastIndexOfSlice(that, end);
    }
    /**
     * @param predicate
     * @param end
     * @return
     * @see javaslang.collection.LinearSeq#lastIndexWhere(java.util.function.Predicate, int)
     */
    public  int lastIndexWhere(Predicate<? super T> predicate, int end) {
        return boxed.lastIndexWhere(predicate, end);
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
     * @return
     * @see javaslang.collection.LinearSeq#reverseIterator()
     */
    public  Iterator<T> reverseIterator() {
        return boxed.reverseIterator();
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
     * @param predicate
     * @param from
     * @return
     * @see javaslang.collection.LinearSeq#segmentLength(java.util.function.Predicate, int)
     */
    public  int segmentLength(Predicate<? super T> predicate, int from) {
        return boxed.segmentLength(predicate, from);
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
     * @param elements
     * @return
     * @see javaslang.collection.Traversable#containsAll(java.lang.Iterable)
     */
    public  boolean containsAll(Iterable<? extends T> elements) {
        return boxed.containsAll(elements);
    }
    /**
     * @param that
     * @return
     * @see javaslang.collection.Seq#endsWith(javaslang.collection.Seq)
     */
    public  boolean endsWith(Seq<? extends T> that) {
        return boxed.endsWith(that);
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
     * @see javaslang.collection.LinearSeq#search(java.lang.Object)
     */
    public  int search(T element) {
        return boxed.search(element);
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
     * @param element
     * @param comparator
     * @return
     * @see javaslang.collection.LinearSeq#search(java.lang.Object, java.util.Comparator)
     */
    public  int search(T element, Comparator<? super T> comparator) {
        return boxed.search(element, comparator);
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
     * @return
     * @see javaslang.Value#toJavaList()
     */
    public  List<T> toJavaList() {
        return boxed.toJavaList();
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
     * @param f
     * @return
     * @see javaslang.Value#toJavaMap(java.util.function.Function)
     */
    public  <K, V> Map<K, V> toJavaMap(Function<? super T, ? extends Tuple2<? extends K, ? extends V>> f) {
        return boxed.toJavaMap(f);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#headOption()
     */
    public  Option<T> headOption() {
        return boxed.headOption();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#dequeue()
     */
    public Tuple2<T, Queue<T>> dequeue() {
        return boxed.dequeue();
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
     * @see javaslang.collection.Queue#dequeueOption()
     */
    public Option<Tuple2<T, Queue<T>>> dequeueOption() {
        return boxed.dequeueOption();
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Queue#enqueue(java.lang.Object)
     */
    public Queue<T> enqueue(T element) {
        return boxed.enqueue(element);
    }
    /**
     * @param elements
     * @return
     * @see javaslang.collection.Queue#enqueue(java.lang.Object[])
     */
    public Queue<T> enqueue(T... elements) {
        return boxed.enqueue(elements);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#isSingleValued()
     */
    public  boolean isSingleValued() {
        return boxed.isSingleValued();
    }
    /**
     * @return
     * @see javaslang.Value#toJavaOptional()
     */
    public  Optional<T> toJavaOptional() {
        return boxed.toJavaOptional();
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#iterator()
     */
    public  Iterator<T> iterator() {
        return boxed.iterator();
    }
    /**
     * @return
     * @see javaslang.Value#toJavaSet()
     */
    public  Set<T> toJavaSet() {
        return boxed.toJavaSet();
    }
    /**
     * @param elements
     * @return
     * @see javaslang.collection.Queue#enqueueAll(java.lang.Iterable)
     */
    public Queue<T> enqueueAll(Iterable<? extends T> elements) {
        return boxed.enqueueAll(elements);
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
     * @return
     * @see javaslang.Value#toJavaStream()
     */
    public  Stream<T> toJavaStream() {
        return boxed.toJavaStream();
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#last()
     */
    public  T last() {
        return boxed.last();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#peek()
     */
    public T peek() {
        return boxed.peek();
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
     * @see javaslang.collection.Queue#peekOption()
     */
    public Option<T> peekOption() {
        return boxed.peekOption();
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#lastOption()
     */
    public  Option<T> lastOption() {
        return boxed.lastOption();
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Queue#append(java.lang.Object)
     */
    public Queue<T> append(T element) {
        return boxed.append(element);
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
     * @param elements
     * @return
     * @see javaslang.collection.Queue#appendAll(java.lang.Iterable)
     */
    public Queue<T> appendAll(Iterable<? extends T> elements) {
        return boxed.appendAll(elements);
    }
    /**
     * @return
     * @see javaslang.collection.Queue#combinations()
     */
    public Queue<Queue<T>> combinations() {
        return boxed.combinations();
    }
    /**
     * @param k
     * @return
     * @see javaslang.collection.Queue#combinations(int)
     */
    public Queue<Queue<T>> combinations(int k) {
        return boxed.combinations(k);
    }
    /**
     * @return
     * @see javaslang.Value#toList()
     */
    public  javaslang.collection.List<T> toList() {
        return boxed.toList();
    }
    /**
     * @param power
     * @return
     * @see javaslang.collection.Queue#crossProduct(int)
     */
    public Iterator<Queue<T>> crossProduct(int power) {
        return boxed.crossProduct(power);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#max()
     */
    public  Option<T> max() {
        return boxed.max();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#distinct()
     */
    public Queue<T> distinct() {
        return boxed.distinct();
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
     * @param comparator
     * @return
     * @see javaslang.collection.Queue#distinctBy(java.util.Comparator)
     */
    public Queue<T> distinctBy(Comparator<? super T> comparator) {
        return boxed.distinctBy(comparator);
    }
    /**
     * @param keyExtractor
     * @return
     * @see javaslang.collection.Queue#distinctBy(java.util.function.Function)
     */
    public <U> Queue<T> distinctBy(Function<? super T, ? extends U> keyExtractor) {
        return boxed.distinctBy(keyExtractor);
    }
    /**
     * @param n
     * @return
     * @see javaslang.collection.Queue#drop(long)
     */
    public Queue<T> drop(long n) {
        return boxed.drop(n);
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
     * @param n
     * @return
     * @see javaslang.collection.Queue#dropRight(long)
     */
    public Queue<T> dropRight(long n) {
        return boxed.dropRight(n);
    }
    /**
     * @return
     * @see javaslang.Value#toQueue()
     */
    public  Queue<T> toQueue() {
        return boxed.toQueue();
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#dropUntil(java.util.function.Predicate)
     */
    public Queue<T> dropUntil(Predicate<? super T> predicate) {
        return boxed.dropUntil(predicate);
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
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#dropWhile(java.util.function.Predicate)
     */
    public Queue<T> dropWhile(Predicate<? super T> predicate) {
        return boxed.dropWhile(predicate);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#filter(java.util.function.Predicate)
     */
    public Queue<T> filter(Predicate<? super T> predicate) {
        return boxed.filter(predicate);
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
     * @param mapper
     * @return
     * @see javaslang.collection.Queue#flatMap(java.util.function.Function)
     */
    public <U> Queue<U> flatMap(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        return boxed.flatMap(mapper);
    }
    /**
     * @param index
     * @return
     * @see javaslang.collection.Queue#get(int)
     */
    public T get(int index) {
        return boxed.get(index);
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
     * @param classifier
     * @return
     * @see javaslang.collection.Queue#groupBy(java.util.function.Function)
     */
    public <C> javaslang.collection.Map<C, Queue<T>> groupBy(Function<? super T, ? extends C> classifier) {
        return boxed.groupBy(classifier);
    }
    /**
     * @param size
     * @return
     * @see javaslang.collection.Queue#grouped(long)
     */
    public Iterator<Queue<T>> grouped(long size) {
        return boxed.grouped(size);
    }
    /**
     * @return
     * @see javaslang.collection.Queue#hasDefiniteSize()
     */
    public boolean hasDefiniteSize() {
        return boxed.hasDefiniteSize();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#head()
     */
    public T head() {
        return boxed.head();
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
     * @param element
     * @param from
     * @return
     * @see javaslang.collection.Queue#indexOf(java.lang.Object, int)
     */
    public int indexOf(T element, int from) {
        return boxed.indexOf(element, from);
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
     * @return
     * @see javaslang.Value#toTree()
     */
    public  Tree<T> toTree() {
        return boxed.toTree();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#init()
     */
    public Queue<T> init() {
        return boxed.init();
    }
    /**
     * @return
     * @see javaslang.Value#toVector()
     */
    public  Vector<T> toVector() {
        return boxed.toVector();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#initOption()
     */
    public Option<Queue<T>> initOption() {
        return boxed.initOption();
    }
    /**
     * @param index
     * @param element
     * @return
     * @see javaslang.collection.Queue#insert(int, java.lang.Object)
     */
    public Queue<T> insert(int index, T element) {
        return boxed.insert(index, element);
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
     * @param index
     * @param elements
     * @return
     * @see javaslang.collection.Queue#insertAll(int, java.lang.Iterable)
     */
    public Queue<T> insertAll(int index, Iterable<? extends T> elements) {
        return boxed.insertAll(index, elements);
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
     * @return
     * @see javaslang.collection.Traversable#nonEmpty()
     */
    public  boolean nonEmpty() {
        return boxed.nonEmpty();
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Queue#intersperse(java.lang.Object)
     */
    public Queue<T> intersperse(T element) {
        return boxed.intersperse(element);
    }
    /**
     * @return
     * @see javaslang.collection.Queue#isEmpty()
     */
    public boolean isEmpty() {
        return boxed.isEmpty();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#isTraversableAgain()
     */
    public boolean isTraversableAgain() {
        return boxed.isTraversableAgain();
    }
    /**
     * @param element
     * @param end
     * @return
     * @see javaslang.collection.Queue#lastIndexOf(java.lang.Object, int)
     */
    public int lastIndexOf(T element, int end) {
        return boxed.lastIndexOf(element, end);
    }
    /**
     * @return
     * @see javaslang.collection.Queue#length()
     */
    public int length() {
        return boxed.length();
    }
    /**
     * @param mapper
     * @return
     * @see javaslang.collection.Queue#map(java.util.function.Function)
     */
    public <U> Queue<U> map(Function<? super T, ? extends U> mapper) {
        return boxed.map(mapper);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#product()
     */
    public  Number product() {
        return boxed.product();
    }
    /**
     * @param length
     * @param element
     * @return
     * @see javaslang.collection.Queue#padTo(int, java.lang.Object)
     */
    public Queue<T> padTo(int length, T element) {
        return boxed.padTo(length, element);
    }
    /**
     * @param from
     * @param that
     * @param replaced
     * @return
     * @see javaslang.collection.Queue#patch(int, java.lang.Iterable, int)
     */
    public Queue<T> patch(int from, Iterable<? extends T> that, int replaced) {
        return boxed.patch(from, that, replaced);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#partition(java.util.function.Predicate)
     */
    public Tuple2<Queue<T>, Queue<T>> partition(Predicate<? super T> predicate) {
        return boxed.partition(predicate);
    }
    /**
     * @param action
     * @return
     * @see javaslang.collection.Queue#peek(java.util.function.Consumer)
     */
    public Queue<T> peek(Consumer<? super T> action) {
        return boxed.peek(action);
    }
    /**
     * @return
     * @see javaslang.collection.Queue#permutations()
     */
    public Queue<Queue<T>> permutations() {
        return boxed.permutations();
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Queue#prepend(java.lang.Object)
     */
    public Queue<T> prepend(T element) {
        return boxed.prepend(element);
    }
    /**
     * @param elements
     * @return
     * @see javaslang.collection.Queue#prependAll(java.lang.Iterable)
     */
    public Queue<T> prependAll(Iterable<? extends T> elements) {
        return boxed.prependAll(elements);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Queue#remove(java.lang.Object)
     */
    public Queue<T> remove(T element) {
        return boxed.remove(element);
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
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#removeFirst(java.util.function.Predicate)
     */
    public Queue<T> removeFirst(Predicate<T> predicate) {
        return boxed.removeFirst(predicate);
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
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#removeLast(java.util.function.Predicate)
     */
    public Queue<T> removeLast(Predicate<T> predicate) {
        return boxed.removeLast(predicate);
    }
    /**
     * @param index
     * @return
     * @see javaslang.collection.Queue#removeAt(int)
     */
    public Queue<T> removeAt(int index) {
        return boxed.removeAt(index);
    }
    /**
     * @param element
     * @return
     * @see javaslang.collection.Queue#removeAll(java.lang.Object)
     */
    public Queue<T> removeAll(T element) {
        return boxed.removeAll(element);
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
     * @param elements
     * @return
     * @see javaslang.collection.Queue#removeAll(java.lang.Iterable)
     */
    public Queue<T> removeAll(Iterable<? extends T> elements) {
        return boxed.removeAll(elements);
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
     * @param currentElement
     * @param newElement
     * @return
     * @see javaslang.collection.Queue#replace(java.lang.Object, java.lang.Object)
     */
    public Queue<T> replace(T currentElement, T newElement) {
        return boxed.replace(currentElement, newElement);
    }
    /**
     * @param currentElement
     * @param newElement
     * @return
     * @see javaslang.collection.Queue#replaceAll(java.lang.Object, java.lang.Object)
     */
    public Queue<T> replaceAll(T currentElement, T newElement) {
        return boxed.replaceAll(currentElement, newElement);
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
     * @param elements
     * @return
     * @see javaslang.collection.Queue#retainAll(java.lang.Iterable)
     */
    public Queue<T> retainAll(Iterable<? extends T> elements) {
        return boxed.retainAll(elements);
    }
    /**
     * @return
     * @see javaslang.collection.Queue#reverse()
     */
    public Queue<T> reverse() {
        return boxed.reverse();
    }
    /**
     * @param zero
     * @param operation
     * @return
     * @see javaslang.collection.Queue#scan(java.lang.Object, java.util.function.BiFunction)
     */
    public Queue<T> scan(T zero, BiFunction<? super T, ? super T, ? extends T> operation) {
        return boxed.scan(zero, operation);
    }
    /**
     * @param zero
     * @param operation
     * @return
     * @see javaslang.collection.Queue#scanLeft(java.lang.Object, java.util.function.BiFunction)
     */
    public <U> Queue<U> scanLeft(U zero, BiFunction<? super U, ? super T, ? extends U> operation) {
        return boxed.scanLeft(zero, operation);
    }
    /**
     * @param zero
     * @param operation
     * @return
     * @see javaslang.collection.Queue#scanRight(java.lang.Object, java.util.function.BiFunction)
     */
    public <U> Queue<U> scanRight(U zero, BiFunction<? super T, ? super U, ? extends U> operation) {
        return boxed.scanRight(zero, operation);
    }
    /**
     * @param beginIndex
     * @param endIndex
     * @return
     * @see javaslang.collection.Queue#slice(long, long)
     */
    public Queue<T> slice(long beginIndex, long endIndex) {
        return boxed.slice(beginIndex, endIndex);
    }
    /**
     * @param size
     * @return
     * @see javaslang.collection.Queue#sliding(long)
     */
    public Iterator<Queue<T>> sliding(long size) {
        return boxed.sliding(size);
    }
    /**
     * @param size
     * @param step
     * @return
     * @see javaslang.collection.Queue#sliding(long, long)
     */
    public Iterator<Queue<T>> sliding(long size, long step) {
        return boxed.sliding(size, step);
    }
    /**
     * @return
     * @see javaslang.collection.Queue#sorted()
     */
    public Queue<T> sorted() {
        return boxed.sorted();
    }
    /**
     * @param comparator
     * @return
     * @see javaslang.collection.Queue#sorted(java.util.Comparator)
     */
    public Queue<T> sorted(Comparator<? super T> comparator) {
        return boxed.sorted(comparator);
    }
    /**
     * @param mapper
     * @return
     * @see javaslang.collection.Queue#sortBy(java.util.function.Function)
     */
    public <U extends Comparable<? super U>> Queue<T> sortBy(Function<? super T, ? extends U> mapper) {
        return boxed.sortBy(mapper);
    }
    /**
     * @param comparator
     * @param mapper
     * @return
     * @see javaslang.collection.Queue#sortBy(java.util.Comparator, java.util.function.Function)
     */
    public <U> Queue<T> sortBy(Comparator<? super U> comparator, Function<? super T, ? extends U> mapper) {
        return boxed.sortBy(comparator, mapper);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#span(java.util.function.Predicate)
     */
    public Tuple2<Queue<T>, Queue<T>> span(Predicate<? super T> predicate) {
        return boxed.span(predicate);
    }
    /**
     * @param n
     * @return
     * @see javaslang.collection.Queue#splitAt(long)
     */
    public Tuple2<Queue<T>, Queue<T>> splitAt(long n) {
        return boxed.splitAt(n);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#splitAt(java.util.function.Predicate)
     */
    public Tuple2<Queue<T>, Queue<T>> splitAt(Predicate<? super T> predicate) {
        return boxed.splitAt(predicate);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#splitAtInclusive(java.util.function.Predicate)
     */
    public Tuple2<Queue<T>, Queue<T>> splitAtInclusive(Predicate<? super T> predicate) {
        return boxed.splitAtInclusive(predicate);
    }
    /**
     * @param that
     * @param offset
     * @return
     * @see javaslang.collection.Queue#startsWith(java.lang.Iterable, int)
     */
    public boolean startsWith(Iterable<? extends T> that, int offset) {
        return boxed.startsWith(that, offset);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#size()
     */
    public  int size() {
        return boxed.size();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#spliterator()
     */
    public Spliterator<T> spliterator() {
        return boxed.spliterator();
    }
    /**
     * @param beginIndex
     * @return
     * @see javaslang.collection.Queue#subSequence(int)
     */
    public Queue<T> subSequence(int beginIndex) {
        return boxed.subSequence(beginIndex);
    }
    /**
     * @param beginIndex
     * @param endIndex
     * @return
     * @see javaslang.collection.Queue#subSequence(int, int)
     */
    public Queue<T> subSequence(int beginIndex, int endIndex) {
        return boxed.subSequence(beginIndex, endIndex);
    }
    /**
     * @return
     * @see javaslang.collection.Queue#tail()
     */
    public Queue<T> tail() {
        return boxed.tail();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#tailOption()
     */
    public Option<Queue<T>> tailOption() {
        return boxed.tailOption();
    }
    /**
     * @param n
     * @return
     * @see javaslang.collection.Queue#take(long)
     */
    public Queue<T> take(long n) {
        return boxed.take(n);
    }
    /**
     * @param n
     * @return
     * @see javaslang.collection.Queue#takeRight(long)
     */
    public Queue<T> takeRight(long n) {
        return boxed.takeRight(n);
    }
    /**
     * @return
     * @see javaslang.collection.Traversable#sum()
     */
    public  Number sum() {
        return boxed.sum();
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#takeUntil(java.util.function.Predicate)
     */
    public Queue<T> takeUntil(Predicate<? super T> predicate) {
        return boxed.takeUntil(predicate);
    }
    /**
     * @param predicate
     * @return
     * @see javaslang.collection.Queue#takeWhile(java.util.function.Predicate)
     */
    public Queue<T> takeWhile(Predicate<? super T> predicate) {
        return boxed.takeWhile(predicate);
    }
    /**
     * @param f
     * @return
     * @see javaslang.collection.Queue#transform(java.util.function.Function)
     */
    public <U> U transform(Function<? super Queue<T>, ? extends U> f) {
        return boxed.transform(f);
    }
    /**
     * @param iterable
     * @return
     * @see javaslang.collection.Queue#unit(java.lang.Iterable)
     */
    public <U> Queue<U> unit(Iterable<? extends U> iterable) {
        return boxed.unit(iterable);
    }
    /**
     * @param unzipper
     * @return
     * @see javaslang.collection.Queue#unzip(java.util.function.Function)
     */
    public <T1, T2> Tuple2<Queue<T1>, Queue<T2>> unzip(
            Function<? super T, Tuple2<? extends T1, ? extends T2>> unzipper) {
        return boxed.unzip(unzipper);
    }
    /**
     * @param unzipper
     * @return
     * @see javaslang.collection.Queue#unzip3(java.util.function.Function)
     */
    public <T1, T2, T3> Tuple3<Queue<T1>, Queue<T2>, Queue<T3>> unzip3(
            Function<? super T, Tuple3<? extends T1, ? extends T2, ? extends T3>> unzipper) {
        return boxed.unzip3(unzipper);
    }
    /**
     * @param index
     * @param element
     * @return
     * @see javaslang.collection.Queue#update(int, java.lang.Object)
     */
    public Queue<T> update(int index, T element) {
        return boxed.update(index, element);
    }
    /**
     * @param that
     * @return
     * @see javaslang.collection.Queue#zip(java.lang.Iterable)
     */
    public <U> Queue<Tuple2<T, U>> zip(Iterable<? extends U> that) {
        return boxed.zip(that);
    }
    /**
     * @param that
     * @param thisElem
     * @param thatElem
     * @return
     * @see javaslang.collection.Queue#zipAll(java.lang.Iterable, java.lang.Object, java.lang.Object)
     */
    public <U> Queue<Tuple2<T, U>> zipAll(Iterable<? extends U> that, T thisElem, U thatElem) {
        return boxed.zipAll(that, thisElem, thatElem);
    }
    /**
     * @return
     * @see javaslang.collection.Queue#zipWithIndex()
     */
    public Queue<Tuple2<T, Long>> zipWithIndex() {
        return boxed.zipWithIndex();
    }
    /**
     * @return
     * @see javaslang.collection.Queue#stringPrefix()
     */
    public String stringPrefix() {
        return boxed.stringPrefix();
    }

      
}
