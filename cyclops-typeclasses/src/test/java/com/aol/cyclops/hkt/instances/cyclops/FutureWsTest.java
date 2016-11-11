package com.aol.cyclops.hkt.instances.cyclops;
import static com.aol.cyclops.hkt.cyclops.FutureType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.FutureType;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.jdk.CompletableFutureInstances;
import com.aol.cyclops.hkt.jdk.CompletableFutureType;
import com.aol.cyclops.util.function.Lambda;

public class FutureWsTest {

    @Test
    public void unit(){
        
        FutureType<String> opt = FutureWs.unit()
                                            .unit("hello")
                                            .convert(FutureType::narrowK);
        
        assertThat(opt.toCompletableFuture().join(),equalTo(FutureW.ofResult("hello").join()));
    }
    @Test
    public void functor(){
        
        FutureType<Integer> opt = FutureWs.unit()
                                     .unit("hello")
                                     .then(h->FutureWs.functor().map((String v) ->v.length(), h))
                                     .convert(FutureType::narrowK);
        
        assertThat(opt.join(),equalTo(FutureW.ofResult("hello".length()).join()));
    }
    @Test
    public void apSimple(){
        FutureWs.applicative()
            .ap(widen(FutureW.ofResult(l1(this::multiplyByTwo))),widen(FutureW.ofResult(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        FutureType<Function<Integer,Integer>> optFn =FutureWs.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(FutureType::narrowK);
        
        FutureType<Integer> opt = FutureWs.unit()
                                     .unit("hello")
                                     .then(h->FutureWs.functor().map((String v) ->v.length(), h))
                                     .then(h->FutureWs.applicative().ap(optFn, h))
                                     .convert(FutureType::narrowK);
        
        assertThat(opt.join(),equalTo(FutureW.ofResult("hello".length()*2).join()));
    }
    @Test
    public void monadSimple(){
       FutureType<Integer> opt  = FutureWs.monad()
                                            .<Integer,Integer>flatMap(i->widen(FutureW.ofResult(i*2)), widen(FutureW.ofResult(3)))
                                            .convert(FutureType::narrowK);
    }
    @Test
    public void monad(){
        
        FutureType<Integer> opt = FutureWs.unit()
                                     .unit("hello")
                                     .then(h->FutureWs.monad().flatMap((String v) ->FutureWs.unit().unit(v.length()), h))
                                     .convert(FutureType::narrowK);
        
        assertThat(opt.join(),equalTo(FutureW.ofResult("hello".length()).join()));
    }
    @Test
    public void monadZeroFilter(){
        
        FutureType<String> opt = FutureWs.unit()
                                     .unit("hello")
                                     .then(h->FutureWs.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FutureType::narrowK);
        
        assertThat(opt.toCompletableFuture().join(),equalTo(FutureW.ofResult("hello").join()));
    }
    @Test
    public void monadZeroFilterOut(){
        
        FutureType<String> opt = FutureWs.unit()
                                     .unit("hello")
                                     .then(h->FutureWs.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(FutureType::narrowK);
        
        assertFalse(opt.toCompletableFuture().isDone());
    }
    
    @Test
    public void monadPlus(){
        FutureType<Integer> opt = FutureWs.<Integer>monadPlus()
                                      .plus(FutureType.widen(FutureW.future()), FutureType.widen(FutureW.ofResult(10)))
                                      .convert(FutureType::narrowK);
        assertThat(opt.get(),equalTo(FutureW.ofResult(10).get()));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<FutureType<Integer>> m = Monoid.of(FutureType.widen(FutureW.future()), (a,b)->a.toCompletableFuture().isDone() ? b : a);
        FutureType<Integer> opt = FutureWs.<Integer>monadPlus(m)
                                      .plus(FutureType.widen(FutureW.ofResult(5)), FutureType.widen(FutureW.ofResult(10)))
                                      .convert(FutureType::narrowK);
        assertThat(opt.join(),equalTo(FutureW.ofResult(10).join()));
    }
    @Test
    public void  foldLeft(){
        int sum  = FutureWs.foldable()
                        .foldLeft(0, (a,b)->a+b, FutureType.widen(FutureW.ofResult(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = FutureWs.foldable()
                        .foldRight(0, (a,b)->a+b, FutureType.widen(FutureW.ofResult(1)));
        
        assertThat(sum,equalTo(1));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<FutureType.µ, Integer>> res = FutureWs.traverse()
                                                               .traverseA(Maybes.applicative(), (Integer a)->MaybeType.just(a*2), FutureType.ofResult(1))
                                                              .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(h->h.convert(FutureType::narrow).get()),
                  equalTo(Maybe.just(FutureW.ofResult(2).get())));
    }
    
}
