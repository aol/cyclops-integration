package com.aol.cyclops.hkt.instances.cyclops;
import static com.aol.cyclops.hkt.cyclops.MaybeType.widen;
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

public class MaybesTest {

    @Test
    public void unit(){
        
        MaybeType<String> opt = MaybeInstances.unit()
                                            .unit("hello")
                                            .convert(MaybeType::narrowK);
        
        assertThat(opt,equalTo(Maybe.of("hello")));
    }
    @Test
    public void functor(){
        
        MaybeType<Integer> opt = MaybeInstances.unit()
                                     .unit("hello")
                                     .then(h->MaybeInstances.functor().map((String v) ->v.length(), h))
                                     .convert(MaybeType::narrowK);
        
        assertThat(opt,equalTo(Maybe.of("hello".length())));
    }
    @Test
    public void apSimple(){
        MaybeInstances.applicative()
            .ap(widen(Maybe.of(l1(this::multiplyByTwo))),widen(Maybe.of(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        MaybeType<Function<Integer,Integer>> optFn =MaybeInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(MaybeType::narrowK);
        
        MaybeType<Integer> opt = MaybeInstances.unit()
                                     .unit("hello")
                                     .then(h->MaybeInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->MaybeInstances.applicative().ap(optFn, h))
                                     .convert(MaybeType::narrowK);
        
        assertThat(opt,equalTo(Maybe.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       MaybeType<Integer> opt  = MaybeInstances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Maybe.of(i*2)), widen(Maybe.of(3)))
                                            .convert(MaybeType::narrowK);
    }
    @Test
    public void monad(){
        
        MaybeType<Integer> opt = MaybeInstances.unit()
                                     .unit("hello")
                                     .then(h->MaybeInstances.monad().flatMap((String v) ->MaybeInstances.unit().unit(v.length()), h))
                                     .convert(MaybeType::narrowK);
        
        assertThat(opt,equalTo(Maybe.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        MaybeType<String> opt = MaybeInstances.unit()
                                     .unit("hello")
                                     .then(h->MaybeInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(MaybeType::narrowK);
        
        assertThat(opt,equalTo(Maybe.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        MaybeType<String> opt = MaybeInstances.unit()
                                     .unit("hello")
                                     .then(h->MaybeInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(MaybeType::narrowK);
        
        assertThat(opt,equalTo(Maybe.none()));
    }
    
    @Test
    public void monadPlus(){
        MaybeType<Integer> opt = MaybeInstances.<Integer>monadPlus()
                                      .plus(MaybeType.widen(Maybe.none()), MaybeType.widen(Maybe.of(10)))
                                      .convert(MaybeType::narrowK);
        assertThat(opt,equalTo(Maybe.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<MaybeType<Integer>> m = Monoid.of(MaybeType.widen(Maybe.none()), (a,b)->a.isPresent() ? b : a);
        MaybeType<Integer> opt = MaybeInstances.<Integer>monadPlus(m)
                                      .plus(MaybeType.widen(Maybe.of(5)), MaybeType.widen(Maybe.of(10)))
                                      .convert(MaybeType::narrowK);
        assertThat(opt,equalTo(Maybe.of(10)));
    }
    @Test
    public void  foldLeft(){
        int sum  = MaybeInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, MaybeType.widen(Maybe.of(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = MaybeInstances.foldable()
                        .foldRight(0, (a,b)->a+b, MaybeType.widen(Maybe.of(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<MaybeType.µ, Integer>> res = MaybeInstances.traverse()
                                                          .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), MaybeType.just(1))
                                                          .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(h->h.convert(MaybeType::narrow).get()),
                  equalTo(Maybe.just(Maybe.just(2).get())));
    }
    
}
