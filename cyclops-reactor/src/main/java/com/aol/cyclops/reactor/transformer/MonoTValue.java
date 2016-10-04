package com.aol.cyclops.reactor.transformer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.control.Matchable;
import com.aol.cyclops.control.PublisherUtils;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.control.monads.transformers.values.MaybeTValue;
import com.aol.cyclops.control.monads.transformers.values.TransformerValue;
import com.aol.cyclops.reactor.MonoUtils;
import com.aol.cyclops.types.ConvertableFunctor;
import com.aol.cyclops.types.Filterable;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.Value;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.aol.cyclops.types.applicative.ApplicativeFunctor;

import lombok.val;
import reactor.core.publisher.Mono;


public class MonoTValue<A> implements MonoT<A>, TransformerValue<A>, MonadicValue<A>, Supplier<A>, ConvertableFunctor<A>, Filterable<A>,
        ApplicativeFunctor<A>, Matchable.ValueAndOptionalMatcher<A> {

    private final AnyMValue<Mono<A>> run;

    /**
     * @return The wrapped AnyM
     */
    public AnyMValue<Mono<A>> unwrap() {
        return run;
    }

    private MonoTValue(final AnyMValue<Mono<A>> run) {
        this.run = run;
    }

    public FutureW<A> value() {
        return FutureW.of(run.get().toFuture());
    }

    public boolean isValuePresent() {
        return !run.isEmpty();
    }

    /**
    * Filter the wrapped Maybe
    * 
    * <pre>
    * {@code 
    *    MaybeT.of(AnyM.fromStream(Maybe.of(10))
    *             .filter(t->t!=10);
    *             
    *     //MaybeT<AnyMValue<Stream<Maybe.empty>>>
    * }
    * </pre>
    * 
    * @param test
    *            Predicate to filter the wrapped Maybe
    * @return MaybeT that applies the provided filter
    */
    public MonoTValue<A> filter(Predicate<? super A> test) {       
        AnyMValue<Mono<A>> x = run.map(opt -> opt.filter(test));
        return MonoTValue.of(x);
    }

    /**
     * Peek at the current value of the CompletableFuture
     * <pre>
     * {@code 
     *    CompletableFutureT.of(AnyM.fromStream(Arrays.asCompletableFuture(10))
     *             .peek(System.out::println);
     *             
     *     //prints 10        
     * }
     * </pre>
     * 
     * @param peek  Consumer to accept current value of CompletableFuture
     * @return CompletableFutureT with peek call
     */
    public MonoTValue<A> peek(Consumer<? super A> peek) {

        return of(run.peek(future -> future.map(a -> {
            peek.accept(a);
            return a;
        })));
    }

    /**
     * Map the wrapped CompletableFuture
     * 
     * <pre>
     * {@code 
     *  CompletableFutureT.of(AnyM.fromStream(Arrays.asCompletableFuture(10))
     *             .map(t->t=t+1);
     *  
     *  
     *  //CompletableFutureT<AnyMValue<Stream<CompletableFuture[11]>>>
     * }
     * </pre>
     * 
     * @param f Mapping function for the wrapped CompletableFuture
     * @return CompletableFutureT that applies the map function to the wrapped CompletableFuture
     */
    public <B> MonoTValue<B> map(Function<? super A, ? extends B> f) {
        return new MonoTValue<B>(
                                    run.map(o -> o.map(f)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.applicative.ApplicativeFunctor#ap(com.aol.cyclops.
     * types.Value, java.util.function.BiFunction)
     */
    @Override
    public <T2, R> MonoTValue<R> combine(Value<? extends T2> app, BiFunction<? super A, ? super T2, ? extends R> fn) {
        return new MonoTValue<R>(
                                   run.map(o -> MonoUtils.combine(o,app, fn)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#zip(java.lang.
     * Iterable, java.util.function.BiFunction)
     */
    @Override
    public <T2, R> MonoTValue<R> zip(Iterable<? extends T2> app, BiFunction<? super A, ? super T2, ? extends R> fn) {

        return new MonoTValue<R>(
                                   run.map(o -> MonoUtils.zip(o,app, fn)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.applicative.ApplicativeFunctor#zip(java.util.
     * function.BiFunction, org.reactivestreams.Publisher)
     */
    @Override
    public <T2, R> MonoTValue<R> zip(BiFunction<? super A, ? super T2, ? extends R> fn, Publisher<? extends T2> app) {
        return new MonoTValue<>(
                                   run.map(o ->  MonoUtils.zip(o,fn, app)));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Zippable#zip(org.jooq.lambda.Seq, java.util.function.BiFunction)
     */
    @Override
    public <U, R> MonoTValue<R> zip(Seq<? extends U> other, BiFunction<? super A, ? super U, ? extends R> zipper) {

        return (MonoTValue<R>) TransformerValue.super.zip(other, zipper);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Zippable#zip(java.util.stream.Stream, java.util.function.BiFunction)
     */
    @Override
    public <U, R> MonoTValue<R> zip(Stream<? extends U> other, BiFunction<? super A, ? super U, ? extends R> zipper) {

        return (MonoTValue<R>) TransformerValue.super.zip(other, zipper);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Zippable#zip(java.util.stream.Stream)
     */
    @Override
    public <U> MonoTValue<Tuple2<A, U>> zip(Stream<? extends U> other) {

        return (MonoTValue) TransformerValue.super.zip(other);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Zippable#zip(org.jooq.lambda.Seq)
     */
    @Override
    public <U> MonoTValue<Tuple2<A, U>> zip(Seq<? extends U> other) {

        return (MonoTValue) TransformerValue.super.zip(other);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Zippable#zip(java.lang.Iterable)
     */
    @Override
    public <U> MonoTValue<Tuple2<A, U>> zip(Iterable<? extends U> other) {

        return (MonoTValue) TransformerValue.super.zip(other);
    }

    /**
     * Flat Map the wrapped CompletableFuture
      * <pre>
     * {@code 
     *  CompletableFutureT.of(AnyM.fromStream(Arrays.asCompletableFuture(10))
     *             .flatMap(t->CompletableFuture.completedFuture(20));
     *  
     *  
     *  //CompletableFutureT<AnyMValue<Stream<CompletableFuture[20]>>>
     * }
     * </pre>
     * @param f FlatMap function
     * @return CompletableFutureT that applies the flatMap function to the wrapped CompletableFuture
     */

    public <B> MonoTValue<B> flatMapT(Function<? super A, MonoTValue<B>> f) {
        return of(run.map(future -> Mono.from(future.flatMap(a -> f.apply(a).run.stream()
                                                                      .toList()
                                                                      .get(0)))));
    }

    private static <B> AnyMValue<Mono<B>> narrow(AnyMValue<Mono<? extends B>> run) {
        return (AnyMValue) run;
    }

    public <B> MonoTValue<B> flatMap(Function<? super A, ? extends MonadicValue<? extends B>> f) {

        
        AnyMValue<Mono<? extends B>> mapped = run.map(o -> Mono.from(o.flatMap(f)));
        return of(narrow(mapped));

    }

    /**
     * Lift a function into one that accepts and returns an CompletableFutureT
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * e.g. to add list handling  / iteration (via CompletableFuture) and iteration (via Stream) to an existing function
     * <pre>
     * {@code 
        Function<Integer,Integer> add2 = i -> i+2;
        Function<CompletableFutureT<Integer>, CompletableFutureT<Integer>> optTAdd2 = CompletableFutureT.lift(add2);
        
        Stream<Integer> withNulls = Stream.of(1,2,3);
        AnyMValue<Integer> stream = AnyM.fromStream(withNulls);
        AnyMValue<CompletableFuture<Integer>> streamOpt = stream.map(CompletableFuture::completedFuture);
        List<Integer> results = optTAdd2.apply(CompletableFutureT.of(streamOpt))
                                        .unwrap()
                                        .<Stream<CompletableFuture<Integer>>>unwrap()
                                        .map(CompletableFuture::join)
                                        .collect(Collectors.toList());
        
        
        //CompletableFuture.completedFuture(List[3,4]);
     * 
     * 
     * }</pre>
     * 
     * 
     * @param fn Function to enhance with functionality from CompletableFuture and another monad type
     * @return Function that accepts and returns an CompletableFutureT
     */
    public static <U, R> Function<MonoTValue<U>, MonoTValue<R>> lift(Function<? super U, ? extends R> fn) {
        return optTu -> optTu.map(input -> fn.apply(input));
    }

    /**
     * Lift a BiFunction into one that accepts and returns  CompletableFutureTs
     * This allows multiple monad types to add functionality to existing functions and methods
     * 
     * e.g. to add list handling / iteration (via CompletableFuture), iteration (via Stream)  and asynchronous execution (CompletableFuture) 
     * to an existing function
     * 
     * <pre>
     * {@code 
        BiFunction<Integer,Integer,Integer> add = (a,b) -> a+b;
        BiFunction<CompletableFutureT<Integer>,CompletableFutureT<Integer>,CompletableFutureT<Integer>> optTAdd2 = CompletableFutureT.lift2(add);
        
        Stream<Integer> withNulls = Stream.of(1,2,3);
        AnyMValue<Integer> stream = AnyM.ofMonad(withNulls);
        AnyMValue<CompletableFuture<Integer>> streamOpt = stream.map(CompletableFuture::completedFuture);
        
        CompletableFuture<CompletableFuture<Integer>> two = CompletableFuture.completedFuture(CompletableFuture.completedFuture(2));
        AnyMValue<CompletableFuture<Integer>> future=  AnyM.fromCompletableFuture(two);
        List<Integer> results = optTAdd2.apply(CompletableFutureT.of(streamOpt),CompletableFutureT.of(future))
                                        .unwrap()
                                        .<Stream<CompletableFuture<Integer>>>unwrap()
                                        .map(CompletableFuture::join)
                                        .collect(Collectors.toList());
                                        
            //CompletableFuture.completedFuture(List[3,4,5]);                       
      }
      </pre>
     * @param fn BiFunction to enhance with functionality from CompletableFuture and another monad type
     * @return Function that accepts and returns an CompletableFutureT
     */
    public static <U1, U2, R> BiFunction<MonoTValue<U1>, MonoTValue<U2>, MonoTValue<R>> lift2(
            BiFunction<? super U1, ? super U2, ? extends R> fn) {
        return (optTu1, optTu2) -> optTu1.flatMapT(input1 -> optTu2.map(input2 -> fn.apply(input1, input2)));
    }

    /**
     * Construct an CompletableFutureT from an AnyM that contains a monad type that contains type other than CompletableFuture
     * The values in the underlying monad will be mapped to CompletableFuture<A>
     * 
     * @param anyM AnyM that doesn't contain a monad wrapping an CompletableFuture
     * @return CompletableFutureT
     */
    public static <A> MonoTValue<A> fromAnyM(AnyMValue<A> anyM) {
        return of(anyM.map(Mono::just));
    }

    /**
     * Construct an CompletableFutureT from an AnyM that wraps a monad containing  CompletableFutures
     * 
     * @param monads AnyM that contains a monad wrapping an CompletableFuture
     * @return CompletableFutureT
     */
    public static <A> MonoTValue<A> of(AnyMValue<Mono<A>> monads) {
        return new MonoTValue<>(
                                   monads);
    }

    public static <A> MonoTValue<A> of(Mono<A> monads) {
        return MonoT.fromOptional(Optional.of(monads));
    }

    public static <A, V extends MonadicValue<Mono<A>>> MonoTValue<A> fromValue(V monadicValue) {
        return of(AnyM.ofValue(monadicValue));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return String.format("MonoTValue[%s]", run);
    }

    @Override
    public A get() {
        return run.get()
                  .block();
    }

    @Override
    public ReactiveSeq<A> stream() {
        val maybeEval = run.toMaybe();
        return maybeEval.isPresent() ? PublisherUtils.stream(maybeEval.get())
                                                
                : ReactiveSeq.of();
    }

    @Override
    public Iterator<A> iterator() {
        val maybeEval = run.toMaybe();
        return maybeEval.isPresent() ? PublisherUtils.iterator(maybeEval.get())
                                             
                : Arrays.<A> asList()
                        .iterator();
    }

    @Override
    public void subscribe(Subscriber<? super A> s) {
        run.toMaybe()
           .forEach(e -> e.subscribe(s));

    }

    public boolean isFuturePresent() {
        return !run.isEmpty();

    }

    @Override
    public boolean test(A t) {
        val maybeEval = run.toMaybe();
        return maybeEval.isPresent() ? MonoUtils.test(maybeEval.get(),t)
                : false;

    }

    public <R> MonoTValue<R> unit(R value) {
        return of(run.unit(Mono.just(value)));
    }

    public <R> MonoTValue<R> empty() {
        return of(run.unit(Mono.empty()));
    }

    public static <T> MonoTValue<T> emptyOptional() {
        return MonoT.fromOptional(Optional.empty());
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Functor#cast(java.lang.Class)
     */
    @Override
    public <U> MonoTValue<U> cast(Class<? extends U> type) {
        return (MonoTValue<U>) TransformerValue.super.cast(type);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Functor#trampoline(java.util.function.Function)
     */
    @Override
    public <R> MonoTValue<R> trampoline(Function<? super A, ? extends Trampoline<? extends R>> mapper) {
        return (MonoTValue<R>) TransformerValue.super.trampoline(mapper);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Functor#patternMatch(java.util.function.Function, java.util.function.Supplier)
     */
    @Override
    public <R> MonoTValue<R> patternMatch(Function<CheckValue1<A, R>, CheckValue1<A, R>> case1, Supplier<? extends R> otherwise) {
        return (MonoTValue<R>) TransformerValue.super.patternMatch(case1, otherwise);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Filterable#ofType(java.lang.Class)
     */
    @Override
    public <U> MonoTValue<U> ofType(Class<? extends U> type) {

        return (MonoTValue<U>) MonoT.super.ofType(type);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Filterable#filterNot(java.util.function.Predicate)
     */
    @Override
    public MonoTValue<A> filterNot(Predicate<? super A> fn) {

        return (MonoTValue<A>) MonoT.super.filterNot(fn);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Filterable#notNull()
     */
    @Override
    public MonoTValue<A> notNull() {

        return (MonoTValue<A>) MonoT.super.notNull();
    }

    @Override
    public int hashCode() {
        return run.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MonoTValue) {
            return run.equals(((MonoTValue) o).run);
        }
        return false;
    }
}