package com.aol.cyclops.reactor.collections.extensions.base;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
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
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.reactivestreams.Publisher;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Reducer;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.StreamUtils;
import com.aol.cyclops.control.Streamable;
import com.aol.cyclops.control.Trampoline;
import com.aol.cyclops.data.collections.extensions.CollectionX;
import com.aol.cyclops.data.collections.extensions.FluentCollectionX;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.data.collections.extensions.standard.SetX;
import com.aol.cyclops.reactor.FluxUtils;
import com.aol.cyclops.reactor.types.ReactorConvertable;
import com.aol.cyclops.types.IterableFunctor;
import com.aol.cyclops.types.Zippable;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class AbstractFluentCollectionX<T> implements LazyFluentCollectionX<T>, ReactorConvertable<T>{
    @AllArgsConstructor
    public static class LazyCollection<T,C extends Collection<T>> implements LazyFluentCollection<T,C>{
        private volatile C list;
        private volatile Flux<T> seq;
        private final Collector<T,?,C> collector;
        public C get(){
            if( seq!=null){
               list =  seq.collect(collector).block();
               seq = null;
            }
              
            return list;
            
        }
        
        public Flux<T> stream(){
            if(seq!=null){
              return seq;
            }
            return Flux.fromIterable(list);
        }
    }
    @AllArgsConstructor
    public static class PersistentLazyCollection<T,C extends Collection<T>>  implements LazyFluentCollection<T,C>{
        private volatile C list;
        private volatile Flux<T> seq;
        private final Reducer<C> reducer;
        public C get(){
            if( seq!=null){
               list =  reducer.mapReduce(seq.toStream());
               seq = null;
            }
              
            return list;
            
        }
        public Flux<T> stream(){
            if(seq!=null)
                return seq;
            return Flux.fromIterable(list);
        }
    }
    abstract public Flux<T> flux();
    abstract public <X> FluentCollectionX<X> stream(Flux<X> stream);
    @Override
    public FluentCollectionX<T> plusLazy(T e){
        Flux f;
      
        return stream(flux().concat(Mono.just(e)));
        
    }
    @Override
    public FluentCollectionX<T> plus(T e){
        add(e);
        return this;
    }
    
    @Override
    public FluentCollectionX<T> plusAll(Collection<? extends T> list){
        addAll(list);
        return this;
    }
    
    @Override
    public FluentCollectionX<T> minus(Object e){
        remove(e);
        return this;
    }
    
    @Override
    public FluentCollectionX<T> minusAll(Collection<?> list){
        removeAll(list);
        return this;
    }
    @Override
    public FluentCollectionX<T> plusAllLazy(Collection<? extends T> list){
        return stream(flux().concatWith(ReactiveSeq.fromIterable(list)));
    }
    
    @Override
    public FluentCollectionX<T> minusLazy(Object e){
        
        return stream(flux().filter(t-> !Objects.equals(t,e)));
        
    }
    @Override
    public FluentCollectionX<T> minusAllLazy(Collection<?> list){
        Supplier<SetX<?>> set = ()->SetX.fromIterable(list);
        return stream(flux().filter(t-> !set.get().contains(t)));
    }
    
    @Override
    public FluentCollectionX<T> combine(BiPredicate<? super T, ? super T> predicate, BinaryOperator<T> op){
        return stream(FluxUtils.combine(flux(), predicate, op)); 
    }
    @Override
    public FluentCollectionX<T> reverse(){
        return stream(FluxUtils.reverse(flux())); 
    }
    @Override
    public FluentCollectionX<T> filter(Predicate<? super T> pred){
        return stream(flux().filter(pred));
    }
    @Override
    public <R> CollectionX<R> map(Function<? super T, ? extends R> mapper){
        return stream(flux().map(mapper));
    }
    @Override
    public <R> CollectionX<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> mapper){
        return stream(flux().flatMap(mapper.andThen(ReactiveSeq::fromIterable)));
    }
    @Override
    public FluentCollectionX<T> limit(long num){
        return stream(flux().take(num));
    }
    @Override
    public FluentCollectionX<T> skip(long num){
        return stream(flux().skip(num));
    }
    @Override
    public FluentCollectionX<T> takeRight(int num){
        return stream(flux().takeLast(num));
    }
    @Override
    public FluentCollectionX<T> dropRight(int num){
        return stream(flux().skipLast(num));
    }
    @Override
    public FluentCollectionX<T> takeWhile(Predicate<? super T> p){
        return stream(flux().takeWhile(p));
    }
    @Override
    public FluentCollectionX<T> dropWhile(Predicate<? super T> p){
        return stream(flux().skipWhile(p));
    }
    @Override
    public FluentCollectionX<T> takeUntil(Predicate<? super T> p){
        return stream(FluxUtils.limitUntil(flux(),p));
    }
    @Override
    public FluentCollectionX<T> dropUntil(Predicate<? super T> p){
        return stream(flux().skipWhile(p.negate()));
    }
     /**
      * Performs a map operation that can call a recursive method without running out of stack space
      * <pre>
      * {@code
      * ReactiveSeq.of(10,20,30,40)
                 .trampoline(i-> fibonacci(i))
                 .forEach(System.out::println); 
                 
        Trampoline<Long> fibonacci(int i){
            return fibonacci(i,1,0);
        }
        Trampoline<Long> fibonacci(int n, long a, long b) {
            return n == 0 ? Trampoline.done(b) : Trampoline.more( ()->fibonacci(n-1, a+b, a));
        }        
                 
      * 55
        6765
        832040
        102334155
      * 
      * 
      * ReactiveSeq.of(10_000,200_000,3_000_000,40_000_000)
                 .trampoline(i-> fibonacci(i))
                 .forEach(System.out::println);
                 
                 
      * completes successfully
      * }
      * 
     * @param mapper
     * @return
     */
    public <R> FluentCollectionX<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper){
        
         return  stream(FluxUtils.trampoline(flux(),mapper));    
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.jooq.lambda.Seq#slice(long, long)
     */
    public FluentCollectionX<T> slice(long from, long to){
        return stream(flux().skip(from).take(to-from));  
    }
    
    

    public FluentCollectionX<ListX<T>> grouped(int groupSize){
        return stream(FluxUtils.grouped(flux(),groupSize));     
    }
    public <K, A, D> FluentCollectionX<Tuple2<K, D>> grouped(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream){
        return stream(FluxUtils.grouped(flux(), classifier, downstream));
    }
    public <K> FluentCollectionX<Tuple2<K, Seq<T>>> grouped(Function<? super T, ? extends K> classifier){
        return stream(FluxUtils.grouped(flux(), classifier));
          
    }
    public <U> FluentCollectionX<Tuple2<T, U>> zip(Iterable<? extends U> other){
        return (FluentCollectionX)stream(flux().zipWithIterable(other, Tuple::tuple));
    }
    public <U, R> FluentCollectionX<R> zip(Iterable<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper){
        return stream(flux().zipWith(ReactiveSeq.fromIterable(other),zipper));
    }
    public FluentCollectionX<ListX<T>> sliding(int windowSize){
        return stream(flux().window(windowSize,1).map(ListX::fromPublisher));   
    }
    public FluentCollectionX<ListX<T>> sliding(int windowSize, int increment){
        return stream(flux().window(windowSize,increment).map(ListX::fromPublisher)); 
    }
    public FluentCollectionX<T> scanLeft(Monoid<T> monoid){
        return stream(flux().scan(monoid.zero(),(BiFunction)monoid.combiner()));   
    }
    public <U> FluentCollectionX<U> scanLeft(U seed, BiFunction<? super U, ? super T,? extends U> function){
        return stream(flux().scan(seed,(BiFunction)function));    
    }
    public FluentCollectionX<T> scanRight(Monoid<T> monoid){
        return stream(FluxUtils.scanRight(flux(),monoid));  
    }
    public <U> FluentCollectionX<U> scanRight(U identity, BiFunction<? super T, ? super U, ? extends U> combiner){
        return stream(FluxUtils.scanRight(flux(),identity,combiner));
    }
    

    /*
     * (non-Javadoc)
     * 
     * @see org.jooq.lambda.Seq#sorted(java.util.function.Function)
     */
    public <U extends Comparable<? super U>> FluentCollectionX<T> sorted(Function<? super T, ? extends U> function){
        return stream(FluxUtils.sorted(flux(),function));
    }
   



    


    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#cycle(int)
     */
    @Override
    public FluentCollectionX<T> cycle(int times) {
        
        return stream(flux().repeat(times));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#cycle(com.aol.cyclops.sequence.Monoid, int)
     */
    @Override
    public FluentCollectionX<T> cycle(Monoid<T> m, int times) {
        
        return stream(Flux.fromStream(StreamUtils.cycle(times, Streamable.of(m.reduce(stream())))));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#cycleWhile(java.util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> cycleWhile(Predicate<? super T> predicate) {
        
        return stream(FluxUtils.cycleWhile(flux(), predicate));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#cycleUntil(java.util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> cycleUntil(Predicate<? super T> predicate) {
        
        return stream(FluxUtils.cycleUntil(flux(), predicate));
    }

   
    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#zip(org.jooq.lambda.Seq)
     */
    @Override
    public <U> FluentCollectionX<Tuple2<T, U>> zip(Seq<? extends U> other) {
        
        return (FluentCollectionX)stream(flux().zipWithIterable(other,Tuple::tuple));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#zip3(java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <S, U> FluentCollectionX<Tuple3<T, S, U>> zip3(Stream<? extends S> second, Stream<? extends U> third) {
        
        return (FluentCollectionX)stream(Flux.zip(flux(),Flux.fromStream(second), Flux.fromStream(third)).map(t->Tuple.tuple(t.getT1(),t.getT2(),t.getT3())));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.data.collections.extensions.standard.ListX#zip4(java.util.stream.Stream, java.util.stream.Stream, java.util.stream.Stream)
     */
    @Override
    public <T2, T3, T4> FluentCollectionX<Tuple4<T, T2, T3, T4>> zip4(Stream<? extends T2> second, Stream<? extends T3> third,
            Stream<? extends T4> fourth) {
       
        return (FluentCollectionX)stream(Flux.zip(flux(),Flux.fromStream(second), Flux.fromStream(third),Flux.fromStream(fourth)).map(t->Tuple.tuple(t.getT1(),t.getT2(),t.getT3(),t.getT4())));

    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#zipWithIndex()
     */
    @Override
    public FluentCollectionX<Tuple2<T, Long>> zipWithIndex() {
        
        return (FluentCollectionX)stream(flux().zipWith(ReactiveSeq.rangeLong(0,Long.MAX_VALUE),Tuple::tuple));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#distinct()
     */
    @Override
    public FluentCollectionX<T> distinct() {
        
        return stream(flux().distinct());
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#sorted()
     */
    @Override
    public FluentCollectionX<T> sorted() {
        
        return stream(FluxUtils.sorted(flux()));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#sorted(java.util.Comparator)
     */
    @Override
    public FluentCollectionX<T> sorted(Comparator<? super T> c) {
        
        return stream(FluxUtils.sorted(flux(),c));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#skipWhile(java.util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> skipWhile(Predicate<? super T> p) {
        
        return stream(flux().skipWhile(p));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#skipUntil(java.util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> skipUntil(Predicate<? super T> p) {
        
        return stream(flux().skipWhile(p.negate()));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#limitWhile(java.util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> limitWhile(Predicate<? super T> p) {
        
        return stream(flux().takeWhile(p));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#limitUntil(java.util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> limitUntil(Predicate<? super T> p) {
        
        return stream(FluxUtils.limitUntil(flux(),p));
    }

    
    

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#intersperse(java.lang.Object)
     */
    @Override
    public FluentCollectionX<T> intersperse(T value) {
        
        return stream(FluxUtils.intersperse(flux(),value));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#shuffle()
     */
    @Override
    public FluentCollectionX<T> shuffle() {
        
        return stream(FluxUtils.shuffle(flux()));
    }

    

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#skipLast(int)
     */
    @Override
    public FluentCollectionX<T> skipLast(int num) {
        
        return stream(flux().skipLast(num));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#limitLast(int)
     */
    @Override
    public FluentCollectionX<T> limitLast(int num) {
    
        return stream(flux().takeLast(num));
    }
   
    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#onEmpty(java.lang.Object)
     */
    @Override
    public FluentCollectionX<T> onEmpty(T value) {
        return stream(FluxUtils.onEmpty(flux(),value));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#onEmptyGet(java.util.function.Supplier)
     */
    @Override
    public FluentCollectionX<T> onEmptyGet(Supplier<? extends T> supplier) {
        return stream(FluxUtils.onEmptyGet(flux(),supplier));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#onEmptyThrow(java.util.function.Supplier)
     */
    @Override
    public <X extends Throwable> FluentCollectionX<T> onEmptyThrow(Supplier<? extends X> supplier) {
        return stream(FluxUtils.onEmptyThrow(flux(),supplier));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Traversable#shuffle(java.util.Random)
     */
    @Override
    public FluentCollectionX<T> shuffle(Random random) {
        return stream(FluxUtils.shuffle(flux(),random));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Filterable#ofType(java.lang.Class)
     */
    @Override
    public <U> FluentCollectionX<U> ofType(Class<? extends U> type) {
        
        return stream(FluxUtils.ofType(flux(), type));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Filterable#filterNot(java.util.function.Predicate)
     */
    @Override
    public FluentCollectionX<T> filterNot(Predicate<? super T> fn) {
        return stream(flux().filter(fn.negate()));
        
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Filterable#notNull()
     */
    @Override
    public FluentCollectionX<T> notNull() {
        return stream(flux().filter(Objects::nonNull));
        
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Filterable#removeAll(java.util.stream.Stream)
     */
    @Override
    public FluentCollectionX<T> removeAll(Stream<? extends T> stream) {
        return stream(FluxUtils.removeAll(flux(),ReactiveSeq.fromStream(stream)));
    }
    @Override
    public FluentCollectionX<T> removeAll(Seq<? extends T> stream) {
        
        return stream(FluxUtils.removeAll(flux(),stream));
       
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Filterable#removeAll(java.lang.Iterable)
     */
    @Override
    public FluentCollectionX<T> removeAll(Iterable<? extends T> it) {
        return stream(FluxUtils.removeAll(flux(),it));
        
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Filterable#removeAll(java.lang.Object[])
     */
    @Override
    public FluentCollectionX<T> removeAll(T... values) {
        return stream(FluxUtils.removeAll(flux(),Arrays.asList(values)));
        
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Filterable#retainAll(java.lang.Iterable)
     */
    @Override
    public FluentCollectionX<T> retainAll(Iterable<? extends T> it) {
        return stream(FluxUtils.retainAll(flux(),it));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Filterable#retainAll(java.util.stream.Stream)
     */
    @Override
    public FluentCollectionX<T> retainAll(Stream<? extends T> stream) {
        return stream(FluxUtils.retainAll(flux(),Seq.seq(stream)));
    }
    @Override
    public FluentCollectionX<T> retainAll(Seq<? extends T> stream) {
        return stream(FluxUtils.retainAll(flux(),stream));
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Filterable#retainAll(java.lang.Object[])
     */
    @Override
    public FluentCollectionX<T> retainAll(T... values) {
        return stream(FluxUtils.retainAll(flux(),Arrays.asList(values)));
    }

    

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Functor#cast(java.lang.Class)
     */
    @Override
    public <U> FluentCollectionX<U> cast(Class<? extends U> type) {
        return stream(flux().map(e->type.cast(e)));
    }

    
    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.Functor#patternMatch(java.lang.Object, java.util.function.Function)
     */
    @Override
    public <R> FluentCollectionX<R> patternMatch(
            Function<CheckValue1<T, R>, CheckValue1<T, R>> case1,Supplier<? extends R> otherwise) {
        
        return stream(FluxUtils.patternMatch(flux(), case1, otherwise));
    }

    

    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.ExtendedTraversable#permutations()
     */
    @Override
    public FluentCollectionX<ReactiveSeq<T>> permutations() {
        return stream(Flux.from(Streamable.fromPublisher(flux()).permutations().map(s->s.stream())));
        
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.ExtendedTraversable#combinations(int)
     */
    @Override
    public FluentCollectionX<ReactiveSeq<T>> combinations(int size) {
        return stream(Flux.from(Streamable.fromPublisher(flux()).combinations(size).map(s->s.stream())));
        
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.lambda.monads.ExtendedTraversable#combinations()
     */
    @Override
    public FluentCollectionX<ReactiveSeq<T>> combinations() {
        return stream(Flux.from(Streamable.fromPublisher(flux()).combinations().map(s->s.stream())));
        
    }

    @Override
    public <C extends Collection<? super T>> FluentCollectionX<C> grouped(int size, Supplier<C> supplier) {
        
        return stream(FluxUtils.grouped(flux(), size, supplier));
    }

    @Override
    public FluentCollectionX<ListX<T>> groupedUntil(Predicate<? super T> predicate) {
        
        return stream(FluxUtils.groupedUntil(flux(), predicate));
    }

    @Override
    public FluentCollectionX<ListX<T>> groupedWhile(Predicate<? super T> predicate) {
        
        return stream(FluxUtils.groupedWhile(flux(), predicate));
        
    }

    @Override
    public <C extends Collection<? super T>> FluentCollectionX<C> groupedWhile(Predicate<? super T> predicate,
            Supplier<C> factory) {
        return stream(FluxUtils.groupedWhile(flux(), predicate,factory));
        
    }

    @Override
    public <C extends Collection<? super T>> FluentCollectionX<C> groupedUntil(Predicate<? super T> predicate,
            Supplier<C> factory) {
        return stream(FluxUtils.groupedWhile(flux(), predicate.negate(),factory));
        
    }
    @Override
    public FluentCollectionX<ListX<T>> groupedStatefullyUntil(BiPredicate<ListX<? super T>, ? super T> predicate) {
        return stream(FluxUtils.groupedStatefullyUntil(flux(), predicate));
    }
  
    
    @Override
    public abstract <T1> FluentCollectionX<T1> from(Collection<T1> c);
    
    
    @Override
    public FluentCollectionX<T> peek(Consumer<? super T> c) {
        return stream(flux().map(e->{
            c.accept(e);
            return e;
            }));
    }
    @Override
    public <U, R> FluentCollectionX<R> zip(Seq<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        return stream(flux().zipWith(ReactiveSeq.fromStream(other),zipper));
    }
    @Override
    public <U, R>  FluentCollectionX<R> zip(Stream<? extends U> other, BiFunction<? super T, ? super U, ? extends R> zipper) {
        return stream(flux().zipWith(ReactiveSeq.fromStream(other),zipper));
    }
    @Override
    public <U> FluentCollectionX<Tuple2<T, U>> zip(Stream<? extends U> other) {
        return zip(ReactiveSeq.fromStream(other));
    }
   
   
    @Override
    public abstract <U> IterableFunctor<U> unitIterator(Iterator<U> U);
    @Override
    public <T2, R> Zippable<R> zip(BiFunction<? super T, ? super T2, ? extends R> fn,
            Publisher<? extends T2> publisher) {
        // TODO Auto-generated method stub
        return LazyFluentCollectionX.super.zip(fn, publisher);
    }
    
   
    
}
