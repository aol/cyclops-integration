package com.aol.cyclops.rx.transformer;


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
import com.aol.cyclops.rx.RxCyclops;
import com.aol.cyclops.types.IterableFoldable;
import com.aol.cyclops.types.Traversable;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.stream.CyclopsCollectable;

import rx.Observable;


/**
 * Monad Transformer for Rx Observables
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
public class ObservableTSeq<T> implements ObservableT<T>{
  
   private final AnyMSeq<Observable<T>> run;

   private ObservableTSeq(final AnyMSeq<? extends Observable<T>> run){
       this.run = ( AnyMSeq)(run);
   }
   public boolean isSeqPresent() {
       return !run.isEmpty();
     }
   /**
	 * @return The wrapped AnyM
	 */
   public AnyMSeq<Observable<T>> unwrap(){
	   return run;
   }
   /**
  	 * Peek at the current value of the Stream
  	 * <pre>
  	 * {@code 
  	 *    ObservableT.fromIterable(ListX.of(Stream.of(10))
  	 *             .peek(System.out::println);
  	 *             
  	 *     //prints 10        
  	 * }
  	 * </pre>
  	 * 
  	 * @param peek  Consumer to accept current value of Stream
  	 * @return ObservableT with peek call
  	 */
   public ObservableTSeq<T> peek(Consumer<? super T> peek){
	   return map(a-> {peek.accept(a); return a;});
   }
   /**
 	 * Filter the wrapped Stream
 	 * <pre>
 	 * {@code 
 	 *   ObservableT.fromIterable(ListX.of(Stream.of(10,11))
 	 *          .filter(t->t!=10);
 	 *             
 	 *     //ObservableT<[11]>>
 	 * }
 	 * </pre>
 	 * @param test Predicate to filter the wrapped Stream
 	 * @return ObservableT that applies the provided filter
 	 */
   public ObservableTSeq<T> filter(Predicate<? super T> test){
       return of(run.map(stream-> stream.filter(i->test.test(i))));
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
   public <B> ObservableTSeq<B> map(Function<? super T,? extends B> f){
       return new ObservableTSeq<B>(run.map(o-> o.map(i->f.apply(i))));
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
   public <B> ObservableTSeq<B> flatMapT(Function<? super T,ObservableTSeq<? extends B>> f){
	   return of(run.map(stream-> stream.flatMap(a-> RxCyclops.toObservable(f.apply(a).run.stream()))
			   							.<B>flatMap(a->a)));
   }
   public <B> ObservableTSeq<B> flatMap(Function<? super T, ? extends Observable<? extends B>> f) {

       return new ObservableTSeq<B>(run.map(o -> o.flatMap(i->f.apply(i))));

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
   public static <U, R> Function<ObservableTSeq<U>, ObservableTSeq<R>> lift(Function<? super U,? extends R> fn) {
		return optTu -> optTu.map(input -> fn.apply(input));
	}
   /**
	 * Construct an ObservableT from an AnyM that contains a monad type that contains type other than Stream
	 * The values in the underlying monad will be mapped to Stream<A>
	 * 
	 * @param anyM AnyM that doesn't contain a monad wrapping an Stream
	 * @return ObservableT
	 */
   public static <A> ObservableTSeq<A> fromAnyM(AnyMSeq<A> anyM){
	   return of(anyM.map(Observable::just));
   }
   /**
	 * Create a ObservableT from an AnyM that wraps a monad containing a Stream
	 * 
	 * @param monads
	 * @return
	 */
   public static <A> ObservableTSeq<A> of(AnyMSeq<? extends Observable<A>> monads){
	   return new ObservableTSeq<>(monads);
   }
   public static <A> ObservableTSeq<A> of(Observable<A> monads){
       return ObservableT.fromIterable(ReactiveSeq.of(monads));
   }
   
   
   /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
   public String toString() {
       return String.format("ObservableTSeq[%s]", run );
	}
 
    /* (non-Javadoc)
     * @see com.aol.cyclops.types.Unit#unit(java.lang.Object)
     */
    @Override
    public <T> ObservableTSeq<T> unit(T unit) {
        return of(run.unit(Observable.just(unit)));
    }
  
    @Override
    public ReactiveSeq<T> stream() {
        return run.map(i->RxCyclops.reactiveSeq(i)).stream().flatMap(e->e);
    }

    @Override
    public Observable<T> observable() {
        return RxCyclops.toObservable(stream());
    }

    @Override
    public Iterator<T> iterator() {
       return stream().iterator();
    }


    public <R> ObservableTSeq<R> unitIterator(Iterator<R> it){
        return of(run.unitIterator(it).map(i->Observable.just(i)));
    }
    @Override
    public <R> ObservableT<R> empty() {
       return of(run.empty());
    }
    @Override
    public AnyM<? extends IterableFoldable<T>> nestedFoldables() {
        return run.map(i->RxCyclops.reactiveSeq(i));
       
    }
    @Override
    public AnyM<? extends CyclopsCollectable<T>> nestedCollectables() {
        return run.map(i->RxCyclops.reactiveSeq(i));
       
    }
    @Override
    public <T> ObservableTSeq<T> unitAnyM(AnyM<Traversable<T>> traversable) {
        
        return of((AnyMSeq)traversable.map(t->Observable.from(t)));
    }
    @Override
    public AnyMSeq<? extends Traversable<T>> transformerStream() {
        return run.map(i->RxCyclops.reactiveSeq(i));
    }
    public static <T> ObservableTSeq<T> emptyStream(){
        return ObservableT.fromIterable(ReactiveSeq.empty());
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#combine(java.util.function.BiPredicate, java.util.function.BinaryOperator)
     */
    @Override
    public ObservableTSeq<T> combine(BiPredicate<? super T, ? super T> predicate, BinaryOperator<T> op) {
       
        return (ObservableTSeq<T>)ObservableT.super.combine(predicate, op);
    }
    
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#cycle(int)
     */
    @Override
    public ObservableTSeq<T> cycle(int times) {
       
        return (ObservableTSeq<T>)ObservableT.super.cycle(times);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#cycle(com.aol.cyclops.Monoid, int)
     */
    @Override
    public ObservableTSeq<T> cycle(Monoid<T> m, int times) {
       
        return (ObservableTSeq<T>)ObservableT.super.cycle(m, times);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#cycleWhile(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> cycleWhile(Predicate<? super T> predicate) {
       
        return (ObservableTSeq<T>)ObservableT.super.cycleWhile(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#cycleUntil(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> cycleUntil(Predicate<? super T> predicate) {
       
        return (ObservableTSeq<T>)ObservableT.super.cycleUntil(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#zip(java.lang.Iterable, java.util.function.BiFunction)
     */
    @Override
    public <U, R> ObservableTSeq<R> zip(Iterable<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
       
        return (ObservableTSeq<R>)ObservableT.super.zip(other, zipper);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#zipStream(java.util.stream.Stream)
     */
    @Override
    public <U> ObservableTSeq<Tuple2<T, U>> zip(Stream<? extends U> other) {
       
        return (ObservableTSeq)ObservableT.super.zip(other);
    }
   
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#zip3(java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <S, U> ObservableTSeq<Tuple3<T, S, U>> zip3(Stream<? extends S> second, Stream<? extends U> third) {
       
        return (ObservableTSeq)ObservableT.super.zip3(second, third);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#zip4(java.util.stream.Stream, java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <T2, T3, T4> ObservableTSeq<Tuple4<T, T2, T3, T4>> zip4(Stream<? extends T2> second, Stream<? extends T3> third,
            Stream<? extends T4> fourth) {
       
        return (ObservableTSeq)ObservableT.super.zip4(second, third, fourth);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#zipWithIndex()
     */
    @Override
    public ObservableTSeq<Tuple2<T, Long>> zipWithIndex() {
       
        return (ObservableTSeq<Tuple2<T, Long>>)ObservableT.super.zipWithIndex();
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#sliding(int)
     */
    @Override
    public ObservableTSeq<ListX<T>> sliding(int windowSize) {
       
        return (ObservableTSeq<ListX<T>>)ObservableT.super.sliding(windowSize);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#sliding(int, int)
     */
    @Override
    public ObservableTSeq<ListX<T>> sliding(int windowSize, int increment) {
       
        return (ObservableTSeq<ListX<T>>)ObservableT.super.sliding(windowSize, increment);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#grouped(int, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> ObservableTSeq<C> grouped(int size, Supplier<C> supplier) {
       
        return (ObservableTSeq<C> )ObservableT.super.grouped(size, supplier);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#groupedUntil(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<ListX<T>> groupedUntil(Predicate<? super T> predicate) {
       
        return (ObservableTSeq<ListX<T>>)ObservableT.super.groupedUntil(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#groupedStatefullyWhile(java.util.function.BiPredicate)
     */
    @Override
    public ObservableTSeq<ListX<T>> groupedStatefullyWhile(BiPredicate<ListX<? super T>, ? super T> predicate) {
       
        return (ObservableTSeq<ListX<T>>)ObservableT.super.groupedStatefullyWhile(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#groupedWhile(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<ListX<T>> groupedWhile(Predicate<? super T> predicate) {
       
        return (ObservableTSeq<ListX<T>>)ObservableT.super.groupedWhile(predicate);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#groupedWhile(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> ObservableTSeq<C> groupedWhile(Predicate<? super T> predicate,
            Supplier<C> factory) {
       
        return (ObservableTSeq<C>)ObservableT.super.groupedWhile(predicate, factory);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#groupedUntil(java.util.function.Predicate, java.util.function.Supplier)
     */
    @Override
    public <C extends Collection<? super T>> ObservableTSeq<C> groupedUntil(Predicate<? super T> predicate,
            Supplier<C> factory) {
       
        return (ObservableTSeq<C>)ObservableT.super.groupedUntil(predicate, factory);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#grouped(int)
     */
    @Override
    public ObservableTSeq<ListX<T>> grouped(int groupSize) {
       
        return ( ObservableTSeq<ListX<T>>)ObservableT.super.grouped(groupSize);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#grouped(java.util.function.Function, java.util.stream.Collector)
     */
    @Override
    public <K, A, D> ObservableTSeq<Tuple2<K, D>> grouped(Function<? super T, ? extends K> classifier,
            Collector<? super T, A, D> downstream) {
       
        return (ObservableTSeq)ObservableT.super.grouped(classifier, downstream);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#grouped(java.util.function.Function)
     */
    @Override
    public <K> ObservableTSeq<Tuple2<K, Seq<T>>> grouped(Function<? super T, ? extends K> classifier) {
       
        return (ObservableTSeq)ObservableT.super.grouped(classifier);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#distinct()
     */
    @Override
    public ObservableTSeq<T> distinct() {
       
        return (ObservableTSeq<T>)ObservableT.super.distinct();
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#scanLeft(com.aol.cyclops.Monoid)
     */
    @Override
    public ObservableTSeq<T> scanLeft(Monoid<T> monoid) {
       
        return (ObservableTSeq<T>)ObservableT.super.scanLeft(monoid);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#scanLeft(java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> ObservableTSeq<U> scanLeft(U seed, BiFunction<? super U, ? super T, ? extends U> function) {
       
        return (ObservableTSeq<U>)ObservableT.super.scanLeft(seed, function);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#scanRight(com.aol.cyclops.Monoid)
     */
    @Override
    public ObservableTSeq<T> scanRight(Monoid<T> monoid) {
       
        return (ObservableTSeq<T>)ObservableT.super.scanRight(monoid);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#scanRight(java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public <U> ObservableTSeq<U> scanRight(U identity, BiFunction<? super T, ? super U,? extends U> combiner) {
       
        return (ObservableTSeq<U>)ObservableT.super.scanRight(identity, combiner);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#sorted()
     */
    @Override
    public ObservableTSeq<T> sorted() {
       
        return (ObservableTSeq<T>)ObservableT.super.sorted();
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#sorted(java.util.Comparator)
     */
    @Override
    public ObservableTSeq<T> sorted(Comparator<? super T> c) {
       
        return (ObservableTSeq<T>)ObservableT.super.sorted(c);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#takeWhile(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> takeWhile(Predicate<? super T> p) {
       
        return (ObservableTSeq<T>)ObservableT.super.takeWhile(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#dropWhile(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> dropWhile(Predicate<? super T> p) {
       
        return (ObservableTSeq<T>)ObservableT.super.dropWhile(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#takeUntil(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> takeUntil(Predicate<? super T> p) {
       
        return (ObservableTSeq<T>)ObservableT.super.takeUntil(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#dropUntil(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> dropUntil(Predicate<? super T> p) {
       
        return (ObservableTSeq<T>)ObservableT.super.dropUntil(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#dropRight(int)
     */
    @Override
    public ObservableTSeq<T> dropRight(int num) {
       
        return (ObservableTSeq<T>)ObservableT.super.dropRight(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#takeRight(int)
     */
    @Override
    public ObservableTSeq<T> takeRight(int num) {
       
        return (ObservableTSeq<T>)ObservableT.super.takeRight(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#skip(long)
     */
    @Override
    public ObservableTSeq<T> skip(long num) {
       
        return (ObservableTSeq<T>)ObservableT.super.skip(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#skipWhile(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> skipWhile(Predicate<? super T> p) {
       
        return (ObservableTSeq<T>)ObservableT.super.skipWhile(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#skipUntil(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> skipUntil(Predicate<? super T> p) {
       
        return (ObservableTSeq<T>)ObservableT.super.skipUntil(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#limit(long)
     */
    @Override
    public ObservableTSeq<T> limit(long num) {
       
        return (ObservableTSeq<T>)ObservableT.super.limit(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#limitWhile(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> limitWhile(Predicate<? super T> p) {
       
        return (ObservableTSeq<T>)ObservableT.super.limitWhile(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#limitUntil(java.util.function.Predicate)
     */
    @Override
    public ObservableTSeq<T> limitUntil(Predicate<? super T> p) {
       
        return (ObservableTSeq<T>)ObservableT.super.limitUntil(p);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#intersperse(java.lang.Object)
     */
    @Override
    public ObservableTSeq<T> intersperse(T value) {
       
        return (ObservableTSeq<T>)ObservableT.super.intersperse(value);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#reverse()
     */
    @Override
    public ObservableTSeq<T> reverse() {
       
        return (ObservableTSeq<T>)ObservableT.super.reverse();
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#shuffle()
     */
    @Override
    public ObservableTSeq<T> shuffle() {
       
        return (ObservableTSeq<T>)ObservableT.super.shuffle();
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#skipLast(int)
     */
    @Override
    public ObservableTSeq<T> skipLast(int num) {
       
        return (ObservableTSeq<T>)ObservableT.super.skipLast(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#limitLast(int)
     */
    @Override
    public ObservableTSeq<T> limitLast(int num) {
       
        return (ObservableTSeq<T>)ObservableT.super.limitLast(num);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#onEmpty(java.lang.Object)
     */
    @Override
    public ObservableTSeq<T> onEmpty(T value) {
       
        return (ObservableTSeq<T>)ObservableT.super.onEmpty(value);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#onEmptyGet(java.util.function.Supplier)
     */
    @Override
    public ObservableTSeq<T> onEmptyGet(Supplier<? extends T> supplier) {
       
        return (ObservableTSeq<T>)ObservableT.super.onEmptyGet(supplier);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#onEmptyThrow(java.util.function.Supplier)
     */
    @Override
    public <X extends Throwable> ObservableTSeq<T> onEmptyThrow(Supplier<? extends X> supplier) {
       
        return (ObservableTSeq<T>)ObservableT.super.onEmptyThrow(supplier);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#shuffle(java.util.Random)
     */
    @Override
    public ObservableTSeq<T> shuffle(Random random) {
       
        return (ObservableTSeq<T>)ObservableT.super.shuffle(random);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#slice(long, long)
     */
    @Override
    public ObservableTSeq<T> slice(long from, long to) {
       
        return (ObservableTSeq<T>)ObservableT.super.slice(from, to);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.control.monads.transformers.values.ObservableT#sorted(java.util.function.Function)
     */
    @Override
    public <U extends Comparable<? super U>> ObservableTSeq<T> sorted(Function<? super T, ? extends U> function) {
        return (ObservableTSeq)ObservableT.super.sorted(function);
    }
    @Override
    public int hashCode(){
        return run.hashCode();
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof ObservableTSeq){
            return run.equals( ((ObservableTSeq)o).run);
        }
        return false;
    }
}