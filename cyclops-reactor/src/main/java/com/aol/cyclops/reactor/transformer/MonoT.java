package com.aol.cyclops.reactor.transformer;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;

import com.aol.cyclops.Matchables;
import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.reactor.Monos;
import com.aol.cyclops.types.Filterable;
import com.aol.cyclops.types.Functor;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.MonadicValue1;
import com.aol.cyclops.types.Unit;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.aol.cyclops.types.stream.ToStream;

import reactor.core.publisher.Mono;

/**
 * Monad Transformer for Reactor Mono types.
 * 
 * It allows users to manipulated Mono instances contained inside other monad types
 * 
 * @author johnmcclean
 *
 * @param <A>
 */
public interface MonoT<A> extends Unit<A>, Publisher<A>, Functor<A>, Filterable<A>, ToStream<A> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Filterable#filter(java.util.function.Predicate)
     */
    MonoT<A> filter(Predicate<? super A> test);

    /**
     * @return An empty MonoT instance
     */
    public <R> MonoT<R> empty();

    /**
     * FlatMap operation
     * 
     * @param f Mapping function
     * @return Mapped and flattened MonoT
     */
    default <B> MonoT<B> bind(Function<? super A, MonoT<? extends B>> f) {
        return of(unwrap().bind(mono -> {
            return f.apply(mono.block())
                    .unwrap()
                    .unwrap();
        }));
    }

    /**
    * Construct an MonoT from an AnyM that wraps a monad containing Monos
    * 
    * <pre>
    * {@code 
    *   MonoT<Integer> monoT = MonoT.of(AnyM.fromOptional(Optional.of(Mono.just(10)));
    * 
    * }
    * </pre>
    * 
    * 
    * @param monads
    *            AnyM that contains a monad wrapping an Mono
    * @return MonoTransformer for manipulating nested Monos
    */
    public static <A> MonoT<A> of(AnyM<Mono<A>> monads) {

        return Matchables.anyM(monads)
                         .visit(v -> MonoTValue.of(v), s -> MonoTSeq.of(s));

    }

    /**
     * @return The wrapped AnyM
     */
    public AnyM<Mono<A>> unwrap();

    /**
     * Peek at the current value of the Mono
     * <pre>
     * {@code 
     *    MonoT.of(AnyM.fromIterable(ListX.of(Mono.just(10)))
     *             .peek(System.out::println);
     *             
     *     //prints 10        
     * }
     * </pre>
     * 
     * @param peek  Consumer to accept current value of CompletableFuture
     * @return CompletableFutureT with peek call
     */
    public MonoT<A> peek(Consumer<? super A> peek);

    /**
     * Map the wrapped Mono
     * 
     * <pre>
     * {@code 
     *   MonoT.of(AnyM.fromIterable(ListX.of(Mono.just(10)))
     *             .map(t->t+1);
     *  
     *  
     * }
     * </pre>
     * 
     * @param f Mapping function for the wrapped Mono
     * @return CompletableFutureT that applies the map function to the wrapped Mono
     */
    @Override
    public <B> MonoT<B> map(Function<? super A, ? extends B> f);

    /**
     * flatMap operation
     * 
     * <pre>
     * {@code 
     *   MonoT.of(AnyM.fromIterable(ListX.of(Mono.just(10)))
     *             .flatMap(t-> Maybe.just(t+1));
     *  
     *  
     * }
     * </pre>
     * 
     * @param f Mapping function
     * @return flatMapped MonoT
     */
    public <B> MonoT<B> flatMap(Function<? super A, ? extends MonadicValue<? extends B>> f);

    /**
     * Lift a function into one that accepts and returns an CompletableFutureT
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * e.g. to add list handling  / iteration (via CompletableFuture) and iteration (via Stream) to an existing function
     * 
     * @param fn Function to enhance with functionality from CompletableFuture and another monad type
     * @return Function that accepts and returns an CompletableFutureT
     */
    public static <U, R> Function<MonoT<U>, MonoT<R>> lift(Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    /**
     * Lift a BiFunction into one that accepts and returns  CompletableFutureTs
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * e.g. to add list handling / iteration (via CompletableFuture), iteration (via Stream)  and asynchronous execution (CompletableFuture) 
     * to an existing function
     * 
     * @param fn BiFunction to enhance with functionality from CompletableFuture and another monad type
     * @return Function that accepts and returns an CompletableFutureT
     */
    public static <U1, U2, R> BiFunction<MonoT<U1>, MonoT<U2>, MonoT<R>> lift2(
            BiFunction<? super U1, ? super U2, ? extends R> fn) {
        return (optTu1, optTu2) -> optTu1.bind(input1 -> optTu2.map(input2 -> fn.apply(input1, input2)));
    }

    /**
     * Construct an MonoT from an AnyM that contains a monad type that contains type other than Mono
     * The values in the underlying monad will be mapped to Mono<A>
     * 
     * @param anyM AnyM that doesn't contain a monad wrapping an Mono
     * @return MonoTransformer for manipulating nested Monos
     */
    public static <A> MonoT<A> fromAnyM(AnyM<A> anyM) {
        return of(anyM.map(Mono::just));
    }

    /**
     * Create a MonoT from an AnyMValue by wrapping the element stored in the AnyMValue in a Mono
     * 
     * @param anyM Monad to embed a Mono inside (wrapping it's current value)
     * @return MonoTransformer for manipulating nested Monos
     */
    public static <A> MonoTValue<A> fromAnyMValue(AnyMValue<A> anyM) {
        return MonoTValue.fromAnyM(anyM);
    }

    /**
     * Create a MonoT from an AnyMSeq by wrapping the elements stored in the AnyMSeq in a Mono
     * 
     * @param anyM  Monad to embed a Mono inside (wrapping it's current values individually in Monos)
     * @return  MonoTransformer for manipulating nested Monos
     */
    public static <A> MonoTSeq<A> fromAnyMSeq(AnyMSeq<A> anyM) {
        return MonoTSeq.fromAnyM(anyM);
    }

    /**
     * Create a MonoTSeq from an Iterable that contains nested Monos
     * <pre>
     * {@code 
     *    MonoTSeq<Integer> monoT = MonoT.fromIterable(Arrays.asList(Mono.just(1));
     * }
     * </pre>
     * @param iterableOfMonos An Iterable containing nested Monos
     * @return  MonoTransformer for manipulating nested Monos
     */
    public static <A> MonoTSeq<A> fromIterable(Iterable<Mono<A>> iterableOfMonos) {
        return MonoTSeq.of(AnyM.fromIterable(iterableOfMonos));
    }

    /**
     * Construct a MonoTSeq from a Stream of Monos
     * 
     * <pre>
     * {@code 
     *    MonoTSeq<Integer> monoT = MonoT.fromStream(Stream.of(Mono.just(1));
     * }
     * </pre> 
     * 
     * @param streamOfMonos Stream containing nested Monos 
     * @return Mono Transformer for manipulating nested Monos
     */
    public static <A> MonoTSeq<A> fromStream(Stream<Mono<A>> streamOfMonos) {
        return MonoTSeq.of(AnyM.fromStream(streamOfMonos));
    }


    /**
     * Construct a MonoTSeq from a Publisher containing Monos
     * 
     * <pre>
     * {@code 
     *    MonoTSeq<Integer> monoT = MonoT.fromPublisher(Flux.just(Mono.just(1));
     * }
     * </pre> 
     * 
     * @param publisherOfMonos Publisher containing nested Monos 
     * @return Mono Transformer for manipulating nested Monos
     */
    public static <A> MonoTSeq<A> fromPublisher(Publisher<Mono<A>> publisherOfMonos) {
        return MonoTSeq.of(AnyM.fromPublisher(publisherOfMonos));
    }

    /**
     * Construct a MonoTValue from a cyclops-react MonadicValue (Maybe, Eval FutureW etc) containing a Mono
     * <pre>
     * {@code 
     *    MonoTValue<Integer> monoT = MonoT.fromValue(Maybe.just(Mono.just(1));
     * }
     * </pre> 
     * @param monadicValue MonadicValue containing a nested Mono
     * @return Mono Transformer for manipulating nested Monos
     */
    public static <A, V extends MonadicValue<Mono<A>>> MonoTValue<A> fromValue(V monadicValue) {
        return MonoTValue.of(AnyM.ofValue(monadicValue));
    }

    /**
     * Construct a MonoTValue from an Optional containing a Mono
     * <pre>
     * {@code 
     *    MonoTValue<Integer> monoT = MonoT.fromOptional(Optional.of(Mono.just(1));
     * }
     * </pre> 
     * @param optional Optional containing a nested Mono
     * @return  Mono Transformer for manipulating nested Monos
     */
    public static <A> MonoTValue<A> fromOptional(Optional<Mono<A>> optional) {
        return MonoTValue.of(AnyM.fromOptional(optional));
    }

    /**
     * Construct a MonoTValue from an CompletableFuture containing a Mono
     * <pre>
     * {@code 
     *    MonoTValue<Integer> monoT = MonoT.fromFuture(CompletableFuture.completedFuture(Mono.just(1));
     * }
     * </pre>  
     * @param future Future containing a nested Mono
     * @return Mono Transformer for manipulating nested Monos
     */
    public static <A> MonoTValue<A> fromFuture(CompletableFuture<Mono<A>> future) {
        return MonoTValue.of(AnyM.fromCompletableFuture(future));
    }
    /**
     * Construct a MonoTValue from a Mono containing a Mono
     * <pre>
     * {@code 
     *    MonoTValue<Integer> monoT = MonoT.fromFuture(Mono.just(Mono.just(1));
     * }
     * </pre>  
     * @param future Mono containing a nested Mono
     * @return Mono Transformer for manipulating nested Monos
     */
    public static <A> MonoTValue<A> fromMono(Mono<Mono<A>> mono) {
        return MonoTValue.of(Monos.anyM(mono));
    }

    /**
     * Construct a MonoTValue from an Iterable containing a Mono
     * <pre>
     * {@code 
     *    MonoTValue<Integer> monoT = MonoT.fromIterableValue(Arrays.asList(Mono.just(1));
     * }
     * </pre> 
     * @param iterableOfMonos An Iterable containing a Mono
     * @return Mono Transformer for manipulating nested Monos
     */
    public static <A> MonoTValue<A> fromIterableValue(Iterable<Mono<A>> iterableOfMonos) {
        return MonoTValue.of(AnyM.fromIterableValue(iterableOfMonos));
    }

    /**
     * @return An empty MonoTValue (wraps an Empty Optional)
     */
    public static <T> MonoTValue<T> emptyOptional() {
        return MonoTValue.of(AnyM.fromOptional(Optional.empty()));
    }

    /**
     * @return An empty MonoTSeq (wraps an Empty List)
     */
    public static <T> MonoTSeq<T> emptyList() {
        return MonoT.fromIterable(ListX.of());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Functor#cast(java.lang.Class)
     */
    @Override
    default <U> MonoT<U> cast(Class<? extends U> type) {
        return (MonoT<U>) Functor.super.cast(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Functor#trampoline(java.util.function.Function)
     */
    @Override
    default <R> MonoT<R> trampoline(Function<? super A, ? extends Trampoline<? extends R>> mapper) {
        return (MonoT<R>) Functor.super.trampoline(mapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Functor#patternMatch(java.util.function.Function,
     * java.util.function.Supplier)
     */
    @Override
    default <R> MonoT<R> patternMatch(Function<CheckValue1<A, R>, CheckValue1<A, R>> case1,
            Supplier<? extends R> otherwise) {
        return (MonoT<R>) Functor.super.patternMatch(case1, otherwise);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Filterable#ofType(java.lang.Class)
     */
    @Override
    default <U> MonoT<U> ofType(Class<? extends U> type) {

        return (MonoT<U>) Filterable.super.ofType(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Filterable#filterNot(java.util.function.Predicate)
     */
    @Override
    default MonoT<A> filterNot(Predicate<? super A> fn) {

        return (MonoT<A>) Filterable.super.filterNot(fn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Filterable#notNull()
     */
    @Override
    default MonoT<A> notNull() {

        return (MonoT<A>) Filterable.super.notNull();
    }

}