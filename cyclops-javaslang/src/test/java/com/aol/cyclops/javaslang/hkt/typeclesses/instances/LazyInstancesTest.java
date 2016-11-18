package com.aol.cyclops.javaslang.hkt.typeclesses.instances;
import static com.aol.cyclops.javaslang.hkt.LazyType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Eval;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.javaslang.hkt.LazyType;
import com.aol.cyclops.javaslang.hkt.OptionType;
import com.aol.cyclops.javaslang.hkt.typeclasses.instances.LazyInstances;
import com.aol.cyclops.javaslang.hkt.typeclasses.instances.OptionInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.Lazy;
import javaslang.control.Option;

public class LazyInstancesTest {

    @Test
    public void unit(){
        
        LazyType<String> opt = LazyInstances.unit()
                                            .unit("hello")
                                            .convert(LazyType::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->"hello")));
    }
    @Test
    public void functor(){
        
        LazyType<Integer> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.functor().map((String v) ->v.length(), h))
                                     .convert(LazyType::narrowK);
        
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
        
        LazyType<Function<Integer,Integer>> optFn =LazyInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(LazyType::narrowK);
        
        LazyType<Integer> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->LazyInstances.applicative().ap(optFn, h))
                                     .convert(LazyType::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->"hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       LazyType<Integer> opt  = LazyInstances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Lazy.of(()->i*2)), widen(Lazy.of(()->3)))
                                            .convert(LazyType::narrowK);
    }
    @Test
    public void monad(){
        
        LazyType<Integer> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.monad().flatMap((String v) ->LazyInstances.unit().unit(v.length()), h))
                                     .convert(LazyType::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->"hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        LazyType<String> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(LazyType::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->"hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        LazyType<String> opt = LazyInstances.unit()
                                     .unit("hello")
                                     .then(h->LazyInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(LazyType::narrowK);
        
        assertThat(opt,equalTo(Lazy.of(()->null)));
    }
    
    @Test
    public void monadPlus(){
        LazyType<Integer> opt = LazyInstances.<Integer>monadPlus()
                                      .plus(LazyType.widen(Lazy.of(()->null)), LazyType.widen(Lazy.of(()->10)))
                                      .convert(LazyType::narrowK);
        assertThat(opt,equalTo(Lazy.of(()->10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<LazyType<Integer>> m = Monoid.of(LazyType.widen(Lazy.of(()->null)), (a,b)->a.get()==null ? b : a);
        LazyType<Integer> opt = LazyInstances.<Integer>monadPlus(m)
                                      .plus(LazyType.widen(Lazy.of(()->5)), LazyType.widen(Lazy.of(()->10)))
                                      .convert(LazyType::narrowK);
        assertThat(opt,equalTo(Lazy.of(()->5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = LazyInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, LazyType.widen(Lazy.of(()->4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = LazyInstances.foldable()
                        .foldRight(0, (a,b)->a+b, LazyType.widen(Lazy.of(()->1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<LazyType.µ, Integer>> res = LazyInstances.traverse()
                                                                   .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), LazyType.of(()->1))
                                                                 .convert(MaybeType::narrowK);
       
       
       assertThat(res,equalTo(Maybe.just(Lazy.of(()->2))));
    }
    
}
