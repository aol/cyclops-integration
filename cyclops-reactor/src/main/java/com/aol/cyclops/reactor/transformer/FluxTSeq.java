package com.aol.cyclops.reactor.transformer;


import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
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

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.types.IterableFoldable;
import com.aol.cyclops.types.Traversable;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.stream.CyclopsCollectable;

import reactor.core.publisher.Flux;



/**
 * Monad Transformer for Rx Fluxs
 * 
 * FluxT consists of an AnyM instance that in turns wraps anoter Monad type that contains an Stream
 * 
 * FluxT<AnyM<*SOME_MONAD_TYPE*<Stream<T>>>>
 * 
 * FluxT allows the deeply wrapped Stream to be manipulating within it's nested /contained context
 * @author johnmcclean
 *
 * @param <T>
 */
public class FluxTSeq<T> implements FluxT<T>{
  
   private final AnyMSeq<Flux<T>> run;

   private FluxTSeq(final AnyMSeq<? extends Flux<T>> run){
       this.run = ( AnyMSeq)(run);
   }
   public boolean isSeqPresent() {
       return !run.isEmpty();
     }
   /**
	 * @return The wrapped AnyM
	 */
   public AnyMSeq<Flux<T>> unwrap(){
	   return run;
   }
   /**
  	 * Peek at the current value of the Stream
  	 * <pre>
  	 * {@code 
  	 *    FluxT.fromIterable(ListX.of(Stream.of(10))
  	 *             .peek(System.out::println);
  	 *             
  	 *     //prints 10        
  	 * }
  	 * </pre>
  	 * 
  	 * @param peek  Consumer to accept current value of Stream
  	 * @return FluxT with peek call
  	 */
   public FluxTSeq<T> peek(Consumer<? super T> peek){
	   return map(a-> {peek.accept(a); return a;});
   }
   /**
 	 * Filter the wrapped Stream
 	 * <pre>
 	 * {@code 
 	 *   FluxT.fromIterable(ListX.of(Stream.of(10,11))
 	 *          .filter(t->t!=10);
 	 *             
 	 *     //FluxT<[11]>>
 	 * }
 	 * </pre>
 	 * @param test Predicate to filter the wrapped Stream
 	 * @return FluxT that applies the provided filter
 	 */
   public FluxTSeq<T> filter(Predicate<? super T> test){
       return of(run.map(stream-> stream.filter(i->test.test(i))));
   }
   /**
	 * Map the wrapped Stream
	 * 
	 * <pre>
	 * {@code 
	 *  FluxT.of(AnyM.fromStream(Arrays.asStream(10))
	 *             .map(t->t=t+1);
	 *  
	 *  
	 *  //FluxT<AnyM<Stream<Stream[11]>>>
	 * }
	 * </pre>
	 * 
	 * @param f Mapping function for the wrapped Stream
	 * @return FluxT that applies the map function to the wrapped Stream
	 */
   public <B> FluxTSeq<B> map(Function<? super T,? extends B> f){
       return new FluxTSeq<B>(run.map(o-> o.map(i->f.apply(i))));
   }
   /**
	 * Flat Map the wrapped Stream
	  * <pre>
	 * {@code 
	 *  FluxT.of(AnyM.fromStream(Arrays.asStream(10))
	 *             .flatMap(t->Stream.empty();
	 *  
	 *  
	 *  //FluxT<AnyM<Stream<Stream.empty>>>
	 * }
	 * </pre>
	 * @param f FlatMap function
	 * @return FluxT that applies the flatMap function to the wrapped Stream
	 */
   public <B> FluxTSeq<B> flatMapT(Function<? super T,FluxTSeq<? extends B>> f){
	   return of(run.map(stream-> stream.flatMap(a-> Flux.from(f.apply(a).run.stream()))
			   							.<B>flatMap(a->a)));
   }
   public <B> FluxTSeq<B> flatMap(Function<? super T, ? extends Flux<? extends B>> f) {

       return new FluxTSeq<B>(run.map(o -> o.flatMap(f)));

   }
   /**
 	 * Lift a function into one that accepts and returns an FluxT
 	 * This allows multiple monad types to add functionality to existing functions and methods
 	 * 
 	 * e.g. to add iteration handling (via Stream) and nullhandling (via Optional) to an existing function
 	 * <pre>
 	 * {@code 
 		Function<Integer,Integer> add2 = i -> i+2;
		Function<FluxT<Integer>, FluxT<Integer>> optTAdd2 = FluxT.lift(add2);
		
		Stream<Integer> nums = Stream.of(1,2);
		AnyM<Stream<Integer>> stream = AnyM.fromOptional(Optional.of(nums));
		
		List<Integer> results = optTAdd2.apply(FluxT.of(stream))
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
 	 * @return Function that accepts and returns an FluxT
 	 */
   public static <U, R> Function<FluxTSeq<U>, FluxTSeq<R>> lift(Function<? super U,? extends R> fn) {
		return optTu -> optTu.map(input -> fn.apply(input));
	}
   /**
	 * Construct an FluxT from an AnyM that contains a monad type that contains type other than Stream
	 * The values in the underlying monad will be mapped to Stream<A>
	 * 
	 * @param anyM AnyM that doesn't contain a monad wrapping an Stream
	 * @return FluxT
	 */
   public static <A> FluxTSeq<A> fromAnyM(AnyMSeq<A> anyM){
	   return of(anyM.map(Flux::just));
   }
   /**
	 * Create a FluxT from an AnyM that wraps a monad containing a Stream
	 * 
	 * @param monads
	 * @return
	 */
   public static <A> FluxTSeq<A> of(AnyMSeq<? extends Flux<A>> monads){
	   return new FluxTSeq<>(monads);
   }
   public static <A> FluxTSeq<A> of(Flux<A> monads){
       return FluxT.fromIterable(ReactiveSeq.of(monads));
   }
   
   
   /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
   public String toString() {
       return String.format("FluxTSeq[%s]", run );
	}
 
    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Unit#unit(java.lang.Object)
     */
    @Override
    public <T> FluxTSeq<T> unit(T unit) {
        return of(run.unit(Flux.just(unit)));
    }
  
    @Override
    public ReactiveSeq<T> stream() {
        return run.map(i->ReactiveSeq.fromPublisher(i)).stream().flatMap(e->e);
    }

    @Override
    public Flux<T> Flux() {
        return Flux.from(stream());
    }

    @Override
    public Iterator<T> iterator() {
       return stream().iterator();
    }


    public <R> FluxTSeq<R> unitIterator(Iterator<R> it){
        return of(run.unitIterator(it).map(i->Flux.just(i)));
    }
    @Override
    public <R> FluxT<R> empty() {
       return of(run.empty());
    }
    @Override
    public AnyM<? extends IterableFoldable<T>> nestedFoldables() {
        return run.map(i->ReactiveSeq.fromPublisher(i));
       
    }
    @Override
    public AnyM<? extends CyclopsCollectable<T>> nestedCollectables() {
        return run.map(i->ReactiveSeq.fromPublisher(i));
       
    }
   
    @Override
    public <T> FluxTSeq<T> unitAnyM(AnyM<Traversable<T>> traversable) {
        
        return of((AnyMSeq)traversable.map(t->Flux.fromIterable(t)));
    }
    @Override
    public AnyMSeq<? extends Traversable<T>> transformerStream() {
        return run.map(i->ReactiveSeq.fromPublisher(i));
    }
    public static <T> FluxTSeq<T> emptyStream(){
        return FluxT.fromIterable(ReactiveSeq.empty());
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#combine(java.util.function.BiPredicate, java.util.function.BinaryOperator)
     */
    @Override
    public FluxTSeq<T> combine(BiPredicate<? super T, ? super T> predicate, BinaryOperator<T> op) {
       
        return (FluxTSeq<T>)FluxT.super.combine(predicate, op);
    }
    
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#cycle(int)
     */
    @Override
    public FluxTSeq<T> cycle(int times) {
       
        return (FluxTSeq<T>)FluxT.super.cycle(times);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#cycle(com.aol.cyclops.Monoid, int)
     */
    @Override
    public FluxTSeq<T> cycle(Monoid<T> m, int times) {
       
        return (FluxTSeq<T>)FluxT.super.cycle(m, times);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#cycleWhile(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> cycleWhile(Predicate<? super T> predicate) {
       
        return (FluxTSeq<T>)FluxT.super.cycleWhile(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#cycleUntil(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> cycleUntil(Predicate<? super T> predicate) {
       
        return (FluxTSeq<T>)FluxT.super.cycleUntil(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#zip(java.lang.Iterable, java.util.function.BiFunction)
     */
    @Override
    public <U, R> FluxTSeq<R> zip(Iterable<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
       
        return (FluxTSeq<R>)FluxT.super.zip(other, zipper);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#zipStream(java.util.stream.Stream)
     */
    @Override
    public <U> FluxTSeq<Tuple2<T, U>> zip(Stream<? extends U> other) {
       
        return (FluxTSeq)FluxT.super.zip(other);
    }
   
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#zip3(java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <S, U> FluxTSeq<Tuple3<T, S, U>> zip3(Stream<? extends S> second, Stream<? extends U> third) {
       
        return (FluxTSeq)FluxT.super.zip3(second, third);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#zip4(java.util.stream.Stream, java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <T2, T3, T4> FluxTSeq<Tuple4<T, T2, T3, T4>> zip4(Stream<? extends T2> second, Stream<? extends T3> third,
            Stream<? extends T4> fourth) {
       
        return (FluxTSeq)FluxT.super.zip4(second, third, fourth);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#zipWithIndex()
     */
    @Override
    public FluxTSeq<Tuple2<T, Long>> zipWithIndex() {
       
        return (FluxTSeq<Tuple2<T, Long>>)FluxT.super.zipWithIndex();
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#sliding(int)
     */
    @Override
    public FluxTSeq<ListX<T>> sliding(int windowSize) {
       
        return (FluxTSeq<ListX<T>>)FluxT.super.sliding(windowSize);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#sliding(int, int)
     */
    @Override
    public FluxTSeq<ListX<T>> sliding(int windowSize, int increment) {
       
        return (FluxTSeq<ListX<T>>)FluxT.super.sliding(windowSize, increment);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#grouped(int, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> FluxTSeq<C> grouped(int size, Supplier<C> supplier) {
       
        return (FluxTSeq<C> )FluxT.super.grouped(size, supplier);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#groupedUntil(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<ListX<T>> groupedUntil(Predicate<? super T> predicate) {
       
        return (FluxTSeq<ListX<T>>)FluxT.super.groupedUntil(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#groupedStatefullyWhile(java.util.function.BiPredicate)
     */
    @Override
    public FluxTSeq<ListX<T>> groupedStatefullyWhile(BiPredicate<ListX<? super T>, ? super T> predicate) {
       
        return (FluxTSeq<ListX<T>>)FluxT.super.groupedStatefullyWhile(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#groupedWhile(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<ListX<T>> groupedWhile(Predicate<? super T> predicate) {
       
        return (FluxTSeq<ListX<T>>)FluxT.super.groupedWhile(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#groupedWhile(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> FluxTSeq<C> groupedWhile(Predicate<? super T> predicate,
            Supplier<C> factory) {
       
        return (FluxTSeq<C>)FluxT.super.groupedWhile(predicate, factory);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#groupedUntil(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> FluxTSeq<C> groupedUntil(Predicate<? super T> predicate,
            Supplier<C> factory) {
       
        return (FluxTSeq<C>)FluxT.super.groupedUntil(predicate, factory);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#grouped(int)
     */
    @Override
    public FluxTSeq<ListX<T>> grouped(int groupSize) {
       
        return ( FluxTSeq<ListX<T>>)FluxT.super.grouped(groupSize);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#grouped(java.util.function.Function, java.util.stream.Collector)
     */
    @Override
    public <K, A, D> FluxTSeq<Tuple2<K, D>> grouped(Function<? super T, ? extends K> classifier,
            Collector<? super T, A, D> downstream) {
       
        return (FluxTSeq)FluxT.super.grouped(classifier, downstream);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#grouped(java.util.function.Function)
     */
    @Override
    public <K> FluxTSeq<Tuple2<K, Seq<T>>> grouped(Function<? super T, ? extends K> classifier) {
       
        return (FluxTSeq)FluxT.super.grouped(classifier);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#distinct()
     */
    @Override
    public FluxTSeq<T> distinct() {
       
        return (FluxTSeq<T>)FluxT.super.distinct();
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#scanLeft(com.aol.cyclops.Monoid)
     */
    @Override
    public FluxTSeq<T> scanLeft(Monoid<T> monoid) {
       
        return (FluxTSeq<T>)FluxT.super.scanLeft(monoid);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#scanLeft(java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> FluxTSeq<U> scanLeft(U seed, BiFunction<? super U, ? super T, ? extends U> function) {
       
        return (FluxTSeq<U>)FluxT.super.scanLeft(seed, function);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#scanRight(com.aol.cyclops.Monoid)
     */
    @Override
    public FluxTSeq<T> scanRight(Monoid<T> monoid) {
       
        return (FluxTSeq<T>)FluxT.super.scanRight(monoid);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#scanRight(java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> FluxTSeq<U> scanRight(U identity, BiFunction<? super T, ? super U,? extends U> combiner) {
       
        return (FluxTSeq<U>)FluxT.super.scanRight(identity, combiner);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#sorted()
     */
    @Override
    public FluxTSeq<T> sorted() {
       
        return (FluxTSeq<T>)FluxT.super.sorted();
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#sorted(java.util.Comparator)
     */
    @Override
    public FluxTSeq<T> sorted(Comparator<? super T> c) {
       
        return (FluxTSeq<T>)FluxT.super.sorted(c);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#takeWhile(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> takeWhile(Predicate<? super T> p) {
       
        return (FluxTSeq<T>)FluxT.super.takeWhile(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#dropWhile(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> dropWhile(Predicate<? super T> p) {
       
        return (FluxTSeq<T>)FluxT.super.dropWhile(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#takeUntil(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> takeUntil(Predicate<? super T> p) {
       
        return (FluxTSeq<T>)FluxT.super.takeUntil(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#dropUntil(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> dropUntil(Predicate<? super T> p) {
       
        return (FluxTSeq<T>)FluxT.super.dropUntil(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#dropRight(int)
     */
    @Override
    public FluxTSeq<T> dropRight(int num) {
       
        return (FluxTSeq<T>)FluxT.super.dropRight(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#takeRight(int)
     */
    @Override
    public FluxTSeq<T> takeRight(int num) {
       
        return (FluxTSeq<T>)FluxT.super.takeRight(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#skip(long)
     */
    @Override
    public FluxTSeq<T> skip(long num) {
       
        return (FluxTSeq<T>)FluxT.super.skip(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#skipWhile(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> skipWhile(Predicate<? super T> p) {
       
        return (FluxTSeq<T>)FluxT.super.skipWhile(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#skipUntil(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> skipUntil(Predicate<? super T> p) {
       
        return (FluxTSeq<T>)FluxT.super.skipUntil(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#limit(long)
     */
    @Override
    public FluxTSeq<T> limit(long num) {
       
        return (FluxTSeq<T>)FluxT.super.limit(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#limitWhile(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> limitWhile(Predicate<? super T> p) {
       
        return (FluxTSeq<T>)FluxT.super.limitWhile(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#limitUntil(java.util.function.Predicate)
     */
    @Override
    public FluxTSeq<T> limitUntil(Predicate<? super T> p) {
       
        return (FluxTSeq<T>)FluxT.super.limitUntil(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#intersperse(java.lang.Object)
     */
    @Override
    public FluxTSeq<T> intersperse(T value) {
       
        return (FluxTSeq<T>)FluxT.super.intersperse(value);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#reverse()
     */
    @Override
    public FluxTSeq<T> reverse() {
       
        return (FluxTSeq<T>)FluxT.super.reverse();
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#shuffle()
     */
    @Override
    public FluxTSeq<T> shuffle() {
       
        return (FluxTSeq<T>)FluxT.super.shuffle();
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#skipLast(int)
     */
    @Override
    public FluxTSeq<T> skipLast(int num) {
       
        return (FluxTSeq<T>)FluxT.super.skipLast(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#limitLast(int)
     */
    @Override
    public FluxTSeq<T> limitLast(int num) {
       
        return (FluxTSeq<T>)FluxT.super.limitLast(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#onEmpty(java.lang.Object)
     */
    @Override
    public FluxTSeq<T> onEmpty(T value) {
       
        return (FluxTSeq<T>)FluxT.super.onEmpty(value);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#onEmptyGet(java.util.function.Supplier)
     */
    @Override
    public FluxTSeq<T> onEmptyGet(Supplier<? extends T> supplier) {
       
        return (FluxTSeq<T>)FluxT.super.onEmptyGet(supplier);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#onEmptyThrow(java.util.function.Supplier)
     */
    @Override
    public <X extends Throwable> FluxTSeq<T> onEmptyThrow(Supplier<? extends X> supplier) {
       
        return (FluxTSeq<T>)FluxT.super.onEmptyThrow(supplier);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#shuffle(java.util.Random)
     */
    @Override
    public FluxTSeq<T> shuffle(Random random) {
       
        return (FluxTSeq<T>)FluxT.super.shuffle(random);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#slice(long, long)
     */
    @Override
    public FluxTSeq<T> slice(long from, long to) {
       
        return (FluxTSeq<T>)FluxT.super.slice(from, to);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.FluxT#sorted(java.util.function.Function)
     */
    @Override
    public <U extends Comparable<? super U>> FluxTSeq<T> sorted(Function<? super T, ? extends U> function) {
        return (FluxTSeq)FluxT.super.sorted(function);
    }
    @Override
    public int hashCode(){
        return run.hashCode();
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof FluxTSeq){
            return run.equals( ((FluxTSeq)o).run);
        }
        return false;
    }
}