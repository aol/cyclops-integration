package com.aol.cyclops.reactor.transformer;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.types.IterableFoldable;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.Traversable;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.aol.cyclops.types.stream.CyclopsCollectable;

import reactor.core.publisher.Flux;

/**
 * Monad Transformer for Reactor Fluxs nested inside Scalar  data types
 * 
 * @author johnmcclean
 *
 * @param <T> the type of elements held in the nested Fluxes
 */
public class FluxTValue<T> implements FluxT<T> {

    private final AnyMValue<Flux<T>> run;

    private FluxTValue(final AnyMValue<? extends Flux<T>> in) {
        this.run = (AnyMValue) in;
    }

    /**
     * @return The wrapped AnyM
     */
    public AnyMValue<Flux<T>> unwrap() {
        return run;
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#isSeqPresent()
     */
    @Override
    public boolean isSeqPresent() {
        return !run.isEmpty();
    }

    /**
     * Peek at the current value of the Flux
     * <pre>
     * {@code 
     *    FluxT.fromOptional(Optional.of(Flux.just(10))
     *         .peek(System.out::println);
     *             
     *     //prints 10        
     * }
     * </pre>
     * 
     * @param peek  Consumer to accept current value of Flux
     * @return FluxT with peek call
     */
    public FluxTValue<T> peek(Consumer<? super T> peek) {
        return map(a -> {
            peek.accept(a);
            return a;
        });
    }

    /**
     * Filter the wrapped Flux
     * <pre>
     * {@code 
     *    FluxT.fromOptional(Optional.of(Flux.just(10,11))
     *             .filter(t->t!=10);
     *             
     * }
     * </pre>
     * @param test Predicate to filter the wrapped Flux
     * @return FluxT that applies the provided filter
     */
    public FluxTValue<T> filter(Predicate<? super T> test) {

        return of(run.map(stream -> stream.filter(i -> test.test(i))));
    }

    /**
     * Map the wrapped Flux
     * 
     * <pre>
     * {@code 
     *  FluxT.fromOptional(Optional.of(Flux.just(10))
     *             .map(t->t=t+1);
     *  
     *  
     * }
     * </pre>
     * 
     * @param f Mapping function for the wrapped Flux
     * @return FluxT that applies the map function to the wrapped Stream
     */
    public <B> FluxTValue<B> map(Function<? super T, ? extends B> f) {
        return new FluxTValue<B>(
                                 run.map(o -> o.map(f)));
    }

    /**
     * Flat Map the wrapped Flux
      * <pre>
     * {@code 
     *  FluxT.fromOptional(Optional.of(Flux.just(10))
     *       .flatMap(t->FluxT.fromOptional(Optional.of(Flux.just(t+10)));
     *  
     *  
     * }
     * </pre>
     * @param f FlatMap function
     * @return FluxT that applies the flatMap function to the wrapped Flux
     */
    public <B> FluxTValue<B> flatMapT(Function<? super T, FluxTValue<? extends B>> f) {
        return of(run.map(stream -> stream.flatMap(a -> Flux.from(f.apply(a).run.stream()))
                                          .<B> flatMap(a -> a)));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.reactor.transformer.FluxT#flatMap(java.util.function.Function)
     */
    public <B> FluxTValue<B> flatMap(Function<? super T, ? extends Flux<? extends B>> f) {

        return new FluxTValue<B>(
                                 run.map(o -> o.flatMap(f)));

    }

    /**
     * Lift a function into one that accepts and returns an FluxT
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * e.g. to add iteration handling (via Stream) and nullhandling (via Optional) to an existing function
  
     * 
     * 
     * @param fn Function to enhance with functionality from Stream and another monad type
     * @return Function that accepts and returns an FluxT
     */
    public static <U, R> Function<FluxTValue<U>, FluxTValue<R>> lift(Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    /**
     * Construct an FluxT from an AnyM that contains a monad type that contains type other than Stream
     * The values in the underlying monad will be mapped to Stream<A>
     * 
     * @param anyM AnyM that doesn't contain a monad wrapping an Stream
     * @return FluxT
     */
    public static <A> FluxTValue<A> fromAnyM(AnyMValue<A> anyM) {
        return of(anyM.map(Flux::just));
    }

    /**
     * Create a FluxT from an AnyM that wraps a monad containing a Stream
     * 
     * @param monads
     * @return
     */
    public static <A> FluxTValue<A> of(AnyMValue<? extends Flux<A>> monads) {
        return new FluxTValue<>(
                                monads);
    }
    

    /**
     * @return True if Flux is present
     */
    public boolean isStreamPresent() {
        return !run.isEmpty();
    }

    /**
     * @return Get wrapped Flux
     */
    public Flux<T> get() {
        return run.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.format("FluxTValue[%s]", run);
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
    public <U> FluxTValue<U> unitIterator(Iterator<U> u) {
        return of(run.unit(Flux.fromIterable(() -> u)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Unit#unit(java.lang.Object)
     */
    @Override
    public <T> FluxTValue<T> unit(T unit) {
        return of(run.unit(Flux.just(unit)));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FoldableTransformerSeq#stream()
     */
    @Override
    public ReactiveSeq<T> stream() {
        return run.map(i -> ReactiveSeq.fromPublisher(i))
                  .stream()
                  .flatMap(e -> e);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.reactor.transformer.FluxT#flux()
     */
    @Override
    public Flux<T> flux() {
        return Flux.from(stream());
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.reactor.transformer.FluxT#empty()
     */
    @Override
    public <R> FluxTValue<R> empty() {
        return of(run.empty());
    }

    
    /* (non-Javadoc)
     * @see com.aol.cyclops.types.anyM.NestedFoldable#nestedFoldables()
     */
    @Override
    public AnyM<? extends IterableFoldable<T>> nestedFoldables() {
        return run.map(i -> ReactiveSeq.fromPublisher(i));

    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.anyM.NestedCollectable#nestedCollectables()
     */
    @Override
    public AnyM<? extends CyclopsCollectable<T>> nestedCollectables() {
        return run.map(i -> ReactiveSeq.fromPublisher(i));

    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#unitAnyM(com.aol.cyclops.control.AnyM)
     */
    @Override
    public <T> FluxTValue<T> unitAnyM(AnyM<Traversable<T>> traversable) {

        return of((AnyMValue) traversable.map(t -> Flux.fromIterable(t)));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#transformerStream()
     */
    @Override
    public AnyM<? extends Traversable<T>> transformerStream() {
        return run.map(i -> ReactiveSeq.fromPublisher(i));
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
    public boolean equals(Object o) {
        if (o instanceof FluxTValue) {
            return run.equals(((FluxTValue) o).run);
        }
        return false;
    }

}