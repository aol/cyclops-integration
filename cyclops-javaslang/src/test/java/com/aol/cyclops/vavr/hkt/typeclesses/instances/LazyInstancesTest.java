package com.aol.cyclops.vavr.hkt.typeclesses.instances;
import static com.aol.cyclops.vavr.hkt.LazyKind.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.LazyKind;
import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Eval;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.vavr.hkt.typeclasses.instances.LazyInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.Lazy;

public class LazyInstancesTest {

    @Test
    public void unit(){
        
        LazyKind<String> opt = LazyInstances.unit()
                                            .unit("hello")
                                            .convert(LazyKind::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->"hello")));
    }
    @Test
    public void functor(){
        
        LazyKind<Integer> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.functor().map((String v) ->v.length(), h))
                                     .convert(LazyKind::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->"hello".length())));
    }
    @Test
    public void apSimple(){
        LazyInstances.applicative()
            .ap(widen(Lazy.of(()->l1(this::multiplyByTwo))),widen(Lazy.of(()->1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        LazyKind<Function<Integer,Integer>> optFn =LazyInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(LazyKind::narrowK);
        
        LazyKind<Integer> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->LazyInstances.applicative().ap(optFn, h))
                                     .convert(LazyKind::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->"hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       LazyKind<Integer> opt  = LazyInstances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Lazy.of(()->i*2)), widen(Lazy.of(()->3)))
                                            .convert(LazyKind::narrowK);
    }
    @Test
    public void monad(){
        
        LazyKind<Integer> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.monad().flatMap((String v) ->LazyInstances.unit().unit(v.length()), h))
                                     .convert(LazyKind::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->"hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        LazyKind<String> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(LazyKind::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->"hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        LazyKind<String> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(LazyKind::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->null)));
    }
    
    @Test
    public void monadPlus(){
        LazyKind<Integer> opt = LazyInstances.<Integer>monadPlus()
                                      .plus(LazyKind.widen(Lazy.of(()->null)), LazyKind.widen(Lazy.of(()->10)))
                                      .convert(LazyKind::narrowK);
        assertThat(opt,equalTo(Lazy.of(()->10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<LazyKind<Integer>> m = Monoid.of(LazyKind.widen(Lazy.of(()->null)), (a, b)->a.get()==null ? b : a);
        LazyKind<Integer> opt = LazyInstances.<Integer>monadPlus(m)
                                      .plus(LazyKind.widen(Lazy.of(()->5)), LazyKind.widen(Lazy.of(()->10)))
                                      .convert(LazyKind::narrowK);
        assertThat(opt,equalTo(Lazy.of(()->5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = LazyInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, LazyKind.widen(Lazy.of(()->4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = LazyInstances.foldable()
                        .foldRight(0, (a,b)->a+b, LazyKind.widen(Lazy.of(()->1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<LazyKind.µ, Integer>> res = LazyInstances.traverse()
                                                                   .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), LazyKind.of(()->1))
                                                                 .convert(MaybeType::narrowK);
       
       
       assertThat(res,equalTo(Maybe.just(Lazy.of(()->2))));
    }
    
}
