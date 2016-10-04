package com.aol.cyclops.reactor;

import java.util.function.BiFunction;

import org.reactivestreams.Publisher;

import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.types.Value;

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

    public static <T1,T2,R> Mono<R> combine(Mono<? extends T1> mono,Value<? extends T2> app, BiFunction<? super T1, ? super T2, ? extends R> fn){
        return  Mono.from(FutureW.of(mono.toFuture()).combine(app, fn));
    }
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
}
