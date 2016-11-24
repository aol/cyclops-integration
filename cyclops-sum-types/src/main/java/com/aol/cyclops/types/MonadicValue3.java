package com.aol.cyclops.types;

import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.types.stream.reactive.ValueSubscriber;

public interface MonadicValue3<T1, T2, T3> extends MonadicValue<T3> {
    /**
     * Perform a flattening transformation of this Monadicvalue2
     * 
     * @param mapper
     *            transformation function
     * @return Transformed MonadicValue3
     */
    <R2> MonadicValue3<T1, T2, R2> flatMap(Function<? super T3, ? extends MonadicValue3<? extends T1, ?  extends T2, ? extends R2>> mapper);

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
    default MonadicValue3<T1, T2, T3> combineEager(final Monoid<T3> monoid, final MonadicValue3<? extends T1, ? extends T2, ? extends T3> v2) {
        return unit(this.<T3> flatMap(t1 -> v2.<T3>map(t2 -> monoid
                                                                   .apply(t1, t2)))
                        .orElseGet(() -> orElseGet(() -> monoid.zero())));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue#map(java.util.function.Function)
     */
    @Override
    <R> MonadicValue3<T1, T2, R> map(Function<? super T3, ? extends R> fn);

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue#unit(java.lang.Object)
     */
    @Override
    <T3> MonadicValue3<T1, T2,T3> unit(T3 unit);

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
     <R> MonadicValue3<T1, T2, R> flatMapIterable(final Function<? super T3, ? extends Iterable<? extends R>> mapper) ;

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
    default <R> MonadicValue3<T1,T2, R> flatMapPublisher(final Function<? super T3, ? extends Publisher<? extends R>> mapper) {
        return this.flatMap(a -> {
            final Publisher<? extends R> publisher = mapper.apply(a);
            final ValueSubscriber<R> sub = ValueSubscriber.subscriber();

            publisher.subscribe(sub);
            return unit(sub.get());
        });
    }
}
