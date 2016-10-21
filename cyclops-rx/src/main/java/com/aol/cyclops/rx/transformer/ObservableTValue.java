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
 * Monad Transformer for Observables  nested inside Scalar  data types
 * 
 * ObservableT allows the deeply wrapped Stream to be manipulating within it's nested /contained context
 * @author johnmcclean
 *
 * @param <T> the type of elements held in the nested Observables
 */
public class ObservableTValue<T> implements ObservableT<T> {

    private final AnyMValue<Observable<T>> run;

    private ObservableTValue(final AnyMValue<? extends Observable<T>> run) {
        this.run = (AnyMValue) run;
    }

    /**
     * @return The wrapped AnyM
     */
    @Override
    public AnyMValue<Observable<T>> unwrap() {
        return run;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#
     * isSeqPresent()
     */
    @Override
    public boolean isSeqPresent() {
        return !run.isEmpty();
    }

    /**
     * Peek at the current value of the Stream
     * <pre>
     * {@code 
     *    ObservableT.of(AnyM.fromOptional(Optional.of(Observable.just(10)))
     *             .peek(System.out::println);
     *             
     *     //prints 10        
     * }
     * </pre>
     * 
     * @param peek  Consumer to accept current value of Stream
     * @return ObservableT with peek call
     */
    @Override
    public ObservableTValue<T> peek(final Consumer<? super T> peek) {
        return map(a -> {
            peek.accept(a);
            return a;
        });
    }

    /**
     * Filter the wrapped Stream
     * <pre>
     * {@code 
     *    ObservableT.of(AnyM.fromOptional(Optional.of(Observable.just(10,11)))
     *             .filter(t->t!=10);
     *             
     *     //ObservableT containing -> <AnyM<Optional<Obsverable[11]>>>
     * }
     * </pre>
     * @param test Predicate to filter the wrapped Stream
     * @return ObservableT that applies the provided filter
     */
    @Override
    public ObservableTValue<T> filter(final Predicate<? super T> test) {

        return of(run.map(stream -> stream.filter(i -> test.test(i))));
    }

    /**
     * Map the wrapped Stream
     * 
     * <pre>
     * {@code 
     *  ObservableT.of(AnyM.fromOptional(Optional.of(Observable.just(10)))
     *             .map(t->t=t+1);
     *  
     *  
     *  //ObservableT -> containing <AnyM<Stream<Stream[11]>>>
     * }
     * </pre>
     * 
     * @param f Mapping function for the wrapped Stream
     * @return ObservableT that applies the map function to the wrapped Stream
     */
    @Override
    public <B> ObservableTValue<B> map(final Function<? super T, ? extends B> f) {
        return new ObservableTValue<B>(
                                       run.map(o -> o.map(i -> f.apply(i))));
    }

    /**
     * Flat Map the wrapped Observable
      * <pre>
     * {@code 
     *  ObservableT.of(AnyM.fromOptional(Optional.of(Observable.just(10)))
     *             .flatMapT(t->ObsverableT.emptyObservable());
     *  
     *  
     *  
     * }
     * </pre>
     * @param f FlatMap function
     * @return ObservableT that applies the flatMap function to the wrapped Stream
     */
    public <B> ObservableTValue<B> flatMapT(final Function<? super T, ObservableTValue<? extends B>> f) {
        
        return of(run.map(stream -> stream.flatMap(a -> Observables.observable(f.apply(a).run.stream()))
                                          .<B> flatMap(a -> a)));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.rx.transformer.ObservableT#flatMap(java.util.function.Function)
     */
    @Override
    public <B> ObservableTValue<B> flatMap(final Function<? super T, ? extends Observable<? extends B>> f) {

        return new ObservableTValue<B>(
                                       run.map(o -> o.flatMap(i -> f.apply(i))));

    }

    /**
     * Lift a function into one that accepts and returns an ObservableT
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * 
     * 
     * @param fn Function to enhance with functionality from Stream and another monad type
     * @return Function that accepts and returns an ObservableT
     */
    public static <U, R> Function<ObservableTValue<U>, ObservableTValue<R>> lift(
            final Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    /**
     * Construct an ObservableT from an AnyM that contains a monad type that contains type other than Stream
     * The values in the underlying monad will be mapped to Stream<A>
     * 
     * @param anyM AnyM that doesn't contain a monad wrapping an Stream
     * @return ObservableT
     */
    public static <A> ObservableTValue<A> fromAnyM(final AnyMValue<A> anyM) {
        return of(anyM.map(Observable::just));
    }

    /**
     * Create a ObservableT from an AnyM that wraps a monad containing a Stream
     * 
     * @param obs
     * @return
     */
    public static <A> ObservableTValue<A> of(final AnyMValue<? extends Observable<A>> obs) {
        return new ObservableTValue<>(
                                      obs);
    }

    /**
     * Construct an ObservableTValue backed by an Optional 
     * 
     * @param obs An Observable to wrap in an Optional
     * @return ObservableTValue wrapping an Observable inside an Optional
     */
    public static <A> ObservableTValue<A> of(final Observable<A> obs) {
        return ObservableT.fromOptional(Optional.of(obs));
    }

    /**
     *  Construct an ObservableTValue by wrapping the supplied MonadValue which contains an Observable
     * 
     * @param monadicValue Observable wrapped in a MonadicValue
     * @return ObservableTValue wrapping an Observable inside an MonadicValue
     */
    public static <A, V extends MonadicValue<? extends Observable<A>>> ObservableTValue<A> fromValue(
            final V monadicValue) {
        return of(AnyM.ofValue(monadicValue));
    }

    /**
     * @return True if Observable is present, otherwise false
     */
    public boolean isObservablePresent() {
        return !run.isEmpty();
    }

    /**
     * @return Get nested Observable
     */
    public Observable<T> get() {
        return run.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
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
    public <U> ObservableTValue<U> unitIterator(final Iterator<U> u) {
        return of(run.unit(Observable.from(() -> u)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Unit#unit(java.lang.Object)
     */
    @Override
    public <T> ObservableTValue<T> unit(final T unit) {
        return of(run.unit(Observable.just(unit)));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FoldableTransformerSeq#stream()
     */
    @Override
    public ReactiveSeq<T> stream() {
        return run.map(i -> Observables.reactiveSeq(i))
                  .stream()
                  .flatMap(e -> e);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.rx.transformer.ObservableT#observable()
     */
    @Override
    public Observable<T> observable() {
        return Observables.observable(stream());
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.rx.transformer.ObservableT#empty()
     */
    @Override
    public <R> ObservableTValue<R> empty() {
        return of(run.empty());
    }

    /**
     * @return An ObserbableTValue backed by an Empty Optional
     */
    public static <T> ObservableTValue<T> emptyOptional() {
        return ObservableT.fromOptional(Optional.empty());
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.anyM.NestedFoldable#nestedFoldables()
     */
    @Override
    public AnyM<? extends IterableFoldable<T>> nestedFoldables() {
        return run.map(i -> Observables.reactiveSeq(i));

    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.anyM.NestedCollectable#nestedCollectables()
     */
    @Override
    public AnyM<? extends CyclopsCollectable<T>> nestedCollectables() {
        return run.map(i -> Observables.reactiveSeq(i));

    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#unitAnyM(com.aol.cyclops.control.AnyM)
     */
    @Override
    public <T> ObservableTValue<T> unitAnyM(final AnyM<Traversable<T>> traversable) {

        return of((AnyMValue) traversable.map(t -> Observable.from(t)));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#transformerStream()
     */
    @Override
    public AnyM<? extends Traversable<T>> transformerStream() {
        return run.map(i -> Observables.reactiveSeq(i));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return run.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ObservableTValue) {
            return run.equals(((ObservableTValue) o).run);
        }
        return false;
    }

}