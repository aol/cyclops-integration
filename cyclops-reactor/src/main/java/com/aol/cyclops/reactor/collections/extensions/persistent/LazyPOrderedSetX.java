package com.aol.cyclops.reactor.collections.extensions.persistent;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.pcollections.PCollection;
import org.pcollections.POrderedSet;
import org.pcollections.PSet;
import org.reactivestreams.Publisher;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Reducer;
import com.aol.cyclops.Reducers;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.data.collections.extensions.persistent.POrderedSetX;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.reactor.Fluxes;
import com.aol.cyclops.reactor.collections.extensions.base.AbstractFluentCollectionX;
import com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollection;
import com.aol.cyclops.reactor.collections.extensions.base.NativePlusLoop;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Flux;

/**
 * An extended OrderedSet type {@see java.util.Set}
 * This makes use of POrderedSet (@see org.pcollections.POrderedSet) from PCollectons.
 * 
 * Extended OrderedSet operations execute lazily e.g.
 * <pre>
 * {@code 
 *    LazyPOrderedSetX<Integer> q = LazyPOrderedSetX.of(1,2,3)
 *                                    .map(i->i*2);
 * }
 * </pre>
 * The map operation above is not executed immediately. It will only be executed when (if) the data inside the
 * POrderedSet is accessed. This allows lazy operations to be chained and executed more efficiently e.g.
 * 
 * <pre>
 * {@code 
 *    LazyPOrderedSetX<Integer> q = LazyPOrderedSetX.of(1,2,3)
 *                                    .map(i->i*2);
 *                                    .filter(i->i<5);
 * }
 * </pre>
 * 
 * The operation above is more efficient than the equivalent operation with a POrderedSetX.

 * NB. Because LazyPOrderedSetX transform operations are performed Lazily, they may result in a different result 
 * than POrderedSetX operations that are performed eagerly. For example a sequence of map operations that result in
 * duplicate keys, may result in a different OrderedSet being produced.
 * 
 * @author johnmcclean
 *
 * @param <T> the type of elements held in this collection
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LazyPOrderedSetX<T> extends AbstractFluentCollectionX<T>implements POrderedSetX<T> {
    private final LazyFluentCollection<T, POrderedSet<T>> lazy;
    @Getter
    private final Reducer<POrderedSet<T>> collector;

    
    @Override
    public LazyPOrderedSetX<T> plusLoop(int max, IntFunction<T> value){
        POrderedSet<T> list = lazy.get();
        if(list instanceof NativePlusLoop){
            return (LazyPOrderedSetX<T>) ((NativePlusLoop)list).plusLoop(max, value);
        }else{
            return (LazyPOrderedSetX<T>) super.plusLoop(max, value);
        }
    }
    @Override
    public LazyPOrderedSetX<T> plusLoop(Supplier<Optional<T>> supplier){
        POrderedSet<T> list = lazy.get();
        if(list instanceof NativePlusLoop){
            return (LazyPOrderedSetX<T>) ((NativePlusLoop)list).plusLoop(supplier);
        }else{
            return (LazyPOrderedSetX<T>) super.plusLoop(supplier);
        }
    }
    public static <T> LazyPOrderedSetX<T> fromPOrderedSet(POrderedSet<T> list,Reducer<POrderedSet<T>> collector){
        return new LazyPOrderedSetX<T>(list,collector);
    }
    /**
     * Create a LazyPStackX from a Stream
     * 
     * @param stream to construct a LazyQueueX from
     * @return LazyPStackX
     */
    public static <T> LazyPOrderedSetX<T> fromStreamS(Stream<T> stream) {
        return new LazyPOrderedSetX<T>(
                                       Flux.from(ReactiveSeq.fromStream(stream)));
    }

    /**
     * Create a LazyPStackX that contains the Integers between start and end
     * 
     * @param start
     *            Number of range to start from
     * @param end
     *            Number for range to end at
     * @return Range ListX
     */
    public static LazyPOrderedSetX<Integer> range(int start, int end) {
        return fromStreamS(ReactiveSeq.range(start, end));
    }

    /**
     * Create a LazyPStackX that contains the Longs between start and end
     * 
     * @param start
     *            Number of range to start from
     * @param end
     *            Number for range to end at
     * @return Range ListX
     */
    public static LazyPOrderedSetX<Long> rangeLong(long start, long end) {
        return fromStreamS(ReactiveSeq.rangeLong(start, end));
    }

    /**
     * Unfold a function into a ListX
     * 
     * <pre>
     * {@code 
     *  LazyPStackX.unfold(1,i->i<=6 ? Optional.of(Tuple.tuple(i,i+1)) : Optional.empty());
     * 
     * //(1,2,3,4,5)
     * 
     * }</pre>
     * 
     * @param seed Initial value 
     * @param unfolder Iteratively applied function, terminated by an empty Optional
     * @return ListX generated by unfolder function
     */
    public static <U, T> LazyPOrderedSetX<T> unfold(U seed, Function<? super U, Optional<Tuple2<T, U>>> unfolder) {
        return fromStreamS(ReactiveSeq.unfold(seed, unfolder));
    }

    /**
     * Generate a LazyPStackX from the provided Supplier up to the provided limit number of times
     * 
     * @param limit Max number of elements to generate
     * @param s Supplier to generate ListX elements
     * @return ListX generated from the provided Supplier
     */
    public static <T> LazyPOrderedSetX<T> generate(long limit, Supplier<T> s) {

        return fromStreamS(ReactiveSeq.generate(s)
                                      .limit(limit));
    }

    /**
     * Create a LazyPStackX by iterative application of a function to an initial element up to the supplied limit number of times
     * 
     * @param limit Max number of elements to generate
     * @param seed Initial element
     * @param f Iteratively applied to each element to generate the next element
     * @return ListX generated by iterative application
     */
    public static <T> LazyPOrderedSetX<T> iterate(long limit, final T seed, final UnaryOperator<T> f) {
        return fromStreamS(ReactiveSeq.iterate(seed, f)
                                      .limit(limit));
    }

    /**
     * @return A collector that generates a LazyPStackX
     */
    public static <T> Collector<T, ?, LazyPOrderedSetX<T>> lazyListXCollector() {
        return Collectors.toCollection(() -> LazyPOrderedSetX.of());
    }

    /**
     * @return An empty LazyPStackX
     */
    public static <T> LazyPOrderedSetX<T> empty() {
        return fromIterable((List<T>) ListX.<T> defaultCollector()
                                           .supplier()
                                           .get());
    }

    /**
     * Create a LazyPStackX from the specified values
     * <pre>
     * {@code 
     *     ListX<Integer> lazy = LazyPStackX.of(1,2,3,4,5);
     *     
     *     //lazily map List
     *     ListX<String> mapped = lazy.map(i->"mapped " +i); 
     *     
     *     String value = mapped.get(0); //transformation triggered now
     * }
     * </pre>
     * 
     * @param values To populate LazyPStackX with
     * @return LazyPStackX
     */
    @SafeVarargs
    public static <T> LazyPOrderedSetX<T> of(T... values) {
        List<T> res = (List<T>) ListX.<T> defaultCollector()
                                     .supplier()
                                     .get();
        for (T v : values)
            res.add(v);
        return fromIterable(res);
    }

    /**
     * Construct a LazyPStackX with a single value
     * <pre>
     * {@code 
     *    ListX<Integer> lazy = LazyPStackX.singleton(5);
     *    
     * }
     * </pre>
     * 
     * 
     * @param value To populate LazyPStackX with
     * @return LazyPStackX with a single value
     */
    public static <T> LazyPOrderedSetX<T> singleton(T value) {
        return LazyPOrderedSetX.<T> of(value);
    }

    /**
     * Construct a LazyPStackX from an Publisher
     * 
     * @param publisher
     *            to construct LazyPStackX from
     * @return ListX
     */
    public static <T> LazyPOrderedSetX<T> fromPublisher(Publisher<? extends T> publisher) {
        return fromStreamS(ReactiveSeq.fromPublisher((Publisher<T>) publisher));
    }

    /**
     * Construct LazyPStackX from an Iterable
     * 
     * @param it to construct LazyPStackX from
     * @return LazyPStackX from Iterable
     */
    public static <T> LazyPOrderedSetX<T> fromIterable(Iterable<T> it) {
        return fromIterable(Reducers.toPOrderedSet(), it);
    }

    /**
     * Construct a LazyPStackX from an Iterable, using the specified Collector.
     * 
     * @param collector To generate Lists from, this can be used to create mutable vs immutable Lists (for example), or control List type (ArrayList, LinkedList)
     * @param it Iterable to construct LazyPStackX from
     * @return Newly constructed LazyPStackX
     */
    public static <T> LazyPOrderedSetX<T> fromIterable(Reducer<POrderedSet<T>> collector, Iterable<T> it) {
        if (it instanceof LazyPOrderedSetX)
            return (LazyPOrderedSetX<T>) it;

        if (it instanceof POrderedSet)
            return new LazyPOrderedSetX<T>(
                                           (POrderedSet<T>) it, collector);

        return new LazyPOrderedSetX<T>(
                                       Flux.fromIterable(it), collector);
    }

    private LazyPOrderedSetX(POrderedSet<T> list, Reducer<POrderedSet<T>> collector) {
        this.lazy = new PersistentLazyCollection<T, POrderedSet<T>>(
                                                                    list, null, collector);
        this.collector = collector;
    }

    private LazyPOrderedSetX(boolean efficientOps, POrderedSet<T> list, Reducer<POrderedSet<T>> collector) {
        this.lazy = new PersistentLazyCollection<T, POrderedSet<T>>(
                                                                    list, null, collector);
        this.collector = collector;
    }

    private LazyPOrderedSetX(POrderedSet<T> list) {
        this.collector = Reducers.toPOrderedSet();
        this.lazy = new PersistentLazyCollection<T, POrderedSet<T>>(
                                                                    list, null, Reducers.toPOrderedSet());
    }

    public LazyPOrderedSetX(Flux<T> stream, Reducer<POrderedSet<T>> collector) {
        this.collector = collector;
        this.lazy = new PersistentLazyCollection<>(
                                                   null, stream, Reducers.toPOrderedSet());
    }

    private LazyPOrderedSetX(Flux<T> stream) {
        this.collector = Reducers.toPOrderedSet();
        this.lazy = new PersistentLazyCollection<>(
                                                   null, stream, collector);
    }

    private LazyPOrderedSetX() {
        this.collector = Reducers.toPOrderedSet();
        this.lazy = new PersistentLazyCollection<>(
                                                   (POrderedSet) this.collector.zero(), null, collector);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#forEach(java.util.function.Consumer)
     */
    @Override
    public void forEach(Consumer<? super T> action) {
        getSet().forEach(action);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return getSet().iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#size()
     */
    @Override
    public int size() {
        return getSet().size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object e) {
        return getSet().contains(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return getSet().equals(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return getSet().isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getSet().hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#toArray()
     */
    @Override
    public Object[] toArray() {
        return getSet().toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return getSet().removeAll(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#toArray(java.lang.Object[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return getSet().toArray(a);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#add(java.lang.Object)
     */
    @Override
    public boolean add(T e) {
        return getSet().add(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return getSet().remove(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return getSet().containsAll(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        return getSet().addAll(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return getSet().retainAll(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#clear()
     */
    @Override
    public void clear() {
        getSet().clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getSet().toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jooq.lambda.Collectable#collect(java.util.stream.Collector)
     */
    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        return stream().collect(collector);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jooq.lambda.Collectable#count()
     */
    @Override
    public long count() {
        return this.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#removeIf(java.util.function.Predicate)
     */
    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return getSet().removeIf(filter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Collection#parallelStream()
     */
    @Override
    public Stream<T> parallelStream() {
        return getSet().parallelStream();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#spliterator()
     */
    @Override
    public Spliterator<T> spliterator() {
        return getSet().spliterator();
    }

    /**
     * @return POrderedSet
     */
    private POrderedSet<T> getSet() {
        return lazy.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#stream(reactor.core.publisher.Flux)
     */
    @Override
    public <X> LazyPOrderedSetX<X> stream(Flux<X> stream) {
        return new LazyPOrderedSetX<X>(
                                       stream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#flux()
     */
    @Override
    public Flux<T> flux() {
        return lazy.flux();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#combine(java.util.function.BiPredicate,
     * java.util.function.BinaryOperator)
     */
    @Override
    public LazyPOrderedSetX<T> combine(BiPredicate<? super T, ? super T> predicate, BinaryOperator<T> op) {

        return (LazyPOrderedSetX<T>) super.combine(predicate, op);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#reverse()
     */
    @Override
    public LazyPOrderedSetX<T> reverse() {

        return (LazyPOrderedSetX<T>) super.reverse();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#filter(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> filter(Predicate<? super T> pred) {

        return (LazyPOrderedSetX<T>) super.filter(pred);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#map(java.util.function.Function)
     */
    @Override
    public <R> LazyPOrderedSetX<R> map(Function<? super T, ? extends R> mapper) {

        return (LazyPOrderedSetX<R>) super.map(mapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#flatMap(java.util.function.Function)
     */
    @Override
    public <R> LazyPOrderedSetX<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return (LazyPOrderedSetX<R>) super.flatMap(mapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#limit(long)
     */
    @Override
    public LazyPOrderedSetX<T> limit(long num) {
        return (LazyPOrderedSetX<T>) super.limit(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#skip(long)
     */
    @Override
    public LazyPOrderedSetX<T> skip(long num) {
        return (LazyPOrderedSetX<T>) super.skip(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#takeRight(int)
     */
    @Override
    public LazyPOrderedSetX<T> takeRight(int num) {
        return (LazyPOrderedSetX<T>) super.takeRight(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#dropRight(int)
     */
    @Override
    public LazyPOrderedSetX<T> dropRight(int num) {
        return (LazyPOrderedSetX<T>) super.dropRight(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#takeWhile(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> takeWhile(Predicate<? super T> p) {
        return (LazyPOrderedSetX<T>) super.takeWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#dropWhile(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> dropWhile(Predicate<? super T> p) {
        return (LazyPOrderedSetX<T>) super.dropWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#takeUntil(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> takeUntil(Predicate<? super T> p) {
        return (LazyPOrderedSetX<T>) super.takeUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#dropUntil(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> dropUntil(Predicate<? super T> p) {
        return (LazyPOrderedSetX<T>) super.dropUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#trampoline(java.util.function.Function)
     */
    @Override
    public <R> LazyPOrderedSetX<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
        return (LazyPOrderedSetX<R>) super.trampoline(mapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#slice(long, long)
     */
    @Override
    public LazyPOrderedSetX<T> slice(long from, long to) {
        return (LazyPOrderedSetX<T>) super.slice(from, to);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#grouped(int)
     */
    @Override
    public LazyPOrderedSetX<ListX<T>> grouped(int groupSize) {

        return (LazyPOrderedSetX<ListX<T>>) super.grouped(groupSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#grouped(java.util.function.Function,
     * java.util.stream.Collector)
     */
    @Override
    public <K, A, D> LazyPOrderedSetX<Tuple2<K, D>> grouped(Function<? super T, ? extends K> classifier,
            Collector<? super T, A, D> downstream) {

        return (LazyPOrderedSetX) super.grouped(classifier, downstream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#grouped(java.util.function.Function)
     */
    @Override
    public <K> LazyPOrderedSetX<Tuple2<K, Seq<T>>> grouped(Function<? super T, ? extends K> classifier) {

        return (LazyPOrderedSetX) super.grouped(classifier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zip(java.lang.Iterable)
     */
    @Override
    public <U> LazyPOrderedSetX<Tuple2<T, U>> zip(Iterable<? extends U> other) {

        return (LazyPOrderedSetX) super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zip(java.lang.Iterable,
     * java.util.function.BiFunction)
     */
    @Override
    public <U, R> LazyPOrderedSetX<R> zip(Iterable<? extends U> other,
            BiFunction<? super T, ? super U, ? extends R> zipper) {

        return (LazyPOrderedSetX<R>) super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#sliding(int)
     */
    @Override
    public LazyPOrderedSetX<ListX<T>> sliding(int windowSize) {

        return (LazyPOrderedSetX<ListX<T>>) super.sliding(windowSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#sliding(int, int)
     */
    @Override
    public LazyPOrderedSetX<ListX<T>> sliding(int windowSize, int increment) {

        return (LazyPOrderedSetX<ListX<T>>) super.sliding(windowSize, increment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#scanLeft(com.aol.cyclops.Monoid)
     */
    @Override
    public LazyPOrderedSetX<T> scanLeft(Monoid<T> monoid) {

        return (LazyPOrderedSetX<T>) super.scanLeft(monoid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#scanLeft(java.lang.Object,
     * java.util.function.BiFunction)
     */
    @Override
    public <U> LazyPOrderedSetX<U> scanLeft(U seed, BiFunction<? super U, ? super T, ? extends U> function) {

        return (LazyPOrderedSetX<U>) super.scanLeft(seed, function);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#scanRight(com.aol.cyclops.Monoid)
     */
    @Override
    public LazyPOrderedSetX<T> scanRight(Monoid<T> monoid) {

        return (LazyPOrderedSetX<T>) super.scanRight(monoid);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#scanRight(java.lang.Object,
     * java.util.function.BiFunction)
     */
    @Override
    public <U> LazyPOrderedSetX<U> scanRight(U identity, BiFunction<? super T, ? super U, ? extends U> combiner) {

        return (LazyPOrderedSetX<U>) super.scanRight(identity, combiner);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#sorted(java.util.function.Function)
     */
    @Override
    public <U extends Comparable<? super U>> LazyPOrderedSetX<T> sorted(Function<? super T, ? extends U> function) {

        return (LazyPOrderedSetX<T>) super.sorted(function);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#plusLazy(java.lang.Object)
     */
    @Override
    public LazyPOrderedSetX<T> plusLazy(T e) {

        return (LazyPOrderedSetX<T>) super.plusLazy(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#plusAllLazy(java.util.Collection)
     */
    @Override
    public LazyPOrderedSetX<T> plusAllLazy(Collection<? extends T> list) {

        return (LazyPOrderedSetX<T>) super.plusAllLazy(list);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#minusLazy(java.lang.Object)
     */
    @Override
    public LazyPOrderedSetX<T> minusLazy(Object e) {

        return (LazyPOrderedSetX<T>) super.minusLazy(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#minusAllLazy(java.util.Collection)
     */
    @Override
    public LazyPOrderedSetX<T> minusAllLazy(Collection<?> list) {

        return (LazyPOrderedSetX<T>) super.minusAllLazy(list);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#cycle(int)
     */
    @Override
    public LazyPStackX<T> cycle(int times) {
        return LazyPStackX.fromPublisher(Flux.from(this.stream()
                                                       .cycle(times)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#cycle(com.aol.cyclops.Monoid, int)
     */
    @Override
    public LazyPStackX<T> cycle(Monoid<T> m, int times) {
        return LazyPStackX.fromPublisher(Flux.from(this.stream()
                                                       .cycle(m, times)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#cycleWhile(java.util.function.Predicate)
     */
    @Override
    public LazyPStackX<T> cycleWhile(Predicate<? super T> predicate) {
        return LazyPStackX.fromPublisher(Flux.from(this.stream()
                                                       .cycleWhile(predicate)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#cycleUntil(java.util.function.Predicate)
     */
    @Override
    public LazyPStackX<T> cycleUntil(Predicate<? super T> predicate) {
        return LazyPStackX.fromPublisher(Flux.from(this.stream()
                                                       .cycleUntil(predicate)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zip(org.jooq.lambda.Seq)
     */
    @Override
    public <U> LazyPOrderedSetX<Tuple2<T, U>> zip(Seq<? extends U> other) {

        return (LazyPOrderedSetX) super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zip3(java.util.stream.Stream,
     * java.util.stream.Stream)
     */
    @Override
    public <S, U> LazyPOrderedSetX<Tuple3<T, S, U>> zip3(Stream<? extends S> second, Stream<? extends U> third) {

        return (LazyPOrderedSetX) super.zip3(second, third);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zip4(java.util.stream.Stream,
     * java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <T2, T3, T4> LazyPOrderedSetX<Tuple4<T, T2, T3, T4>> zip4(Stream<? extends T2> second,
            Stream<? extends T3> third, Stream<? extends T4> fourth) {

        return (LazyPOrderedSetX) super.zip4(second, third, fourth);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zipWithIndex()
     */
    @Override
    public LazyPOrderedSetX<Tuple2<T, Long>> zipWithIndex() {

        return (LazyPOrderedSetX<Tuple2<T, Long>>) super.zipWithIndex();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#distinct()
     */
    @Override
    public LazyPOrderedSetX<T> distinct() {

        return (LazyPOrderedSetX<T>) super.distinct();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#sorted()
     */
    @Override
    public LazyPOrderedSetX<T> sorted() {

        return (LazyPOrderedSetX<T>) super.sorted();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#sorted(java.util.Comparator)
     */
    @Override
    public LazyPOrderedSetX<T> sorted(Comparator<? super T> c) {

        return (LazyPOrderedSetX<T>) super.sorted(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#skipWhile(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> skipWhile(Predicate<? super T> p) {

        return (LazyPOrderedSetX<T>) super.skipWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#skipUntil(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> skipUntil(Predicate<? super T> p) {

        return (LazyPOrderedSetX<T>) super.skipUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#limitWhile(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> limitWhile(Predicate<? super T> p) {

        return (LazyPOrderedSetX<T>) super.limitWhile(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#limitUntil(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> limitUntil(Predicate<? super T> p) {

        return (LazyPOrderedSetX<T>) super.limitUntil(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#intersperse(java.lang.Object)
     */
    @Override
    public LazyPOrderedSetX<T> intersperse(T value) {

        return (LazyPOrderedSetX<T>) super.intersperse(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#shuffle()
     */
    @Override
    public LazyPOrderedSetX<T> shuffle() {

        return (LazyPOrderedSetX<T>) super.shuffle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#skipLast(int)
     */
    @Override
    public LazyPOrderedSetX<T> skipLast(int num) {

        return (LazyPOrderedSetX<T>) super.skipLast(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#limitLast(int)
     */
    @Override
    public LazyPOrderedSetX<T> limitLast(int num) {

        return (LazyPOrderedSetX<T>) super.limitLast(num);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#onEmpty(java.lang.Object)
     */
    @Override
    public LazyPOrderedSetX<T> onEmpty(T value) {

        return (LazyPOrderedSetX<T>) super.onEmpty(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#onEmptyGet(java.util.function.Supplier)
     */
    @Override
    public LazyPOrderedSetX<T> onEmptyGet(Supplier<? extends T> supplier) {

        return (LazyPOrderedSetX<T>) super.onEmptyGet(supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#onEmptyThrow(java.util.function.Supplier)
     */
    @Override
    public <X extends Throwable> LazyPOrderedSetX<T> onEmptyThrow(Supplier<? extends X> supplier) {

        return (LazyPOrderedSetX<T>) super.onEmptyThrow(supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#shuffle(java.util.Random)
     */
    @Override
    public LazyPOrderedSetX<T> shuffle(Random random) {

        return (LazyPOrderedSetX<T>) super.shuffle(random);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#ofType(java.lang.Class)
     */
    @Override
    public <U> LazyPOrderedSetX<U> ofType(Class<? extends U> type) {

        return (LazyPOrderedSetX<U>) super.ofType(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#filterNot(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<T> filterNot(Predicate<? super T> fn) {

        return (LazyPOrderedSetX<T>) super.filterNot(fn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#notNull()
     */
    @Override
    public LazyPOrderedSetX<T> notNull() {

        return (LazyPOrderedSetX<T>) super.notNull();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#removeAll(java.util.stream.Stream)
     */
    @Override
    public LazyPOrderedSetX<T> removeAll(Stream<? extends T> stream) {

        return (LazyPOrderedSetX<T>) (super.removeAll(stream));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#removeAll(org.jooq.lambda.Seq)
     */
    @Override
    public LazyPOrderedSetX<T> removeAll(Seq<? extends T> stream) {

        return (LazyPOrderedSetX<T>) super.removeAll(stream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#removeAll(java.lang.Iterable)
     */
    @Override
    public LazyPOrderedSetX<T> removeAll(Iterable<? extends T> it) {

        return (LazyPOrderedSetX<T>) super.removeAll(it);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#removeAll(java.lang.Object[])
     */
    @Override
    public LazyPOrderedSetX<T> removeAll(T... values) {

        return (LazyPOrderedSetX<T>) super.removeAll(values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#retainAll(java.lang.Iterable)
     */
    @Override
    public LazyPOrderedSetX<T> retainAll(Iterable<? extends T> it) {

        return (LazyPOrderedSetX<T>) super.retainAll(it);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#retainAll(java.util.stream.Stream)
     */
    @Override
    public LazyPOrderedSetX<T> retainAll(Stream<? extends T> stream) {

        return (LazyPOrderedSetX<T>) super.retainAll(stream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#retainAll(org.jooq.lambda.Seq)
     */
    @Override
    public LazyPOrderedSetX<T> retainAll(Seq<? extends T> stream) {

        return (LazyPOrderedSetX<T>) super.retainAll(stream);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#retainAll(java.lang.Object[])
     */
    @Override
    public LazyPOrderedSetX<T> retainAll(T... values) {

        return (LazyPOrderedSetX<T>) super.retainAll(values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#cast(java.lang.Class)
     */
    @Override
    public <U> LazyPOrderedSetX<U> cast(Class<? extends U> type) {

        return (LazyPOrderedSetX<U>) super.cast(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#patternMatch(java.util.function.Function,
     * java.util.function.Supplier)
     */
    @Override
    public <R> LazyPOrderedSetX<R> patternMatch(Function<CheckValue1<T, R>, CheckValue1<T, R>> case1,
            Supplier<? extends R> otherwise) {

        return (LazyPOrderedSetX<R>) super.patternMatch(case1, otherwise);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#permutations()
     */
    @Override
    public LazyPOrderedSetX<ReactiveSeq<T>> permutations() {

        return (LazyPOrderedSetX<ReactiveSeq<T>>) super.permutations();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#combinations(int)
     */
    @Override
    public LazyPOrderedSetX<ReactiveSeq<T>> combinations(int size) {

        return (LazyPOrderedSetX<ReactiveSeq<T>>) super.combinations(size);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#combinations()
     */
    @Override
    public LazyPOrderedSetX<ReactiveSeq<T>> combinations() {

        return (LazyPOrderedSetX<ReactiveSeq<T>>) super.combinations();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#grouped(int, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> LazyPOrderedSetX<C> grouped(int size, Supplier<C> supplier) {

        return (LazyPOrderedSetX<C>) super.grouped(size, supplier);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#groupedUntil(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<ListX<T>> groupedUntil(Predicate<? super T> predicate) {

        return (LazyPOrderedSetX<ListX<T>>) super.groupedUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#groupedWhile(java.util.function.Predicate)
     */
    @Override
    public LazyPOrderedSetX<ListX<T>> groupedWhile(Predicate<? super T> predicate) {

        return (LazyPOrderedSetX<ListX<T>>) super.groupedWhile(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#groupedWhile(java.util.function.Predicate,
     * java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> LazyPOrderedSetX<C> groupedWhile(Predicate<? super T> predicate,
            Supplier<C> factory) {

        return (LazyPOrderedSetX<C>) super.groupedWhile(predicate, factory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#groupedUntil(java.util.function.Predicate,
     * java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> LazyPOrderedSetX<C> groupedUntil(Predicate<? super T> predicate,
            Supplier<C> factory) {

        return (LazyPOrderedSetX<C>) super.groupedUntil(predicate, factory);
    }

    /** PStackX methods **/

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.standard.ListX#with(int,
     * java.lang.Object)
     */
    public LazyPOrderedSetX<T> with(int i, T element) {
        return stream(Fluxes.insertAt(Fluxes.deleteBetween(flux(), i, i + 1), i, element));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#groupedStatefullyUntil(java.util.function.
     * BiPredicate)
     */
    @Override
    public LazyPOrderedSetX<ListX<T>> groupedStatefullyUntil(BiPredicate<ListX<? super T>, ? super T> predicate) {

        return (LazyPOrderedSetX<ListX<T>>) super.groupedStatefullyUntil(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#peek(java.util.function.Consumer)
     */
    @Override
    public LazyPOrderedSetX<T> peek(Consumer<? super T> c) {

        return (LazyPOrderedSetX) super.peek(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zip(org.jooq.lambda.Seq,
     * java.util.function.BiFunction)
     */
    @Override
    public <U, R> LazyPOrderedSetX<R> zip(Seq<? extends U> other,
            BiFunction<? super T, ? super U, ? extends R> zipper) {

        return (LazyPOrderedSetX<R>) super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zip(java.util.stream.Stream,
     * java.util.function.BiFunction)
     */
    @Override
    public <U, R> LazyPOrderedSetX<R> zip(Stream<? extends U> other,
            BiFunction<? super T, ? super U, ? extends R> zipper) {

        return (LazyPOrderedSetX<R>) super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zip(java.util.stream.Stream)
     */
    @Override
    public <U> LazyPOrderedSetX<Tuple2<T, U>> zip(Stream<? extends U> other) {

        return (LazyPOrderedSetX) super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#zip(java.util.function.BiFunction,
     * org.reactivestreams.Publisher)
     */
    @Override
    public <T2, R> LazyPOrderedSetX<R> zip(BiFunction<? super T, ? super T2, ? extends R> fn,
            Publisher<? extends T2> publisher) {

        return (LazyPOrderedSetX<R>) super.zip(fn, publisher);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.standard.ListX#onEmptySwitch(
     * java.util.function.Supplier)
     */
    @Override
    public LazyPOrderedSetX<T> onEmptySwitch(Supplier<? extends POrderedSet<T>> supplier) {
        return stream(Fluxes.onEmptySwitch(flux(), () -> Flux.fromIterable(supplier.get())));

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.standard.ListX#unit(
     * Collection)
     */
    @Override
    public <R> LazyPOrderedSetX<R> unit(Collection<R> col) {

        return fromIterable(col);
    }

    @Override
    public <R> LazyPOrderedSetX<R> unit(R value) {
        return singleton(value);
    }

    @Override
    public <R> LazyPOrderedSetX<R> unitIterator(Iterator<R> it) {
        return fromIterable(() -> it);
    }

    @Override
    public <R> LazyPOrderedSetX<R> emptyUnit() {

        return LazyPOrderedSetX.<R> empty();
    }

    /**
     * @return This converted to PVector
     */
    public LazyPOrderedSetX<T> toPVector() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX
     * #plusInOrder(java.lang.Object)
     */
    @Override
    public LazyPOrderedSetX<T> plusInOrder(T e) {
        return plus(e);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.data.collections.extensions.CollectionX#stream()
     */
    @Override
    public ReactiveSeq<T> stream() {

        return ReactiveSeq.fromIterable(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#from(java.util.Collection)
     */
    @Override
    public <X> LazyPOrderedSetX<X> from(Collection<X> col) {
        return fromIterable(col);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.data.collections.extensions.persistent.PQueueX#monoid()
     */
    @Override
    public <T> Reducer<POrderedSet<T>> monoid() {

        return Reducers.toPOrderedSet();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.pcollections.MapPSet#plus(java.lang.Object)
     */
    @Override
    public LazyPOrderedSetX<T> plus(T e) {
        return new LazyPOrderedSetX<T>(
                                       getSet().plus(e), this.collector);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.pcollections.MapPSet#plusAll(java.util.Collection)
     */
    @Override
    public LazyPOrderedSetX<T> plusAll(Collection<? extends T> list) {
        return new LazyPOrderedSetX<T>(
                                       getSet().plusAll(list), this.collector);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.pcollections.POrderedSet#get(int)
     */
    @Override
    public T get(int index) {
        return getSet().get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.pcollections.POrderedSet#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object o) {
        return getSet().indexOf(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#minus(java.lang.Object)
     */
    @Override
    public LazyPOrderedSetX<T> minus(Object e) {
        PCollection<T> res = getSet().minus(e);
        return LazyPOrderedSetX.fromIterable(this.collector, res);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.reactor.collections.extensions.base.
     * AbstractFluentCollectionX#minusAll(java.util.Collection)
     */
    public LazyPOrderedSetX<T> minusAll(Collection<?> list) {
        PCollection<T> res = getSet().minusAll(list);
        return LazyPOrderedSetX.fromIterable(this.collector, res);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.reactor.collections.extensions.base.LazyFluentCollectionX#materialize()
     */
    @Override
    public LazyPOrderedSetX<T> materialize() {
       this.lazy.get();
       return this;
    }

}
