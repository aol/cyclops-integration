package com.aol.cyclops.types;

import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.types.stream.reactive.ValueSubscriber;

public interface MonadicValue5<T1, T2, T3, T4, T5> extends MonadicValue<T5> {
    /**
     * Perform a flattening transformation of this Monadicvalue2
     * 
     * @param mapper
     *            transformation function
     * @return Transformed MonadicValue3
     */
    <R2> MonadicValue5<T1, T2, T3, T4,R2> flatMap(Function<? super T5, ? extends MonadicValue5<? extends T1, ?  extends T2, ? extends T3, ? extends T4,? extends R2>> mapper);

    /**
     * Eagerly combine two MonadicValues using the supplied monoid
     * 
     * <pre>
     * {@code 
     * 
     *  Monoid<Integer> add = Mondoid.of(1,Semigroups.intSum);
     *  Maybe.of(10).combineEager(add,Maybe.none());
     *  //Maybe[10]
     *  
     *  Maybe.none().combineEager(add,Maybe.of(10));
     *  //Maybe[10]
     *  
     *  Maybe.none().combineEager(add,Maybe.none());
     *  //Maybe.none()
     *  
     *  Maybe.of(10).combineEager(add,Maybe.of(10));
     *  //Maybe[20]
     *  
     *  Monoid
    <Integer> firstNonNull = Monoid.of(null , Semigroups.firstNonNull());
     *  Maybe.of(10).combineEager(firstNonNull,Maybe.of(10));
     *  //Maybe[10]
     * }
     * 
     * @param monoid Monoid to be used to combine values
     * @param v2 MonadicValue to combine with
     * @return Combined MonadicValue
     */
    default MonadicValue5<T1, T2, T3,T4,T5> combineEager(final Monoid<T5> monoid, final MonadicValue5<? extends T1, ? extends T2, ? extends T3,? extends T4, ? extends T5> v2) {
        return unit(this.<T5> flatMap(t1 -> v2.<T5>map(t2 -> monoid
                                                                   .apply(t1, t2)))
                        .orElseGet(() -> orElseGet(() -> monoid.zero())));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue#map(java.util.function.Function)
     */
    @Override
    <R> MonadicValue5<T1, T2, T3, T4,R> map(Function<? super T5, ? extends R> fn);

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue#unit(java.lang.Object)
     */
    @Override
    <T5> MonadicValue5<T1, T2,T3,T4,T5> unit(T5 unit);

    /**
     * A flattening transformation operation that takes the first value from the returned Publisher.
     * <pre>
     * {@code 
     *   Xor.primary(1).map(i->i+2).flatMapPublisher(i->Arrays.asList(()->i*3,20);
     *   //Xor[9]
     * 
     * }</pre>
     * 
     *
     * @param mapper  transformation function
     * @return MonadicValue3 the first element returned after the flatMap function is applied
     */
     <R> MonadicValue5<T1, T2, T3, T4,R> flatMapIterable(final Function<? super T5, ? extends Iterable<? extends R>> mapper) ;

    /**
    
     * A flattening transformation operation that takes the first value from the returned Publisher.
     * <pre>
     * {@code 
     *   Ior.primary(1).map(i->i+2).flatMapPublisher(i->Flux.just(()->i*3,20);
     *   //Xor[9]
     * 
     * }</pre>
     * @param mapper FlatMap  transformation function
     * @return MonadicValue3 subscribed from publisher after the flatMap function is applied
     */
    default <R> MonadicValue5<T1,T2, T3, T4,R> flatMapPublisher(final Function<? super T5, ? extends Publisher<? extends R>> mapper) {
        return this.flatMap(a -> {
            final Publisher<? extends R> publisher = mapper.apply(a);
            final ValueSubscriber<R> sub = ValueSubscriber.subscriber();

            publisher.subscribe(sub);
            return unit(sub.get());
        });
    }
}
