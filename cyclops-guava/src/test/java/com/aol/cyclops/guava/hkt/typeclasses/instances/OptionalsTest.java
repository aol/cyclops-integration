package com.aol.cyclops.guava.hkt.typeclasses.instances;
import static com.aol.cyclops.guava.hkt.OptionalKind.widen;

import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.companion.guava.Optionals;
import com.aol.cyclops.guava.hkt.OptionalKind;
import cyclops.function.Monoid;
import cyclops.monads.GuavaWitness;
import cyclops.monads.GuavaWitness.optional;
import org.junit.Test;

import com.aol.cyclops2.hkt.Higher;
import cyclops.control.Maybe;
import cyclops.function.Fn1;
import cyclops.function.Lambda;

import com.google.common.base.Optional;

public class OptionalsTest {

    @Test
    public void unit(){
        
        OptionalKind<String> opt = Optionals.Instances.unit()
                                            .unit("hello")
                                            .convert(OptionalKind::narrowK);
        
        assertThat(opt,equalTo(Optional.of("hello")));
    }
    @Test
    public void functor(){
        
        OptionalKind<Integer> opt = Optionals.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Optionals.Instances.functor().map((String v) ->v.length(), h))
                                     .convert(OptionalKind::narrowK);
        
        assertThat(opt,equalTo(Optional.of("hello".length())));
    }
    @Test
    public void apSimple(){
        Optionals.Instances.applicative()
            .ap(widen(Optional.of(l1(this::multiplyByTwo))),widen(Optional.of(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        OptionalKind<Fn1<Integer,Integer>> optFn = Optionals.Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(OptionalKind::narrowK);
        
        OptionalKind<Integer> opt = Optionals.Instances.unit()
                                             .unit("hello")
                                             .applyHKT(h-> Optionals.Instances.functor().map((String v) ->v.length(), h))
                                             .applyHKT(h-> Optionals.Instances.applicative().ap(optFn, h))
                                             .convert(OptionalKind::narrowK);
                
        assertThat(opt,equalTo(Optional.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       OptionalKind<Integer> opt  = Optionals.Instances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Optional.of(i*2)), widen(Optional.of(3)))
                                            .convert(OptionalKind::narrowK);
    }
    @Test
    public void monad(){
        
        OptionalKind<Integer> opt = Optionals.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Optionals.Instances.monad().flatMap((String v) -> Optionals.Instances.unit().unit(v.length()), h))
                                     .convert(OptionalKind::narrowK);
        
        assertThat(opt,equalTo(Optional.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        OptionalKind<String> opt = Optionals.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Optionals.Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(OptionalKind::narrowK);
        
        assertThat(opt,equalTo(Optional.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        OptionalKind<String> opt = Optionals.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Optionals.Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(OptionalKind::narrowK);
        
        assertThat(opt,equalTo(Optional.absent()));
    }
    
    @Test
    public void monadPlus(){
        OptionalKind<Integer> opt = Optionals.Instances.<Integer>monadPlus()
                                      .plus(OptionalKind.widen(Optional.absent()), OptionalKind.widen(Optional.of(10)))
                                      .convert(OptionalKind::narrowK);
        assertThat(opt,equalTo(Optional.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<OptionalKind<Integer>> m = Monoid.of(OptionalKind.widen(Optional.absent()), (a, b)->a.isPresent() ? b : a);
        OptionalKind<Integer> opt = Optionals.Instances.<Integer>monadPlus(m)
                                      .plus(OptionalKind.widen(Optional.of(5)), OptionalKind.widen(Optional.of(10)))
                                      .convert(OptionalKind::narrowK);
        assertThat(opt,equalTo(Optional.of(10)));
    }
    @Test
    public void  foldLeft(){
        int sum  = Optionals.Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, OptionalKind.widen(Optional.of(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = Optionals.Instances.foldable()
                        .foldRight(0, (a,b)->a+b, OptionalKind.widen(Optional.of(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       Maybe<Higher<optional, Integer>> res = Optionals.Instances.traverse()
                                                                 .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), OptionalKind.of(1))
                                                                 .convert(Maybe::narrowK);
       
       
       assertThat(res,equalTo(Maybe.just(Optional.of(2))));
    }
    
}
