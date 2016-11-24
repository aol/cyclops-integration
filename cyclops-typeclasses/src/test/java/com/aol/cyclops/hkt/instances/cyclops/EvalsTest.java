package com.aol.cyclops.hkt.instances.cyclops;
import static com.aol.cyclops.hkt.cyclops.EvalType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Eval;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.EvalType;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.util.function.Lambda;

public class EvalsTest {

    @Test
    public void unit(){
        
        EvalType<String> opt = EvalInstances.unit()
                                            .unit("hello")
                                            .convert(EvalType::narrowK);
        
        assertThat(opt,equalTo(Eval.now("hello")));
    }
    @Test
    public void functor(){
        
        EvalType<Integer> opt = EvalInstances.unit()
                                     .unit("hello")
                                     .then(h->EvalInstances.functor().map((String v) ->v.length(), h))
                                     .convert(EvalType::narrowK);
        
        assertThat(opt,equalTo(Eval.now("hello".length())));
    }
    @Test
    public void apSimple(){
        EvalInstances.applicative()
            .ap(widen(Eval.now(l1(this::multiplyByTwo))),widen(Eval.now(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        EvalType<Function<Integer,Integer>> optFn =EvalInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(EvalType::narrowK);
        
        EvalType<Integer> opt = EvalInstances.unit()
                                     .unit("hello")
                                     .then(h->EvalInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->EvalInstances.applicative().ap(optFn, h))
                                     .convert(EvalType::narrowK);
        
        assertThat(opt,equalTo(Eval.now("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       EvalType<Integer> opt  = EvalInstances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Eval.now(i*2)), widen(Eval.now(3)))
                                            .convert(EvalType::narrowK);
    }
    @Test
    public void monad(){
        
        EvalType<Integer> opt = EvalInstances.unit()
                                     .unit("hello")
                                     .then(h->EvalInstances.monad().flatMap((String v) ->EvalInstances.unit().unit(v.length()), h))
                                     .convert(EvalType::narrowK);
        
        assertThat(opt,equalTo(Eval.now("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        EvalType<String> opt = EvalInstances.unit()
                                     .unit("hello")
                                     .then(h->EvalInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(EvalType::narrowK);
        
        assertThat(opt,equalTo(Eval.now("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        EvalType<String> opt = EvalInstances.unit()
                                     .unit("hello")
                                     .then(h->EvalInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(EvalType::narrowK);
        
        assertThat(opt,equalTo(Eval.now(null)));
    }
    
    @Test
    public void monadPlus(){
        EvalType<Integer> opt = EvalInstances.<Integer>monadPlus()
                                      .plus(EvalType.widen(Eval.now(null)), EvalType.widen(Eval.now(10)))
                                      .convert(EvalType::narrowK);
        assertThat(opt,equalTo(Eval.now(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<EvalType<Integer>> m = Monoid.of(EvalType.widen(Eval.now(null)), (a,b)->a.isPresent() ? b : a);
        EvalType<Integer> opt = EvalInstances.<Integer>monadPlus(m)
                                      .plus(EvalType.widen(Eval.now(5)), EvalType.widen(Eval.now(10)))
                                      .convert(EvalType::narrowK);
        assertThat(opt,equalTo(Eval.now(10)));
    }
    @Test
    public void  foldLeft(){
        int sum  = EvalInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, EvalType.widen(Eval.now(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = EvalInstances.foldable()
                        .foldRight(0, (a,b)->a+b, EvalType.widen(Eval.now(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<EvalType.µ, Integer>> res = EvalInstances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), EvalType.now(1))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(h->h.convert(EvalType::narrow).get()),
                  equalTo(Maybe.just(Eval.now(2).get())));
    }
    
}
