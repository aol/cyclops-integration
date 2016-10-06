package com.aol.cyclops.reactor;

import java.util.Iterator;
import java.util.function.BiFunction;

import org.reactivestreams.Publisher;

import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.types.Value;
import com.aol.cyclops.types.stream.reactive.ValueSubscriber;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Extension Methods for Mono
 * 
 * 
 * @author johnmcclean
 *
 */
public class MonoUtils {

    /**
     * Lazily combine this Mono with the supplied value via the supplied BiFunction
     * 
     * @param mono Mono to combine with another value
     * @param app Value to combine with supplied mono
     * @param fn Combiner function
     * @return Combined Mono
     */
    public static <T1,T2,R> Mono<R> combine(Mono<? extends T1> mono,Value<? extends T2> app, BiFunction<? super T1, ? super T2, ? extends R> fn){
        return  Mono.from(FutureW.of(mono.toFuture()).combine(app, fn));
    }
    /**
     * Lazily combine this Mono with the supplied Mono via the supplied BiFunction
     * 
     * @param mono Mono to combine with another value
     * @param app Mono to combine with supplied mono
     * @param fn Combiner function
     * @return Combined Mono
     */
    public static <T1,T2,R> Mono<R> combine(Mono<? extends T1> mono,Mono<? extends T2> app, BiFunction<? super T1, ? super T2, ? extends R> fn){
        return  Mono.from(FutureW.of(mono.toFuture()).combine(FutureW.of(app.toFuture()), fn));
    }
    public static <T1,T2,R> Mono<R> zip(Mono<? extends T1> mono,Iterable<? extends T2> app, BiFunction<? super T1, ? super T2, ? extends R> fn){
        return  Mono.from(FutureW.of(mono.toFuture()).zip(app, fn));
    }
    public static <T1,T2,R> Mono<R> zip(Mono<? extends T1> mono,BiFunction<? super T1, ? super T2, ? extends R> fn, Publisher<? extends T2> app){
        return  Mono.from(FutureW.of(mono.toFuture()).zip(fn,app));
    }
    public static <T> boolean test(Mono<T> mono,T test){
        return  FutureW.of(mono.toFuture()).test(test);
    }
    
    public static <T> Mono<T> fromIterable(Iterable<T> t){
        return Mono.from(Flux.fromIterable(t));
    }
    public static<T> Iterator<T> iterator(Mono<T> pub){
     
        ValueSubscriber<T> sub = ValueSubscriber.subscriber();
        pub.subscribe(sub);
        return sub.iterator();
    }
}
