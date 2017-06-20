package com.aol.cyclops.rx.hkt.typeclasses.instances;

import com.aol.cyclops.rx2.hkt.SingleKind;
import com.aol.cyclops2.hkt.Higher;
import cyclops.async.Future;

import cyclops.companion.rx2.Singles;
import cyclops.companion.rx2.Singles.Instances;
import cyclops.control.Maybe;
import cyclops.function.Fn1;

import cyclops.function.Monoid;
import cyclops.typeclasses.monad.Applicative;
import cyclops.typeclasses.monad.Traverse;
import io.reactivex.Single;
import org.junit.Test;


import static com.aol.cyclops.rx2.hkt.SingleKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SinglesTest {

    @Test
    public void unit(){
        
        SingleKind<String> opt = Instances.unit()
                                            .unit("hello")
                                            .convert(SingleKind::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(Future.ofResult("hello").join()));
    }
    @Test
    public void functor(){
        
        SingleKind<Integer> opt = Instances.unit()
                                     .unit("hello")
                                     .apply(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .convert(SingleKind::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(Future.ofResult("hello".length()).join()));
    }
    @Test
    public void apSimple(){
        Instances.applicative()
            .ap(widen(Future.ofResult(l1(this::multiplyByTwo))),widen(Future.ofResult(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        SingleKind<Fn1<Integer,Integer>> optFn = Instances.unit().unit(l1((Integer i) ->i*2)).convert(SingleKind::narrowK);
        
        SingleKind<Integer> opt = Instances.unit()
                                     .unit("hello")
                                     .apply(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .apply(h-> Instances.applicative().ap(optFn, h))
                                     .convert(SingleKind::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(Future.ofResult("hello".length()*2).join()));
    }
    @Test
    public void monadSimple(){
       SingleKind<Integer> opt  = Instances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Future.ofResult(i*2)), widen(Future.ofResult(3)))
                                            .convert(SingleKind::narrowK);
    }
    @Test
    public void monad(){
        
        SingleKind<Integer> opt = Instances.unit()
                                     .unit("hello")
                                     .apply(h-> Instances.monad().flatMap((String v) -> Instances.unit().unit(v.length()), h))
                                     .convert(SingleKind::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(Future.ofResult("hello".length()).join()));
    }
    @Test
    public void monadZeroFilter(){
        
        SingleKind<String> opt = Instances.unit()
                                     .unit("hello")
                                     .apply(h-> Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(SingleKind::narrowK);
        
        assertThat(opt.toFuture().join(),equalTo(Future.ofResult("hello").join()));
    }
    @Test
    public void monadZeroFilterOut(){
        
        SingleKind<String> opt = Instances.unit()
                                     .unit("hello")
                                     .apply(h-> Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(SingleKind::narrowK);
        
        assertTrue(opt.blockingGet()==null);
    }
    
    @Test
    public void monadPlus(){
        SingleKind<Integer> opt = Instances.<Integer>monadPlus()
                                      .plus(widen(Single.never()), widen(Single.just(10)))
                                      .convert(SingleKind::narrowK);
        assertTrue(opt.blockingGet()==null);
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<SingleKind<Integer>> m = Monoid.of(widen(Single.never()), (a, b)->a.toFuture().isDone() ? b : a);
        SingleKind<Integer> opt = Instances.<Integer>monadPlus(m)
                                      .plus(widen(Single.just(5)), widen(Single.just(10)))
                                      .convert(SingleKind::narrowK);
        assertThat(opt.blockingGet(),equalTo(10));
    }
    @Test
    public void  foldLeft(){
        int sum  = Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, widen(Future.ofResult(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = Instances.foldable()
                        .foldRight(0, (a,b)->a+b, widen(Future.ofResult(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
        Traverse<SingleKind.µ> traverse = Instances.traverse();
        Applicative<Maybe.µ> applicative = Maybe.Instances.applicative();

        SingleKind<Integer> mono = widen(Single.just(1));

        Higher<Maybe.µ, Higher<SingleKind.µ, Integer>> t = traverse.traverseA(applicative, (Integer a) -> Maybe.just(a * 2), mono);

        Maybe<Higher<SingleKind.µ, Integer>> res = traverse.traverseA(applicative, (Integer a)-> Maybe.just(a*2),mono)
                                                        .convert(Maybe::narrowK);



       assertThat(res.map(h->h.convert(SingleKind::narrowK).blockingGet()),
                  equalTo(Maybe.just(Single.just(2).blockingGet())));
    }
    @Test
    public void sequence(){
        Traverse<SingleKind.µ> traverse = Instances.traverse();
        Applicative<Maybe.µ> applicative = Maybe.Instances.applicative();

        Higher<Maybe.µ, Higher<SingleKind.µ, Integer>> res = traverse.sequenceA(applicative, widen(Single.just(Maybe.just(1))));
        Maybe<Single<Integer>> nk = res.convert(Maybe::narrowK)
                                     .map(h -> h.convert(SingleKind::narrow));

    }
    
}
