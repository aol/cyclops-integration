package com.aol.cyclops.vavr.hkt.typeclesses.instances;
import static com.aol.cyclops.vavr.hkt.LazyKind.widen;

import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.companion.vavr.Lazys;
import com.aol.cyclops.vavr.hkt.LazyKind;
import com.oath.cyclops.hkt.Higher;
import cyclops.companion.vavr.Lazys.Instances;
import cyclops.control.Maybe;
import cyclops.function.Function1;
import cyclops.function.Monoid;
import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.lazy;
import org.junit.Test;



import io.vavr.Lazy;

public class LazyInstancesTest {

    @Test
    public void unit(){

        LazyKind<String> opt = Instances.unit()
                                            .unit("hello")
                                            .convert(LazyKind::narrowK);

        assertThat(opt,equalTo(Lazy.of(()->"hello")));
    }
    @Test
    public void functor(){
        Higher<lazy,Integer> res =null;
        LazyKind<Integer> x = LazyKind.narrowK(res);
        LazyKind<Integer> opt = Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .convert(r-> LazyKind.narrowK(r));

        assertThat(opt,equalTo(Lazy.of(()->"hello".length())));
    }
    @Test
    public void apSimple(){
        Instances.applicative()
            .ap(widen(Lazy.of(()-> l1(this::multiplyByTwo))),widen(Lazy.of(()->1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        LazyKind<Function1<Integer,Integer>> optFn = Instances.unit()
                                                                  .unit(l1((Integer i) ->i*2))
                                                                  .convert(LazyKind::narrowK);

        LazyKind<Integer> opt = Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .applyHKT(h-> Instances.applicative().ap(optFn, h))
                                     .convert(LazyKind::narrowK);

        assertThat(opt,equalTo(Lazy.of(()->"hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       LazyKind<Integer> opt  = Instances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Lazy.of(()->i*2)), widen(Lazy.of(()->3)))
                                            .convert(LazyKind::narrowK);
    }
    @Test
    public void monad(){

        LazyKind<Integer> opt = Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Instances.monad().flatMap((String v) -> Instances.unit().unit(v.length()), h))
                                     .convert(LazyKind::narrowK);

        assertThat(opt,equalTo(Lazy.of(()->"hello".length())));
    }
    @Test
    public void monadZeroFilter(){

        LazyKind<String> opt = Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(LazyKind::narrowK);

        assertThat(opt,equalTo(Lazy.of(()->"hello")));
    }
    @Test
    public void monadZeroFilterOut(){

        LazyKind<String> opt = Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(LazyKind::narrowK);

        assertThat(opt,equalTo(Lazy.of(()->null)));
    }

    @Test
    public void monadPlus(){
        LazyKind<Integer> opt = Instances.<Integer>monadPlus()
                                      .plus(LazyKind.widen(Lazy.of(()->null)), LazyKind.widen(Lazy.of(()->10)))
                                      .convert(LazyKind::narrowK);
        assertThat(opt,equalTo(Lazy.of(()->10)));
    }
    @Test
    public void monadPlusNonEmpty(){

        Monoid<LazyKind<Integer>> m = Monoid.of(LazyKind.widen(Lazy.of(()->null)), (a, b)->a.get()==null ? b : a);
        LazyKind<Integer> opt = Instances.<Integer>monadPlusK(m)
                                      .plus(LazyKind.widen(Lazy.of(()->5)), LazyKind.widen(Lazy.of(()->10)))
                                      .convert(LazyKind::narrowK);
        assertThat(opt,equalTo(Lazy.of(()->5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, LazyKind.widen(Lazy.of(()->4)));

        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = Instances.foldable()
                        .foldRight(0, (a,b)->a+b, LazyKind.widen(Lazy.of(()->1)));

        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       Maybe<Higher<lazy, Integer>> res = Instances.traverse()
                                                                   .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), LazyKind.of(()->1))
                                                                 .convert(Maybe::narrowK);


       assertThat(res,equalTo(Maybe.just(Lazy.of(()->2))));
    }

}
