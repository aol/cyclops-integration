package com.aol.cyclops.javaslang.hkt.typeclesses.instances;

import static com.aol.cyclops.javaslang.hkt.FutureType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.Maybes;
import com.aol.cyclops.javaslang.hkt.FutureType;
import com.aol.cyclops.javaslang.hkt.OptionType;
import com.aol.cyclops.javaslang.hkt.typeclasses.instances.Futures;
import com.aol.cyclops.javaslang.hkt.typeclasses.instances.Options;
import com.aol.cyclops.util.function.Lambda;

import javaslang.concurrent.Future;
import javaslang.control.Option;

public class FuturesTest {

    @Test
    public void unit(){
        
        FutureType<String> opt = Futures.unit()
                                            .unit("hello")
                                            .convert(FutureType::narrowK);
        
        assertThat(opt.get(),equalTo(Future.successful("hello").get()));
    }
    @Test
    public void functor(){
        
        FutureType<Integer> opt = Futures.unit()
                                     .unit("hello")
                                     .then(h->Futures.functor().map((String v) ->v.length(), h))
                                     .convert(FutureType::narrowK);
        
        assertThat(opt.get(),equalTo(FutureW.ofResult("hello".length()).get()));
    }
    @Test
    public void apSimple(){
        Futures.applicative()
            .ap(widen(FutureW.ofResult(l1(this::multiplyByTwo))),widen(FutureW.ofResult(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        FutureType<Function<Integer,Integer>> optFn =Futures.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(FutureType::narrowK);
        
        FutureType<Integer> opt = Futures.unit()
                                     .unit("hello")
                                     .then(h->Futures.functor().map((String v) ->v.length(), h))
                                     .then(h->Futures.applicative().ap(optFn, h))
                                     .convert(FutureType::narrowK);
        
        assertThat(opt.get(),equalTo(FutureW.ofResult("hello".length()*2).get()));
    }
    @Test
    public void monadSimple(){
       FutureType<Integer> opt  = Futures.monad()
                                            .<Integer,Integer>flatMap(i->widen(FutureW.ofResult(i*2)), widen(FutureW.ofResult(3)))
                                            .convert(FutureType::narrowK);
    }
    @Test
    public void monad(){
        
        FutureType<Integer> opt = Futures.unit()
                                     .unit("hello")
                                     .then(h->Futures.monad().flatMap((String v) ->Futures.unit().unit(v.length()), h))
                                     .convert(FutureType::narrowK);
        
        assertThat(opt.get(),equalTo(FutureW.ofResult("hello".length()).get()));
    }
    @Test
    public void monadZeroFilter(){
        
        FutureType<String> opt = Futures.unit()
                                     .unit("hello")
                                     .then(h->Futures.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FutureType::narrowK);
        
        assertThat(opt.get(),equalTo(Future.successful("hello").get()));
    }
    @Test
    public void monadZeroFilterOut(){
        
        FutureType<String> opt = Futures.unit()
                                     .unit("hello")
                                     .then(h->Futures.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(FutureType::narrowK);
        
        assertFalse(opt.isCompleted());
    }
    
    @Test
    public void monadPlus(){
        FutureType<Integer> opt = Futures.<Integer>monadPlus()
                                      .plus(FutureType.widen(FutureW.future()), FutureType.widen(FutureW.ofResult(10)))
                                      .convert(FutureType::narrowK);
        assertThat(opt.get(),equalTo(FutureW.ofResult(10).get()));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<FutureType<Integer>> m = Monoid.of(FutureType.widen(FutureW.future()), (a,b)->a.isCompleted() ? b : a);
        FutureType<Integer> opt = Futures.<Integer>monadPlus(m)
                                      .plus(FutureType.widen(FutureW.ofResult(5)), FutureType.widen(FutureW.ofResult(10)))
                                      .convert(FutureType::narrowK);
        assertThat(opt.get(),equalTo(FutureW.ofResult(10).get()));
    }
    @Test
    public void  foldLeft(){
        int sum  = Futures.foldable()
                        .foldLeft(0, (a,b)->a+b, FutureType.widen(FutureW.ofResult(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = Futures.foldable()
                        .foldRight(0, (a,b)->a+b, FutureType.widen(FutureW.ofResult(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<FutureType.µ, Integer>> res = Futures.traverse()
                                                                 .traverseA(Maybes.applicative(), (Integer a)->MaybeType.just(a*2), FutureType.successful(1))
                                                                 .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(h->h.convert(FutureType::narrowK).get()),
                  equalTo(Maybe.just(Future.successful(2).get())));
    }
    
}
