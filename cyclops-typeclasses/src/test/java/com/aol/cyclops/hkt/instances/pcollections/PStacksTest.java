package com.aol.cyclops.hkt.instances.pcollections;
import static com.aol.cyclops.hkt.pcollections.PStackType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.function.Function;

import org.junit.Test;
import org.pcollections.PStack;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.data.collections.extensions.persistent.PStackX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.hkt.pcollections.PStackType;
import com.aol.cyclops.util.function.Lambda;

public class PStacksTest {

    @Test
    public void unit(){
        
        PStackType<String> list = PStacks.unit()
                                     .unit("hello")
                                     .convert(PStackType::narrowK);
        
        assertThat(list,equalTo(PStackX.of("hello")));
    }
    @Test
    public void functor(){
        
        PStackType<Integer> list = PStacks.unit()
                                     .unit("hello")
                                     .then(h->PStacks.functor().map((String v) ->v.length(), h))
                                     .convert(PStackType::narrowK);
        
        assertThat(list,equalTo(PStackX.of("hello".length())));
    }
    @Test
    public void apSimple(){
        PStacks.zippingApplicative()
            .ap(widen(PStackX.of(l1(this::multiplyByTwo))),widen(PStackX.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        PStackType<Function<Integer,Integer>> listFn =PStacks.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(PStackType::narrowK);
        
        PStackType<Integer> list = PStacks.unit()
                                     .unit("hello")
                                     .then(h->PStacks.functor().map((String v) ->v.length(), h))
                                     .then(h->PStacks.zippingApplicative().ap(listFn, h))
                                     .convert(PStackType::narrowK);
        
        assertThat(list,equalTo(PStackX.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       PStackType<Integer> list  = PStacks.monad()
                                      .flatMap(i->widen(PStackX.range(0,i)), widen(PStackX.of(1,2,3)))
                                      .convert(PStackType::narrowK);
    }
    @Test
    public void monad(){
        
        PStackType<Integer> list = PStacks.unit()
                                     .unit("hello")
                                     .then(h->PStacks.monad().flatMap((String v) ->PStacks.unit().unit(v.length()), h))
                                     .convert(PStackType::narrowK);
        
        assertThat(list,equalTo(PStackX.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        PStackType<String> list = PStacks.unit()
                                     .unit("hello")
                                     .then(h->PStacks.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(PStackType::narrowK);
        
        assertThat(list,equalTo(PStackX.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        PStackType<String> list = PStacks.unit()
                                     .unit("hello")
                                     .then(h->PStacks.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(PStackType::narrowK);
        
        assertThat(list,equalTo(PStackX.empty()));
    }
    
    @Test
    public void monadPlus(){
        PStackType<Integer> list = PStacks.<Integer>monadPlus()
                                      .plus(PStackType.widen(PStackX.empty()), PStackType.widen(PStackX.of(10)))
                                      .convert(PStackType::narrowK);
        assertThat(list,equalTo(PStackX.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<PStackType<Integer>> m = Monoid.of(PStackType.widen(PStackX.empty()), (a,b)->a.isEmpty() ? b : a);
        PStackType<Integer> list = PStacks.<Integer>monadPlus(m)
                                      .plus(PStackType.widen(PStackX.of(5)), PStackType.widen(PStackX.of(10)))
                                      .convert(PStackType::narrowK);
        assertThat(list,equalTo(PStackX.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = PStacks.foldable()
                        .foldLeft(0, (a,b)->a+b, PStackType.widen(PStackX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = PStacks.foldable()
                        .foldRight(0, (a,b)->a+b, PStackType.widen(PStackX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<PStackType.µ, Integer>> res = PStacks.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), PStackType.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res,equalTo(Maybe.just(PStackX.of(2,4,6))));
    }
    
}
