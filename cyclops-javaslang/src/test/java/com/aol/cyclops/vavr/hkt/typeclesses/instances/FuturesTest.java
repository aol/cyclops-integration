package com.aol.cyclops.vavr.hkt.typeclesses.instances;

import static com.aol.cyclops.vavr.hkt.FutureKind.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.FutureKind;
import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.vavr.hkt.typeclasses.instances.Instances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.concurrent.Future;

public class FuturesTest {

    @Test
    public void unit(){
        
        FutureKind<String> opt = Instances.unit()
                                            .unit("hello")
                                            .convert(FutureKind::narrowK);
        
        assertThat(opt.get(),equalTo(Future.successful("hello").get()));
    }
    @Test
    public void functor(){
        
        FutureKind<Integer> opt = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .convert(FutureKind::narrowK);
        
        assertThat(opt.get(),equalTo(FutureW.ofResult("hello".length()).get()));
    }
    @Test
    public void apSimple(){
        Instances.applicative()
            .ap(widen(FutureW.ofResult(l1(this::multiplyByTwo))),widen(FutureW.ofResult(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        FutureKind<Function<Integer,Integer>> optFn = Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(FutureKind::narrowK);
        
        FutureKind<Integer> opt = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .then(h-> Instances.applicative().ap(optFn, h))
                                     .convert(FutureKind::narrowK);
        
        assertThat(opt.get(),equalTo(FutureW.ofResult("hello".length()*2).get()));
    }
    @Test
    public void monadSimple(){
       FutureKind<Integer> opt  = Instances.monad()
                                            .<Integer,Integer>flatMap(i->widen(FutureW.ofResult(i*2)), widen(FutureW.ofResult(3)))
                                            .convert(FutureKind::narrowK);
    }
    @Test
    public void monad(){
        
        FutureKind<Integer> opt = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.monad().flatMap((String v) -> Instances.unit().unit(v.length()), h))
                                     .convert(FutureKind::narrowK);
        
        assertThat(opt.get(),equalTo(FutureW.ofResult("hello".length()).get()));
    }
    @Test
    public void monadZeroFilter(){
        
        FutureKind<String> opt = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FutureKind::narrowK);
        
        assertThat(opt.get(),equalTo(Future.successful("hello").get()));
    }
    @Test
    public void monadZeroFilterOut(){
        
        FutureKind<String> opt = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(FutureKind::narrowK);
        
        assertFalse(opt.isCompleted());
    }
    
    @Test
    public void monadPlus(){
        FutureKind<Integer> opt = Instances.<Integer>monadPlus()
                                      .plus(FutureKind.widen(FutureW.future()), FutureKind.widen(FutureW.ofResult(10)))
                                      .convert(FutureKind::narrowK);
        assertThat(opt.get(),equalTo(FutureW.ofResult(10).get()));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<FutureKind<Integer>> m = Monoid.of(FutureKind.widen(FutureW.future()), (a, b)->a.isCompleted() ? b : a);
        FutureKind<Integer> opt = Instances.<Integer>monadPlus(m)
                                      .plus(FutureKind.widen(FutureW.ofResult(5)), FutureKind.widen(FutureW.ofResult(10)))
                                      .convert(FutureKind::narrowK);
        assertThat(opt.get(),equalTo(FutureW.ofResult(10).get()));
    }
    @Test
    public void  foldLeft(){
        int sum  = Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, FutureKind.widen(FutureW.ofResult(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = Instances.foldable()
                        .foldRight(0, (a,b)->a+b, FutureKind.widen(FutureW.ofResult(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<FutureKind.µ, Integer>> res = Instances.traverse()
                                                                 .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), FutureKind.successful(1))
                                                                 .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(h->h.convert(FutureKind::narrowK).get()),
                  equalTo(Maybe.just(Future.successful(2).get())));
    }
    
}
