package com.aol.cyclops.hkt.instances.jdk;
import static com.aol.cyclops.hkt.jdk.CompletableFutureType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.hkt.jdk.CompletableFutureType;
import com.aol.cyclops.hkt.jdk.OptionalType;
import com.aol.cyclops.util.CompletableFutures;
import com.aol.cyclops.util.function.Lambda;

public class CompletableFuturesTest {

    @Test
    public void unit(){
        
        CompletableFutureType<String> opt = CompletableFutureInstances.unit()
                                            .unit("hello")
                                            .convert(CompletableFutureType::narrowK);
        
        assertThat(opt.toCompletableFuture().join(),equalTo(CompletableFuture.completedFuture("hello").join()));
    }
    @Test
    public void functor(){
        
        CompletableFutureType<Integer> opt = CompletableFutureInstances.unit()
                                     .unit("hello")
                                     .then(h->CompletableFutureInstances.functor().map((String v) ->v.length(), h))
                                     .convert(CompletableFutureType::narrowK);
        
        assertThat(opt.toCompletableFuture().join(),equalTo(CompletableFuture.completedFuture("hello".length()).join()));
    }
    @Test
    public void apSimple(){
        CompletableFutureInstances.applicative()
            .ap(widen(CompletableFuture.completedFuture(l1(this::multiplyByTwo))),widen(CompletableFuture.completedFuture(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        CompletableFutureType<Function<Integer,Integer>> optFn =CompletableFutureInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(CompletableFutureType::narrowK);
        
        CompletableFutureType<Integer> opt = CompletableFutureInstances.unit()
                                     .unit("hello")
                                     .then(h->CompletableFutureInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->CompletableFutureInstances.applicative().ap(optFn, h))
                                     .convert(CompletableFutureType::narrowK);
        
        assertThat(opt.toCompletableFuture().join(),equalTo(CompletableFuture.completedFuture("hello".length()*2).join()));
    }
    @Test
    public void monadSimple(){
       CompletableFutureType<Integer> opt  = CompletableFutureInstances.monad()
                                            .<Integer,Integer>flatMap(i->widen(CompletableFuture.completedFuture(i*2)), widen(CompletableFuture.completedFuture(3)))
                                            .convert(CompletableFutureType::narrowK);
    }
    @Test
    public void monad(){
        
        CompletableFutureType<Integer> opt = CompletableFutureInstances.unit()
                                     .unit("hello")
                                     .then(h->CompletableFutureInstances.monad().flatMap((String v) ->CompletableFutureInstances.unit().unit(v.length()), h))
                                     .convert(CompletableFutureType::narrowK);
        
        assertThat(opt.toCompletableFuture().join(),equalTo(CompletableFuture.completedFuture("hello".length()).join()));
    }
    @Test
    public void monadZeroFilter(){
        
        CompletableFutureType<String> opt = CompletableFutureInstances.unit()
                                     .unit("hello")
                                     .then(h->CompletableFutureInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(CompletableFutureType::narrowK);
        
        assertThat(opt.toCompletableFuture().join(),equalTo(CompletableFuture.completedFuture("hello").join()));
    }
    @Test
    public void monadZeroFilterOut(){
        
        CompletableFutureType<String> opt = CompletableFutureInstances.unit()
                                     .unit("hello")
                                     .then(h->CompletableFutureInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(CompletableFutureType::narrowK);
        
        assertFalse(opt.toCompletableFuture().isDone());
    }
    
    @Test
    public void monadPlus(){
        CompletableFutureType<Integer> opt = CompletableFutureInstances.<Integer>monadPlus()
                                      .plus(CompletableFutureType.widen(new CompletableFuture<>()), CompletableFutureType.widen(CompletableFuture.completedFuture(10)))
                                      .convert(CompletableFutureType::narrowK);
        assertThat(opt.toCompletableFuture().join(),equalTo(CompletableFuture.completedFuture(10).join()));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<CompletableFutureType<Integer>> m = Monoid.of(CompletableFutureType.widen(new CompletableFuture<>()), (a,b)->a.toCompletableFuture().isDone() ? b : a);
        CompletableFutureType<Integer> opt = CompletableFutureInstances.<Integer>monadPlus(m)
                                      .plus(CompletableFutureType.widen(CompletableFuture.completedFuture(5)), CompletableFutureType.widen(CompletableFuture.completedFuture(10)))
                                      .convert(CompletableFutureType::narrowK);
        assertThat(opt.toCompletableFuture().join(),equalTo(CompletableFuture.completedFuture(10).join()));
    }
    @Test
    public void  foldLeft(){
        int sum  = CompletableFutureInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, CompletableFutureType.widen(CompletableFuture.completedFuture(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = CompletableFutureInstances.foldable()
                        .foldRight(0, (a,b)->a+b, CompletableFutureType.widen(CompletableFuture.completedFuture(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<CompletableFutureType.µ, Integer>> res = CompletableFutureInstances.traverse()
                                                                          .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), CompletableFutureType.completedFuture(1))
                                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res.get().convert(CompletableFutureType::narrow).join(),equalTo(2));
    }
    
}
