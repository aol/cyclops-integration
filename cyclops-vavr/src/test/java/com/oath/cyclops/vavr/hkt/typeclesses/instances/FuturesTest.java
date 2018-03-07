package com.oath.cyclops.vavr.hkt.typeclesses.instances;

import static com.oath.cyclops.vavr.hkt.FutureKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import cyclops.companion.vavr.Futures;
import cyclops.monads.VavrWitness.future;
import org.junit.Test;

import com.oath.cyclops.hkt.Higher;
import cyclops.control.Maybe;
import cyclops.function.Function1;
import cyclops.function.Lambda;
import cyclops.function.Monoid;

import io.vavr.concurrent.Future;

public class FuturesTest {

    @Test
    public void unit(){

        FutureKind<String> opt = Futures.Instances.unit()
                                            .unit("hello")
                                            .convert(FutureKind::narrowK);

        assertThat(opt.get(),equalTo(Future.successful("hello").get()));
    }
    @Test
    public void functor(){

        FutureKind<Integer> opt = Futures.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Futures.Instances.functor().map((String v) ->v.length(), h))
                                     .convert(FutureKind::narrowK);

        assertThat(opt.get(),equalTo(Future.successful("hello".length()).get()));
    }
    @Test
    public void apSimple(){
        Futures.Instances.applicative()
            .ap(widen(Future.successful(l1(this::multiplyByTwo))),widen(Future.successful(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        FutureKind<Function1<Integer,Integer>> optFn = Futures.Instances
                                                        .unit()
                                                        .unit(Lambda.l1((Integer i) ->i*2))
                                                        .convert(FutureKind::narrowK);

        FutureKind<Integer> opt = Futures.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Futures.Instances.functor().map((String v) ->v.length(), h))
                                     .applyHKT(h-> Futures.Instances.applicative().ap(optFn, h))
                                     .convert(FutureKind::narrowK);

        assertThat(opt.get(),equalTo(Future.successful("hello".length()*2).get()));
    }
    @Test
    public void monadSimple(){
       FutureKind<Integer> opt  = Futures.Instances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Future.successful(i*2)), widen(Future.successful(3)))
                                            .convert(FutureKind::narrowK);
    }
    @Test
    public void monad(){

        FutureKind<Integer> opt = Futures.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Futures.Instances.monad().flatMap((String v) -> Futures.Instances.unit().unit(v.length()), h))
                                     .convert(FutureKind::narrowK);

        assertThat(opt.get(),equalTo(Future.successful("hello".length()).get()));
    }
    @Test
    public void monadZeroFilter(){

        FutureKind<String> opt = Futures.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Futures.Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FutureKind::narrowK);

        assertThat(opt.get(),equalTo(Future.successful("hello").get()));
    }
    @Test
    public void monadZeroFilterOut(){

        FutureKind<String> opt = Futures.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Futures.Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(FutureKind::narrowK);

        assertFalse(opt.isCompleted());
    }

    @Test
    public void monadPlus(){
        FutureKind<Integer> opt = Futures.Instances.<Integer>monadPlus()
                                      .plus(FutureKind.widen(cyclops.async.Future.future()), FutureKind.widen(Future.successful(10)))
                                      .convert(FutureKind::narrowK);
        assertThat(opt.get(),equalTo(Future.successful(10).get()));
    }
    @Test
    public void monadPlusNonEmpty(){

        Monoid<FutureKind<Integer>> m = Monoid.of(FutureKind.widen(cyclops.async.Future.future()), (a, b)->a.isCompleted() ? b : a);
        FutureKind<Integer> opt = Futures.Instances.<Integer>monadPlusK(m)
                                      .plus(FutureKind.widen(Future.successful(5)), FutureKind.widen(Future.successful(10)))
                                      .convert(FutureKind::narrowK);
        assertThat(opt.get(),equalTo(Future.successful(10).get()));
    }
    @Test
    public void  foldLeft(){
        int sum  = Futures.Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, FutureKind.widen(Future.successful(4)));

        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = Futures.Instances.foldable()
                        .foldRight(0, (a,b)->a+b, FutureKind.widen(Future.successful(1)));

        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       Maybe<Higher<future, Integer>> res = Futures.Instances.traverse()
                                                                 .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), FutureKind.successful(1))
                                                                 .convert(Maybe::narrowK);


       assertThat(res.map(h->h.convert(FutureKind::narrowK).get()),
                  equalTo(Maybe.just(Future.successful(2).get())));
    }

}
