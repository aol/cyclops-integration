package com.aol.cyclops.reactor.transformer;


import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.reactivestreams.Publisher;

import com.aol.cyclops.Matchables;
import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.control.monads.transformers.values.FoldableTransformerSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.reactor.Reactor;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.anyM.AnyMValue;

import reactor.core.publisher.Flux;



/**
 * Monad Transformer for RxJava Fluxs
 * 
 * FluxT consists of an AnyM instance that in turns wraps anoter Monad type that contains an Flux
 * 
 * FluxT<AnyM<*SOME_MONAD_TYPE*<Flux<T>>>>
 * 
 * FluxT allows the deeply wrapped Flux to be manipulating within it's nested /contained context
 * @author johnmcclean
 *
 * @param <T>
 */
public interface FluxT<T> extends  FoldableTransformerSeq<T>{
  
    public <R> FluxT<R> unitIterator(Iterator<R> it);
    public <R> FluxT<R> unit(R t);
    public <R> FluxT<R> empty();
   
   public <B> FluxT<B> flatMap(Function<? super T, ? extends Flux<? extends B>> f);
   
   default Flux<Flux<T>> fluxOfFlux(){
	   return Flux.from(this.unwrap().stream());
   }
   /**
	 * @return The wrapped AnyM
	 */
   public AnyM<Flux<T>> unwrap();
   /**
  	 * Peek at the current value of the Flux
  	 * <pre>
  	 * {@code 
  	 *    FluxT.of(AnyM.fromFlux(Arrays.asFlux(10))
  	 *             .peek(System.out::println);
  	 *             
  	 *     //prints 10        
  	 * }
  	 * </pre>
  	 * 
  	 * @param peek  Consumer to accept current value of Flux
  	 * @return FluxT with peek call
  	 */
   public FluxT<T> peek(Consumer<? super T> peek);
   /**
 	 * Filter the wrapped Flux
 	 * <pre>
 	 * {@code 
 	 *    FluxT.of(AnyM.fromFlux(Arrays.asFlux(10,11))
 	 *             .filter(t->t!=10);
 	 *             
 	 *     //FluxT<AnyM<Flux<Flux[11]>>>
 	 * }
 	 * </pre>
 	 * @param test Predicate to filter the wrapped Flux
 	 * @return FluxT that applies the provided filter
 	 */
   public FluxT<T> filter(Predicate<? super T> test);
   /**
	 * Map the wrapped Flux
	 * 
	 * <pre>
	 * {@code 
	 *  FluxT.of(AnyM.fromFlux(Arrays.asFlux(10))
	 *             .map(t->t=t+1);
	 *  
	 *  
	 *  //FluxT<AnyM<Flux<Flux[11]>>>
	 * }
	 * </pre>
	 * 
	 * @param f Mapping function for the wrapped Flux
	 * @return FluxT that applies the map function to the wrapped Flux
	 */
   public <B> FluxT<B> map(Function<? super T,? extends B> f);
   /**
	 * Flat Map the wrapped Flux
	  * <pre>
	 * {@code 
	 *  FluxT.of(AnyM.fromFlux(Flux.just(10))
	 *             .flatMap(t->Flux.empty());
	 *  
	 *  
	 *  //FluxT<AnyM<Flux<Flux.empty>>>
	 * }
	 * </pre>
	 * @param f FlatMap function
	 * @return FluxT that applies the flatMap function to the wrapped Flux
	 */
   default <B> FluxT<B> bind(Function<? super T,FluxT<? extends B>> f){
	   return of(unwrap().map(Flux-> Flux.flatMap(a-> Flux.from(f.apply(a).unwrap().stream()))
					.<B>flatMap(a->a)));
   }
   /**
 	 * Lift a function into one that accepts and returns an FluxT
 	 * This allows multiple monad types to add functionality to existing functions and methods
 	 * 
 	 * e.g. to add iteration handling (via Flux) and nullhandling (via Optional) to an existing function
 	 * <pre>
 	 * {@code 
 		Function<Integer,Integer> add2 = i -> i+2;
		Function<FluxT<Integer>, FluxT<Integer>> optTAdd2 = FluxT.lift(add2);
		
		Flux<Integer> nums = Flux.of(1,2);
		AnyM<Flux<Integer>> Flux = AnyM.fromOptional(Optional.of(nums));
		
		List<Integer> results = optTAdd2.apply(FluxT.of(Flux))
										.unwrap()
										.<Optional<Flux<Integer>>>unwrap()
										.get()
										.collect(Collectors.toList());
 		//Flux.of(3,4);
 	 * 
 	 * 
 	 * }</pre>
 	 * 
 	 * 
 	 * @param fn Function to enhance with functionality from Flux and another monad type
 	 * @return Function that accepts and returns an FluxT
 	 */
   public static <U, R> Function<FluxT<U>, FluxT<R>> lift(Function<? super U,? extends R> fn) {
		return optTu -> optTu.map(input -> fn.apply(input));
	}
   /**
	 * Construct an FluxT from an AnyM that contains a monad type that contains type other than Flux
	 * The values in the underlying monad will be mapped to Flux<A>
	 * 
	 * @param anyM AnyM that doesn't contain a monad wrapping an Flux
	 * @return FluxT
	 */
   public static <A> FluxT<A> fromAnyM(AnyM<A> anyM){
	   return of(anyM.map(Flux::just));
   }
   /**
	 * Create a FluxT from an AnyM that wraps a monad containing a Flux
	 * 
	 * @param monads
	 * @return
	 */
   public static <A> FluxT<A> of(AnyM<? extends Flux<A>> monads){
       return Matchables.anyM(monads).visit(v-> FluxTValue.of(v), s->FluxTSeq.of(s));
   }
   

   public static <A> FluxTValue<A> fromAnyMValue(AnyMValue<A> anyM) {
       return FluxTValue.fromAnyM(anyM);
   }

   public static <A> FluxTSeq<A> fromAnyMSeq(AnyMSeq<A> anyM) {
       return FluxTSeq.fromAnyM(anyM);
   }

   public static <A> FluxTSeq<A> fromIterable(
           Iterable<Flux<A>> iterableOfFluxs) {
       return FluxTSeq.of(AnyM.fromIterable(iterableOfFluxs));
   }

   public static <A> FluxTSeq<A> fromFlux(Flux<Flux<A>> FluxOfFluxs) {
       return FluxTSeq.of(Reactor.flux(FluxOfFluxs));
   }

   public static <A> FluxTSeq<A> fromPublisher(
           Publisher<Flux<A>> publisherOfFluxs) {
       return FluxTSeq.of(AnyM.fromPublisher(publisherOfFluxs));
   }

   public static <A, V extends MonadicValue<? extends Flux<A>>> FluxTValue<A> fromValue(
           V monadicValue) {
       return FluxTValue.fromValue(monadicValue);
   }

   public static <A> FluxTValue<A> fromOptional(Optional<Flux<A>> optional) {
       return FluxTValue.of(AnyM.fromOptional(optional));
   }

   public static <A> FluxTValue<A> fromFuture(CompletableFuture<Flux<A>> future) {
       return FluxTValue.of(AnyM.fromCompletableFuture(future));
   }

   public static <A> FluxTValue<A> fromIterableValue(
           Iterable<Flux<A>> iterableOfFluxs) {
       return FluxTValue.of(AnyM.fromIterableValue(iterableOfFluxs));
   }
   public static<T>  FluxTSeq<T> emptyFlux() {
       return FluxT.fromIterable(ReactiveSeq.empty());
   }
   
   public Flux<T> Flux();
   /* (non-Javadoc)
  * @see com.aol.cyclops.types.Functor#cast(java.lang.Class)
  */
 @Override
 default <U> FluxT<U> cast(Class<? extends U> type) {
     return (FluxT<U>)FoldableTransformerSeq.super.cast(type);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.types.Functor#trampoline(java.util.function.Function)
  */
 @Override
 default <R> FluxT<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
     return (FluxT<R>)FoldableTransformerSeq.super.trampoline(mapper);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.types.Functor#patternMatch(java.util.function.Function, java.util.function.Supplier)
  */
 @Override
 default <R> FluxT<R> patternMatch(Function<CheckValue1<T, R>, CheckValue1<T, R>> case1,
         Supplier<? extends R> otherwise) {
    return (FluxT<R>)FoldableTransformerSeq.super.patternMatch(case1, otherwise);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.types.Filterable#ofType(java.lang.Class)
  */
 @Override
 default <U> FluxT<U> ofType(Class<? extends U> type) {
     
     return (FluxT<U>)FoldableTransformerSeq.super.ofType(type);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.types.Filterable#filterNot(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> filterNot(Predicate<? super T> fn) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.filterNot(fn);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.types.Filterable#notNull()
  */
 @Override
 default FluxT<T> notNull() {
    
     return (FluxT<T>)FoldableTransformerSeq.super.notNull();
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#combine(java.util.function.BiPredicate, java.util.function.BinaryOperator)
  */
 @Override
 default FluxT<T> combine(BiPredicate<? super T, ? super T> predicate, BinaryOperator<T> op) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.combine(predicate, op);
 }
 
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#cycle(int)
  */
 @Override
 default FluxT<T> cycle(int times) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.cycle(times);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#cycle(com.aol.cyclops.Monoid, int)
  */
 @Override
 default FluxT<T> cycle(Monoid<T> m, int times) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.cycle(m, times);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#cycleWhile(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> cycleWhile(Predicate<? super T> predicate) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.cycleWhile(predicate);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#cycleUntil(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> cycleUntil(Predicate<? super T> predicate) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.cycleUntil(predicate);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#zip(java.lang.Iterable, java.util.function.BiFunction)
  */
 @Override
 default <U, R> FluxT<R> zip(Iterable<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
    
     return (FluxT<R>)FoldableTransformerSeq.super.zip(other, zipper);
 }
 @Override
 default <U, R> FluxT<R> zip(Seq<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
    
     return (FluxT<R>)FoldableTransformerSeq.super.zip(other, zipper);
 }
 @Override
 default <U, R> FluxT<R> zip(Stream<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
    
     return (FluxT<R>)FoldableTransformerSeq.super.zip(other, zipper);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#zip(java.util.Flux.Flux)
  */
 @Override
 default <U> FluxT<Tuple2<T, U>> zip(Stream<? extends U> other) {
    
     return (FluxT)FoldableTransformerSeq.super.zip(other);
 }
 @Override
 default <U> FluxT<Tuple2<T, U>> zip(Seq<? extends U> other) {
    
     return (FluxT)FoldableTransformerSeq.super.zip(other);
 }
 @Override
 default <U> FluxT<Tuple2<T, U>> zip(Iterable<? extends U> other) {
    
     return (FluxT)FoldableTransformerSeq.super.zip(other);
 }

 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#zip3(java.util.Flux.Flux, java.util.Flux.Flux)
  */
 @Override
 default <S, U> FluxT<Tuple3<T, S, U>> zip3(Stream<? extends S> second, Stream<? extends U> third) {
    
     return (FluxT)FoldableTransformerSeq.super.zip3(second, third);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#zip4(java.util.Flux.Flux, java.util.Flux.Flux, java.util.Flux.Flux)
  */
 @Override
 default <T2, T3, T4> FluxT<Tuple4<T, T2, T3, T4>> zip4(Stream<? extends T2> second, Stream<? extends T3> third,
        Stream<? extends T4> fourth) {
    
     return (FluxT)FoldableTransformerSeq.super.zip4(second, third, fourth);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#zipWithIndex()
  */
 @Override
 default FluxT<Tuple2<T, Long>> zipWithIndex() {
    
     return (FluxT<Tuple2<T, Long>>)FoldableTransformerSeq.super.zipWithIndex();
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sliding(int)
  */
 @Override
 default FluxT<ListX<T>> sliding(int windowSize) {
    
     return (FluxT<ListX<T>>)FoldableTransformerSeq.super.sliding(windowSize);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sliding(int, int)
  */
 @Override
 default FluxT<ListX<T>> sliding(int windowSize, int increment) {
    
     return (FluxT<ListX<T>>)FoldableTransformerSeq.super.sliding(windowSize, increment);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#grouped(int, java.util.function.Supplier)
  */
 @Override
 default <C extends Collection<? super T>> FluxT<C> grouped(int size, Supplier<C> supplier) {
    
     return (FluxT<C> )FoldableTransformerSeq.super.grouped(size, supplier);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#groupedUntil(java.util.function.Predicate)
  */
 @Override
 default FluxT<ListX<T>> groupedUntil(Predicate<? super T> predicate) {
    
     return (FluxT<ListX<T>>)FoldableTransformerSeq.super.groupedUntil(predicate);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#groupedStatefullyWhile(java.util.function.BiPredicate)
  */
 @Override
 default FluxT<ListX<T>> groupedStatefullyWhile(BiPredicate<ListX<? super T>, ? super T> predicate) {
    
     return (FluxT<ListX<T>>)FoldableTransformerSeq.super.groupedStatefullyWhile(predicate);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#groupedWhile(java.util.function.Predicate)
  */
 @Override
 default FluxT<ListX<T>> groupedWhile(Predicate<? super T> predicate) {
    
     return (FluxT<ListX<T>>)FoldableTransformerSeq.super.groupedWhile(predicate);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#groupedWhile(java.util.function.Predicate, java.util.function.Supplier)
  */
 @Override
 default <C extends Collection<? super T>> FluxT<C> groupedWhile(Predicate<? super T> predicate,
         Supplier<C> factory) {
    
     return (FluxT<C>)FoldableTransformerSeq.super.groupedWhile(predicate, factory);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#groupedUntil(java.util.function.Predicate, java.util.function.Supplier)
  */
 @Override
 default <C extends Collection<? super T>> FluxT<C> groupedUntil(Predicate<? super T> predicate,
         Supplier<C> factory) {
    
     return (FluxT<C>)FoldableTransformerSeq.super.groupedUntil(predicate, factory);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#grouped(int)
  */
 @Override
 default FluxT<ListX<T>> grouped(int groupSize) {
    
     return ( FluxT<ListX<T>>)FoldableTransformerSeq.super.grouped(groupSize);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#grouped(java.util.function.Function, java.util.Flux.Collector)
  */
 @Override
 default <K, A, D> FluxT<Tuple2<K, D>> grouped(Function<? super T, ? extends K> classifier,
         Collector<? super T, A, D> downFlux) {
    
     return (FluxT)FoldableTransformerSeq.super.grouped(classifier, downFlux);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#grouped(java.util.function.Function)
  */
 @Override
 default <K> FluxT<Tuple2<K, Seq<T>>> grouped(Function<? super T, ? extends K> classifier) {
    
     return (FluxT)FoldableTransformerSeq.super.grouped(classifier);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#distinct()
  */
 @Override
 default FluxT<T> distinct() {
    
     return (FluxT<T>)FoldableTransformerSeq.super.distinct();
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#scanLeft(com.aol.cyclops.Monoid)
  */
 @Override
 default FluxT<T> scanLeft(Monoid<T> monoid) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.scanLeft(monoid);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#scanLeft(java.lang.Object, java.util.function.BiFunction)
  */
 @Override
 default <U> FluxT<U> scanLeft(U seed, BiFunction<? super U, ? super T, ? extends U> function) {
    
     return (FluxT<U>)FoldableTransformerSeq.super.scanLeft(seed, function);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#scanRight(com.aol.cyclops.Monoid)
  */
 @Override
 default FluxT<T> scanRight(Monoid<T> monoid) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.scanRight(monoid);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#scanRight(java.lang.Object, java.util.function.BiFunction)
  */
 @Override
 default <U> FluxT<U> scanRight(U identity, BiFunction<? super T, ? super U,? extends U> combiner) {
    
     return (FluxT<U>)FoldableTransformerSeq.super.scanRight(identity, combiner);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sorted()
  */
 @Override
 default FluxT<T> sorted() {
    
     return (FluxT<T>)FoldableTransformerSeq.super.sorted();
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sorted(java.util.Comparator)
  */
 @Override
 default FluxT<T> sorted(Comparator<? super T> c) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.sorted(c);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#takeWhile(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> takeWhile(Predicate<? super T> p) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.takeWhile(p);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#dropWhile(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> dropWhile(Predicate<? super T> p) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.dropWhile(p);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#takeUntil(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> takeUntil(Predicate<? super T> p) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.takeUntil(p);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#dropUntil(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> dropUntil(Predicate<? super T> p) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.dropUntil(p);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#dropRight(int)
  */
 @Override
 default FluxT<T> dropRight(int num) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.dropRight(num);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#takeRight(int)
  */
 @Override
 default FluxT<T> takeRight(int num) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.takeRight(num);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#skip(long)
  */
 @Override
 default FluxT<T> skip(long num) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.skip(num);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#skipWhile(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> skipWhile(Predicate<? super T> p) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.skipWhile(p);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#skipUntil(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> skipUntil(Predicate<? super T> p) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.skipUntil(p);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#limit(long)
  */
 @Override
 default FluxT<T> limit(long num) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.limit(num);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#limitWhile(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> limitWhile(Predicate<? super T> p) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.limitWhile(p);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#limitUntil(java.util.function.Predicate)
  */
 @Override
 default FluxT<T> limitUntil(Predicate<? super T> p) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.limitUntil(p);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#intersperse(java.lang.Object)
  */
 @Override
 default FluxT<T> intersperse(T value) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.intersperse(value);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#reverse()
  */
 @Override
 default FluxT<T> reverse() {
    
     return (FluxT<T>)FoldableTransformerSeq.super.reverse();
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#shuffle()
  */
 @Override
 default FluxT<T> shuffle() {
    
     return (FluxT<T>)FoldableTransformerSeq.super.shuffle();
 }

 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#skipLast(int)
  */
 @Override
 default FluxT<T> skipLast(int num) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.skipLast(num);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#limitLast(int)
  */
 @Override
 default FluxT<T> limitLast(int num) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.limitLast(num);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#onEmpty(java.lang.Object)
  */
 @Override
 default FluxT<T> onEmpty(T value) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.onEmpty(value);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#onEmptyGet(java.util.function.Supplier)
  */
 @Override
 default FluxT<T> onEmptyGet(Supplier<? extends T> supplier) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.onEmptyGet(supplier);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#onEmptyThrow(java.util.function.Supplier)
  */
 @Override
 default <X extends Throwable> FluxT<T> onEmptyThrow(Supplier<? extends X> supplier) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.onEmptyThrow(supplier);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#shuffle(java.util.Random)
  */
 @Override
 default FluxT<T> shuffle(Random random) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.shuffle(random);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#slice(long, long)
  */
 @Override
 default FluxT<T> slice(long from, long to) {
    
     return (FluxT<T>)FoldableTransformerSeq.super.slice(from, to);
 }
 /* (non-Javadoc)
  * @see com.aol.cyclops.control.monads.transformers.values.TransformerSeq#sorted(java.util.function.Function)
  */
 @Override
 default <U extends Comparable<? super U>> FluxT<T> sorted(Function<? super T, ? extends U> function) {
     return (FluxT)FoldableTransformerSeq.super.sorted(function);
 }
}