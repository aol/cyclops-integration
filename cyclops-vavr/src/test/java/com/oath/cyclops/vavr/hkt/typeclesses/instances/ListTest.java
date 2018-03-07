package com.oath.cyclops.vavr.hkt.typeclesses.instances;
import static com.oath.cyclops.vavr.hkt.ListKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.collections.mutable.ListX;
import cyclops.companion.vavr.Lists;


import cyclops.monads.VavrWitness.list;
import cyclops.reactive.ReactiveSeq;
import org.junit.Test;

import com.oath.cyclops.hkt.Higher;
import cyclops.control.Maybe;
import cyclops.function.Function1;
import cyclops.function.Lambda;
import io.vavr.collection.List;

public class ListTest {

    @Test
    public void unit(){

        ListKind<String> list = Lists.Instances.unit()
                                     .unit("hello")
                                     .convert(ListKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){

        ListKind<Integer> list = Lists.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Lists.Instances.functor().map((String v) ->v.length(), h))
                                     .convert(ListKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        Lists.Instances.zippingApplicative()
            .ap(widen(List.of(l1(this::multiplyByTwo))),widen(List.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        ListKind<Function1<Integer,Integer>> listFn = Lists.Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(ListKind::narrowK);

        ListKind<Integer> list = Lists.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Lists.Instances.functor().map((String v) ->v.length(), h))
                                     .applyHKT(h-> Lists.Instances.zippingApplicative().ap(listFn, h))
                                     .convert(ListKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       ListKind<Integer> list  = Lists.Instances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(List.of(1,2,3)))
                                      .convert(ListKind::narrowK);
    }
    @Test
    public void monad(){

        ListKind<Integer> list = Lists.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Lists.Instances.monad().flatMap((String v) -> Lists.Instances.unit().unit(v.length()), h))
                                     .convert(ListKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){

        ListKind<String> list = Lists.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Lists.Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ListKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){

        ListKind<String> list = Lists.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Lists.Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(ListKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList()));
    }

    @Test
    public void monadPlus(){
        ListKind<Integer> list = Lists.Instances.<Integer>monadPlus()
                                      .plus(ListKind.widen(List.empty()), ListKind.widen(List.of(10)))
                                      .convert(ListKind::narrowK);
        assertThat(list.toJavaList(),equalTo(Arrays.asList(10)));
    }

    @Test
    public void  foldLeft(){
        int sum  = Lists.Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, ListKind.widen(List.of(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Lists.Instances.foldable()
                        .foldRight(0, (a,b)->a+b, ListKind.widen(List.of(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       Maybe<Higher<list, Integer>> res = Lists.Instances.traverse()
                                                         .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), ListKind.just(1,2,3))
                                                         .convert(Maybe::narrowK);


       assertThat(res.map(i->i.convert(ListKind::narrowK).toJavaList()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }

}
