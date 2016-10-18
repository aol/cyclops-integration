package com.aol.cyclops.rx.transformer;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.rx.Observables;
import com.aol.cyclops.types.IterableFoldable;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.Traversable;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.aol.cyclops.types.stream.CyclopsCollectable;

import rx.Observable;

/**
 * Monad Transformer for Cyclops Streams
 * 
 * ObservableT consists of an AnyM instance that in turns wraps anoter Monad type that contains an Stream
 * 
 * ObservableT<AnyM<*SOME_MONAD_TYPE*<Stream<T>>>>
 * 
 * ObservableT allows the deeply wrapped Stream to be manipulating within it's nested /contained context
 * @author johnmcclean
 *
 * @param <T>
 */
public class ObservableTValue<T> implements ObservableT<T> {

    private final AnyMValue<Observable<T>> run;

    private ObservableTValue(final AnyMValue<? extends Observable<T>> run) {
        this.run = (AnyMValue) run;
    }

    /**
     * @return The wrapped AnyM
     */
    public AnyMValue<Observable<T>> unwrap() {
        return run;
    }

    public boolean isSeqPresent() {
        return !run.isEmpty();
    }

    /**
     * Peek at the current value of the Stream
     * <pre>
     * {@code 
     *    ObservableT.of(AnyM.fromStream(Arrays.asStream(10))
     *             .peek(System.out::println);
     *             
     *     //prints 10        
     * }
     * </pre>
     * 
     * @param peek  Consumer to accept current value of Stream
     * @return ObservableT with peek call
     */
    public ObservableTValue<T> peek(Consumer<? super T> peek) {
        return map(a -> {
            peek.accept(a);
            return a;
        });
    }

    /**
     * Filter the wrapped Stream
     * <pre>
     * {@code 
     *    ObservableT.of(AnyM.fromStream(Arrays.asStream(10,11))
     *             .filter(t->t!=10);
     *             
     *     //ObservableT<AnyM<Stream<Stream[11]>>>
     * }
     * </pre>
     * @param test Predicate to filter the wrapped Stream
     * @return ObservableT that applies the provided filter
     */
    public ObservableTValue<T> filter(Predicate<? super T> test) {

        return of(run.map(stream -> stream.filter(i -> test.test(i))));
    }

    /**
     * Map the wrapped Stream
     * 
     * <pre>
     * {@code 
     *  ObservableT.of(AnyM.fromStream(Arrays.asStream(10))
     *             .map(t->t=t+1);
     *  
     *  
     *  //ObservableT<AnyM<Stream<Stream[11]>>>
     * }
     * </pre>
     * 
     * @param f Mapping function for the wrapped Stream
     * @return ObservableT that applies the map function to the wrapped Stream
     */
    public <B> ObservableTValue<B> map(Function<? super T, ? extends B> f) {
        return new ObservableTValue<B>(
                                       run.map(o -> o.map(i -> f.apply(i))));
    }

    /**
     * Flat Map the wrapped Stream
      * <pre>
     * {@code 
     *  ObservableT.of(AnyM.fromStream(Arrays.asStream(10))
     *             .flatMap(t->Stream.empty();
     *  
     *  
     *  //ObservableT<AnyM<Stream<Stream.empty>>>
     * }
     * </pre>
     * @param f FlatMap function
     * @return ObservableT that applies the flatMap function to the wrapped Stream
     */
    public <B> ObservableTValue<B> flatMapT(Function<? super T, ObservableTValue<? extends B>> f) {
        return of(run.map(stream -> stream.flatMap(a -> Observables.observable(f.apply(a).run.stream()))
                                          .<B> flatMap(a -> a)));
    }

    public <B> ObservableTValue<B> flatMap(Function<? super T, ? extends Observable<? extends B>> f) {

        return new ObservableTValue<B>(
                                       run.map(o -> o.flatMap(i -> f.apply(i))));

    }

    /**
     * Lift a function into one that accepts and returns an ObservableT
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * e.g. to add iteration handling (via Stream) and nullhandling (via Optional) to an existing function
     * <pre>
     * {@code 
    	Function<Integer,Integer> add2 = i -> i+2;
    	Function<ObservableT<Integer>, ObservableT<Integer>> optTAdd2 = ObservableT.lift(add2);
    	
    	Stream<Integer> nums = Stream.of(1,2);
    	AnyM<Stream<Integer>> stream = AnyM.fromOptional(Optional.of(nums));
    	
    	List<Integer> results = optTAdd2.apply(ObservableT.of(stream))
    									.unwrap()
    									.<Optional<Stream<Integer>>>unwrap()
    									.get()
    									.collect(Collectors.toList());
    	//Stream.of(3,4);
     * 
     * 
     * }</pre>
     * 
     * 
     * @param fn Function to enhance with functionality from Stream and another monad type
     * @return Function that accepts and returns an ObservableT
     */
    public static <U, R> Function<ObservableTValue<U>, ObservableTValue<R>> lift(Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    /**
     * Construct an ObservableT from an AnyM that contains a monad type that contains type other than Stream
     * The values in the underlying monad will be mapped to Stream<A>
     * 
     * @param anyM AnyM that doesn't contain a monad wrapping an Stream
     * @return ObservableT
     */
    public static <A> ObservableTValue<A> fromAnyM(AnyMValue<A> anyM) {
        return of(anyM.map(Observable::just));
    }

    /**
     * Create a ObservableT from an AnyM that wraps a monad containing a Stream
     * 
     * @param monads
     * @return
     */
    public static <A> ObservableTValue<A> of(AnyMValue<? extends Observable<A>> monads) {
        return new ObservableTValue<>(
                                      monads);
    }

    public static <A> ObservableTValue<A> of(Observable<A> monads) {
        return ObservableT.fromOptional(Optional.of(monads));
    }

    public static <A, V extends MonadicValue<? extends Observable<A>>> ObservableTValue<A> fromValue(V monadicValue) {
        return of(AnyM.ofValue(monadicValue));
    }

    public boolean isStreamPresent() {
        return !run.isEmpty();
    }

    public Observable<T> get() {
        return run.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.format("ObservableTValue[%s]", run);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return stream().iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.IterableFunctor#unitIterator(java.util.Iterator)
     */
    @Override
    public <U> ObservableTValue<U> unitIterator(Iterator<U> u) {
        return of(run.unit(Observable.from(() -> u)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Unit#unit(java.lang.Object)
     */
    @Override
    public <T> ObservableTValue<T> unit(T unit) {
        return of(run.unit(Observable.just(unit)));
    }

    @Override
    public ReactiveSeq<T> stream() {
        return run.map(i -> Observables.reactiveSeq(i))
                  .stream()
                  .flatMap(e -> e);
    }

    @Override
    public Observable<T> observable() {
        return Observables.observable(stream());
    }

    @Override
    public <R> ObservableTValue<R> empty() {
        return of(run.empty());
    }

    public static <T> ObservableTValue<T> emptyOptional() {
        return ObservableT.fromOptional(Optional.empty());
    }

    @Override
    public AnyM<? extends IterableFoldable<T>> nestedFoldables() {
        return run.map(i -> Observables.reactiveSeq(i));

    }

    @Override
    public AnyM<? extends CyclopsCollectable<T>> nestedCollectables() {
        return run.map(i -> Observables.reactiveSeq(i));

    }

    @Override
    public <T> ObservableTValue<T> unitAnyM(AnyM<Traversable<T>> traversable) {

        return of((AnyMValue) traversable.map(t -> Observable.from(t)));
    }

    @Override
    public AnyM<? extends Traversable<T>> transformerStream() {
        return run.map(i -> Observables.reactiveSeq(i));
    }

    @Override
    public int hashCode() {
        return run.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ObservableTValue) {
            return run.equals(((ObservableTValue) o).run);
        }
        return false;
    }

}