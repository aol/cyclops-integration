package com.aol.cyclops.reactor.hkt.typeclasses.instances;
import static com.aol.cyclops.reactor.hkt.MonoType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.reactor.hkt.MonoType;
import com.aol.cyclops.util.function.Lambda;

import reactor.core.publisher.Mono;

public class MonosTest {

    @Test
    public void unit(){
        
        MonoType<String> opt = MonoInstances.unit()
                                            .unit("hello")
                                            .convert(MonoType::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(FutureW.ofResult("hello").join()));
    }
    @Test
    public void functor(){
        
        MonoType<Integer> opt = MonoInstances.unit()
                                     .unit("hello")
                                     .then(h->MonoInstances.functor().map((String v) ->v.length(), h))
                                     .convert(MonoType::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(FutureW.ofResult("hello".length()).join()));
    }
    @Test
    public void apSimple(){
        MonoInstances.applicative()
            .ap(widen(FutureW.ofResult(l1(this::multiplyByTwo))),widen(FutureW.ofResult(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        MonoType<Function<Integer,Integer>> optFn =MonoInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(MonoType::narrowK);
        
        MonoType<Integer> opt = MonoInstances.unit()
                                     .unit("hello")
                                     .then(h->MonoInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->MonoInstances.applicative().ap(optFn, h))
                                     .convert(MonoType::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(FutureW.ofResult("hello".length()*2).join()));
    }
    @Test
    public void monadSimple(){
       MonoType<Integer> opt  = MonoInstances.monad()
                                            .<Integer,Integer>flatMap(i->widen(FutureW.ofResult(i*2)), widen(FutureW.ofResult(3)))
                                            .convert(MonoType::narrowK);
    }
    @Test
    public void monad(){
        
        MonoType<Integer> opt = MonoInstances.unit()
                                     .unit("hello")
                                     .then(h->MonoInstances.monad().flatMap((String v) ->MonoInstances.unit().unit(v.length()), h))
                                     .convert(MonoType::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(FutureW.ofResult("hello".length()).join()));
    }
    @Test
    public void monadZeroFilter(){
        
        MonoType<String> opt = MonoInstances.unit()
                                     .unit("hello")
                                     .then(h->MonoInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(MonoType::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(FutureW.ofResult("hello").join()));
    }
    @Test
    public void monadZeroFilterOut(){
        
        MonoType<String> opt = MonoInstances.unit()
                                     .unit("hello")
                                     .then(h->MonoInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(MonoType::narrowK);
        
        assertTrue(opt.block()==null);
    }
    
    @Test
    public void monadPlus(){
        MonoType<Integer> opt = MonoInstances.<Integer>monadPlus()
                                      .plus(MonoType.widen(Mono.empty()), MonoType.widen(Mono.just(10)))
                                      .convert(MonoType::narrowK);
        assertTrue(opt.block()==null);
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<MonoType<Integer>> m = Monoid.of(MonoType.widen(Mono.empty()), (a,b)->a.toFuture().isDone() ? b : a);
        MonoType<Integer> opt = MonoInstances.<Integer>monadPlus(m)
                                      .plus(MonoType.widen(Mono.just(5)), MonoType.widen(Mono.just(10)))
                                      .convert(MonoType::narrowK);
        assertThat(opt.block(),equalTo(10));
    }
    @Test
    public void  foldLeft(){
        int sum  = MonoInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, MonoType.widen(FutureW.ofResult(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = MonoInstances.foldable()
                        .foldRight(0, (a,b)->a+b, MonoType.widen(FutureW.ofResult(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<MonoType.µ, Integer>> res = MonoInstances.traverse()
                                                                 .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), MonoType.just(1))
                                                                 .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(h->h.convert(MonoType::narrowK).block()),
                  equalTo(Maybe.just(Mono.just(2).block())));
    }
    
}
