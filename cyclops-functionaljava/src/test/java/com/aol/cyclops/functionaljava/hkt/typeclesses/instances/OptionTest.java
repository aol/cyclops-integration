package com.aol.cyclops.functionaljava.hkt.typeclesses.instances;
import static com.aol.cyclops.functionaljava.hkt.OptionKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.companion.functionaljava.Options;
import com.aol.cyclops.functionaljava.hkt.OptionKind;
import cyclops.monads.FJWitness;
import cyclops.monads.FJWitness.option;
import org.junit.Test;
import com.oath.cyclops.hkt.Higher;
import cyclops.control.Maybe;
import cyclops.function.Fn1;
import cyclops.function.Lambda;
import cyclops.function.Monoid;


import fj.data.Option;

public class OptionTest {

    @Test
    public void unit(){

        OptionKind<String> opt = Options.Instances.unit()
                                            .unit("hello")
                                            .convert(OptionKind::narrowK);

        assertThat(opt,equalTo(Option.some("hello")));
    }
    @Test
    public void functor(){

        OptionKind<Integer> opt = Options.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Options.Instances.functor().map((String v) ->v.length(), h))
                                     .convert(OptionKind::narrowK);

        assertThat(opt,equalTo(Option.some("hello".length())));
    }
    @Test
    public void apSimple(){
        Options.Instances.applicative()
            .ap(widen(Option.some(l1(this::multiplyByTwo))),widen(Option.some(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        OptionKind<Fn1<Integer,Integer>> optFn = Options.Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(OptionKind::narrowK);

        OptionKind<Integer> opt = Options.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Options.Instances.functor().map((String v) ->v.length(), h))
                                     .applyHKT(h-> Options.Instances.applicative().ap(optFn, h))
                                     .convert(OptionKind::narrowK);

        assertThat(opt,equalTo(Option.some("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       OptionKind<Integer> opt  = Options.Instances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Option.some(i*2)), widen(Option.some(3)))
                                            .convert(OptionKind::narrowK);
    }
    @Test
    public void monad(){

        OptionKind<Integer> opt = Options.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Options.Instances.monad().flatMap((String v) -> Options.Instances.unit().unit(v.length()), h))
                                     .convert(OptionKind::narrowK);

        assertThat(opt,equalTo(Option.some("hello".length())));
    }
    @Test
    public void monadZeroFilter(){

        OptionKind<String> opt = Options.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Options.Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(OptionKind::narrowK);

        assertThat(opt,equalTo(Option.some("hello")));
    }
    @Test
    public void monadZeroFilterOut(){

        OptionKind<String> opt = Options.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Options.Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(OptionKind::narrowK);

        assertThat(opt,equalTo(Option.none()));
    }

    @Test
    public void monadPlus(){
        OptionKind<Integer> opt = Options.Instances.<Integer>monadPlus()
                                      .plus(OptionKind.widen(Option.none()), OptionKind.widen(Option.some(10)))
                                      .convert(OptionKind::narrowK);
        assertThat(opt,equalTo(Option.some(10)));
    }
    @Test
    public void monadPlusNonEmpty(){

        Monoid<OptionKind<Integer>> m = Monoid.of(OptionKind.widen(Option.none()), (a, b)->a.isSome() ? b : a);
        OptionKind<Integer> opt = Options.Instances.<Integer>monadPlusK(m)
                                      .plus(OptionKind.widen(Option.some(5)), OptionKind.widen(Option.some(10)))
                                      .convert(OptionKind::narrowK);
        assertThat(opt,equalTo(Option.some(10)));
    }
    @Test
    public void  foldLeft(){
        int sum  = Options.Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, OptionKind.widen(Option.some(4)));

        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = Options.Instances.foldable()
                        .foldRight(0, (a,b)->a+b, OptionKind.widen(Option.some(1)));

        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       Maybe<Higher<option, Integer>> res = Options.Instances.traverse()
                                                             .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), OptionKind.of(1))
                                                             .convert(Maybe::narrowK);


       assertThat(res,equalTo(Maybe.just(Option.some(2))));
    }

}
