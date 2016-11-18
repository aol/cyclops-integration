package com.aol.cyclops.functionaljava.hkt.typeclesses.instances;
import static com.aol.cyclops.functionaljava.hkt.OptionType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.functionaljava.hkt.OptionType;
import com.aol.cyclops.functionaljava.hkt.typeclassess.instances.OptionInstances;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.hkt.instances.jdk.OptionalInstances;
import com.aol.cyclops.hkt.jdk.OptionalType;
import com.aol.cyclops.util.function.Lambda;

import fj.data.Option;

public class OptionTest {

    @Test
    public void unit(){
        
        OptionType<String> opt = OptionInstances.unit()
                                            .unit("hello")
                                            .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello")));
    }
    @Test
    public void functor(){
        
        OptionType<Integer> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.functor().map((String v) ->v.length(), h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello".length())));
    }
    @Test
    public void apSimple(){
        OptionInstances.applicative()
            .ap(widen(Option.some(l1(this::multiplyByTwo))),widen(Option.some(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        OptionType<Function<Integer,Integer>> optFn =OptionInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(OptionType::narrowK);
        
        OptionType<Integer> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->OptionInstances.applicative().ap(optFn, h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       OptionType<Integer> opt  = OptionInstances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Option.some(i*2)), widen(Option.some(3)))
                                            .convert(OptionType::narrowK);
    }
    @Test
    public void monad(){
        
        OptionType<Integer> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.monad().flatMap((String v) ->OptionInstances.unit().unit(v.length()), h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        OptionType<String> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        OptionType<String> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.none()));
    }
    
    @Test
    public void monadPlus(){
        OptionType<Integer> opt = OptionInstances.<Integer>monadPlus()
                                      .plus(OptionType.widen(Option.none()), OptionType.widen(Option.some(10)))
                                      .convert(OptionType::narrowK);
        assertThat(opt,equalTo(Option.some(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<OptionType<Integer>> m = Monoid.of(OptionType.widen(Option.none()), (a,b)->a.isSome() ? b : a);
        OptionType<Integer> opt = OptionInstances.<Integer>monadPlus(m)
                                      .plus(OptionType.widen(Option.some(5)), OptionType.widen(Option.some(10)))
                                      .convert(OptionType::narrowK);
        assertThat(opt,equalTo(Option.some(10)));
    }
    @Test
    public void  foldLeft(){
        int sum  = OptionInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, OptionType.widen(Option.some(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = OptionInstances.foldable()
                        .foldRight(0, (a,b)->a+b, OptionType.widen(Option.some(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<OptionType.µ, Integer>> res = OptionInstances.traverse()
                                                             .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), OptionType.of(1))
                                                             .convert(MaybeType::narrowK);
       
       
       assertThat(res,equalTo(Maybe.just(Option.some(2))));
    }
    
}
