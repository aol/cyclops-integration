package com.aol.cyclops.vavr.hkt.typeclesses.instances;
import static com.aol.cyclops.vavr.hkt.OptionKind.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.OptionKind;
import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.vavr.hkt.typeclasses.instances.OptionInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.control.Option;

public class OptionsTest {

    @Test
    public void unit(){
        
        OptionKind<String> opt = OptionInstances.unit()
                                            .unit("hello")
                                            .convert(OptionKind::narrowK);
        
        assertThat(opt,equalTo(Option.of("hello")));
    }
    @Test
    public void functor(){
        
        OptionKind<Integer> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.functor().map((String v) ->v.length(), h))
                                     .convert(OptionKind::narrowK);
        
        assertThat(opt,equalTo(Option.of("hello".length())));
    }
    @Test
    public void apSimple(){
        OptionInstances.applicative()
            .ap(widen(Option.of(l1(this::multiplyByTwo))),widen(Option.of(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        OptionKind<Function<Integer,Integer>> optFn =OptionInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(OptionKind::narrowK);
        
        OptionKind<Integer> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->OptionInstances.applicative().ap(optFn, h))
                                     .convert(OptionKind::narrowK);
        
        assertThat(opt,equalTo(Option.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       OptionKind<Integer> opt  = OptionInstances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Option.of(i*2)), widen(Option.of(3)))
                                            .convert(OptionKind::narrowK);
    }
    @Test
    public void monad(){
        
        OptionKind<Integer> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.monad().flatMap((String v) ->OptionInstances.unit().unit(v.length()), h))
                                     .convert(OptionKind::narrowK);
        
        assertThat(opt,equalTo(Option.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        OptionKind<String> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(OptionKind::narrowK);
        
        assertThat(opt,equalTo(Option.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        OptionKind<String> opt = OptionInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(OptionKind::narrowK);
        
        assertThat(opt,equalTo(Option.none()));
    }
    
    @Test
    public void monadPlus(){
        OptionKind<Integer> opt = OptionInstances.<Integer>monadPlus()
                                      .plus(OptionKind.widen(Option.none()), OptionKind.widen(Option.of(10)))
                                      .convert(OptionKind::narrowK);
        assertThat(opt,equalTo(Option.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<OptionKind<Integer>> m = Monoid.of(OptionKind.widen(Option.none()), (a, b)->a.isDefined() ? b : a);
        OptionKind<Integer> opt = OptionInstances.<Integer>monadPlus(m)
                                      .plus(OptionKind.widen(Option.of(5)), OptionKind.widen(Option.of(10)))
                                      .convert(OptionKind::narrowK);
        assertThat(opt,equalTo(Option.of(10)));
    }
    @Test
    public void  foldLeft(){
        int sum  = OptionInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, OptionKind.widen(Option.of(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = OptionInstances.foldable()
                        .foldRight(0, (a,b)->a+b, OptionKind.widen(Option.of(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<OptionKind.µ, Integer>> res = OptionInstances.traverse()
                                                                 .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), OptionKind.of(1))
                                                                 .convert(MaybeType::narrowK);
       
       
       assertThat(res,equalTo(Maybe.just(Option.of(2))));
    }
    
}
