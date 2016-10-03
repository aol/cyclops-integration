package com.aol.cyclops.control;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.reactor.operators.GroupBySize;
import com.aol.cyclops.reactor.operators.GroupedWhile;
import com.aol.cyclops.types.stream.reactive.SeqSubscriber;

import reactor.core.publisher.Flux;

public class FluxUtils {
    public final static <T, C extends Collection<? super T>> Flux<C> grouped(final Flux<T> stream, final int groupSize,
            final Supplier<C> factory) {
        return new GroupBySize<T, C>(
                                             stream, factory).grouped(groupSize);

    }
    
    public static <T> Iterator<T> iterator(Flux<T> stream){
        SeqSubscriber<T> sub = SeqSubscriber.subscriber();
        Iterator<T> it = stream.subscribeWith(sub).iterator();
        return it;
    }
    
    public static <T> Flux<T> combine(final Flux<T> stream, final BiPredicate<? super T, ? super T> predicate, final BinaryOperator<T> op) {
        final Iterator<T> it = iterator(stream);
        final Object UNSET = new Object();
        return Flux.fromIterable(()->new Iterator<ReactiveSeq<T>>() {
            T current = (T) UNSET;

            @Override
            public boolean hasNext() {
                return it.hasNext() || current != UNSET;
            }

            @Override
            public ReactiveSeq<T> next() {
                while (it.hasNext()) {
                    final T next = it.next();

                    if (current == UNSET) {
                        current = next;

                    } else if (predicate.test(current, next)) {
                        current = op.apply(current, next);

                    } else {
                        final T result = current;
                        current = (T) UNSET;
                        return ReactiveSeq.of(result, next);
                    }
                }
                if (it.hasNext())
                    return ReactiveSeq.empty();
                final T result = current;
                current = (T) UNSET;
                return ReactiveSeq.of(result);
            }

        }).flatMap(Function.identity());
    }
    /**
     * Repeat in a Flux while specified predicate holds
     * <pre>
     * {@code 
     *  int count =0;
     *  
        assertThat(FluxUtils.cycleWhile(Flux.just(1,2,2)
                                            ,next -> count++<6 )
                                            .collect(Collectors.toList()),equalTo(Arrays.asList(1,2,2,1,2,2)));
     * }
     * </pre>
     * @param predicate
     *            repeat while true
     * @return Repeating Stream
     */
    public final static <T> Flux<T> cycleWhile(final Flux<T> stream, final Predicate<? super T> predicate) {
       
        return stream.repeat().takeWhile(predicate);
    }

    /**
     * Repeat in a Stream until specified predicate holds
     * 
     * <pre>
     * {@code 
     *  count =0;
        assertThat(FluxUtils.cycleUntil(Flux.just(1,2,2,3)
                                            ,next -> count++>10 )
                                            .collect(Collectors.toList()),equalTo(Arrays.asList(1, 2, 2, 3, 1, 2, 2, 3, 1, 2, 2)));
    
     * }
     * </pre>
     * @param predicate
     *            repeat while true
     * @return Repeating Stream
     */
    public final static <T> Flux<T> cycleUntil(final Flux<T> stream, final Predicate<? super T> predicate) {
        return stream.repeat().takeUntil(predicate);
    }
    
    /**
     * Keep only those elements in a stream that are of a given type.
     * 
     * 
     * assertThat(Arrays.asList(1, 2, 3), 
     *      equalTo( FluxUtils.ofType(Flux.just(1, "a", 2, "b", 3,Integer.class));
     * 
     */
    @SuppressWarnings("unchecked")
    public static <T, U> Flux<U> ofType(final Flux<T> stream, final Class<? extends U> type) {
        return stream.filter(type::isInstance)
                     .map(t -> (U) t);
    }
    
    public final static <T> Flux<ListX<T>> groupedWhile(final Flux<T> stream, final Predicate<? super T> predicate) {
        return new GroupedWhile<T, ListX<T>>(
                                                   stream).batchWhile(predicate);
    }

    public final static <T, C extends Collection<? super T>> Flux<C> groupedWhile(final Flux<T> stream, final Predicate<? super T> predicate,
            final Supplier<C> factory) {
        return new GroupedWhile<T, C>(
                                            stream, factory).batchWhile(predicate);
    }

    public final static <T> Flux<ListX<T>> groupedUntil(final Flux<T> stream, final Predicate<? super T> predicate) {
        return groupedWhile(stream, predicate.negate());
    }
    
    
    /**
     * Performs a map operation that can call a recursive method without running out of stack space
     * <pre>
     * {@code
     * 
       FluxUtils.trampoline(Flux.just(10,20,30,40),i-> fibonacci(i))
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
     * 
       FluxUtils.trampoline(Flux.just(10_000,200_000,3_000_000,40_000_000),i-> fibonacci(i))
                .forEach(System.out::println);
                
                
     * completes successfully
     * }
     * </pre>
     * 
    * @param mapper TCO Transformation function
    * @return Functor transformed by the supplied transformation function
    */
   public static <T,R> Flux<R> trampoline(Flux<T> flux,Function<? super T, ? extends Trampoline<? extends R>> mapper) {
       return flux.map(in -> mapper.apply(in)
                              .result());
   }

   /**
   * Transform the elements of this Stream with a Pattern Matching case and default value
   *
   * <pre>
   * {@code
   * List<String> result = CollectionX.of(1,2,3,4)
                                            .patternMatch(
                                                      c->c.valuesWhere(i->"even", (Integer i)->i%2==0 )
                                                    )
   * }
   * // CollectionX["odd","even","odd","even"]
   * </pre>
   *
   *
   * @param case1 Function to generate a case (or chain of cases as a single case)
   * @param otherwise Value if supplied case doesn't match
   * @return CollectionX where elements are transformed by pattern matching
   */
   public static <T,R> Flux<R> patternMatch(Flux<T> flux,Function<CheckValue1<T, R>, CheckValue1<T, R>> case1, Supplier<? extends R> otherwise) {

       return flux.map(u -> Matchable.of(u)
                                .matches(case1, otherwise)
                                .get());

   }
   
   
   public static <T> Flux<T> reverse(Flux<T> flux){
       
      
       return Flux.fromIterable(()-> new Iterator<T>(){
        
        Iterator<T> it;
        private void init(){
            if(it==null){
                List<T> list = flux.collect(Collectors.toList()).block();
                ReactiveSeq<T> seq = ReactiveSeq.fromList(list).reverse();
                it = seq.iterator();
            }
        }
        @Override
        public boolean hasNext() {
            init();
            return it.hasNext();
        }

        @Override
        public T next() {
            init();
            return it.next();
        }
           
       }
     );
   }
   public static <T> Flux<T> shuffle(Flux<T> flux){
       
       
       return Flux.fromIterable(()-> new Iterator<T>(){
        
        Iterator<T> it;
        private void init(){
            if(it==null){
                List<T> list = flux.collect(Collectors.toList()).block();
                Collections.shuffle(list);
               
                it = list.iterator();
            }
        }
        @Override
        public boolean hasNext() {
            init();
            return it.hasNext();
        }

        @Override
        public T next() {
            init();
            return it.next();
        }
           
       }
     );
   }
   public static <T> Flux<T> shuffle(Flux<T> flux,Random random){
       
       
       return Flux.fromIterable(()-> new Iterator<T>(){
        
        Iterator<T> it;
        private void init(){
            if(it==null){
                List<T> list = flux.collect(Collectors.toList()).block();
                Collections.shuffle(list,random);
               
                it = list.iterator();
            }
        }
        @Override
        public boolean hasNext() {
            init();
            return it.hasNext();
        }

        @Override
        public T next() {
            init();
            return it.next();
        }
           
       }
     );
   }
   public static <T> Flux<T> sorted(Flux<T> flux){
       
       
       return Flux.fromIterable(()-> new Iterator<T>(){
        
        Iterator<T> it;
        private void init(){
            if(it==null){
                ReactiveSeq<T> seq = ReactiveSeq.fromPublisher(flux);
                it = seq.sorted().iterator();
            }
        }
        @Override
        public boolean hasNext() {
            init();
            return it.hasNext();
        }

        @Override
        public T next() {
            init();
            return it.next();
        }
           
       }
     );
   }
   public static <T> Flux<T> sorted(Flux<T> flux,Comparator<? super T> c){

       return Flux.fromIterable(()-> new Iterator<T>(){
        
        Iterator<T> it;
        private void init(){
            if(it==null){
                ReactiveSeq<T> seq = ReactiveSeq.fromPublisher(flux);
                it = seq.sorted(c).iterator();
            }
        }
        @Override
        public boolean hasNext() {
            init();
            return it.hasNext();
        }

        @Override
        public T next() {
            init();
            return it.next();
        }
           
       }
     );
   }
}

