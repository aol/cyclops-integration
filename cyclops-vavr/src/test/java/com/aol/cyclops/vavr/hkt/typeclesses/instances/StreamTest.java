package com.aol.cyclops.vavr.hkt.typeclesses.instances;
import static com.aol.cyclops.vavr.hkt.StreamKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.collections.mutable.ListX;
import cyclops.companion.vavr.Streams;
import com.aol.cyclops.vavr.hkt.StreamKind;

import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.stream;
import cyclops.reactive.ReactiveSeq;
import cyclops.typeclasses.Pure;
import cyclops.typeclasses.functor.Functor;
import org.junit.Test;

import com.oath.cyclops.hkt.Higher;
import cyclops.control.Maybe;
import cyclops.function.Fn1;
import cyclops.function.Lambda;

import io.vavr.collection.Stream;

public class StreamTest {

    @Test
    public void unit(){

        StreamKind<String> list = Streams.Instances.unit()
                                     .unit("hello")
                                     .convert(StreamKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        Pure<stream> unit = Streams.Instances.unit();
        StreamKind<Integer> list = Streams.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Streams.Instances.functor().map((String v) ->v.length(), h))
                                     .convert(StreamKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void functor2(){

        Pure<stream> pure = Streams.Instances.unit();
        Functor<stream> functor = Streams.Instances.functor();

        StreamKind<Integer> list = pure.unit("hello")
                                       .applyHKT(h->functor.map((String v) ->v.length(), h))
                                       .convert(StreamKind::narrowK);


        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        Streams.Instances.zippingApplicative()
            .ap(widen(Stream.of(l1(this::multiplyByTwo))),widen(Stream.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        StreamKind<Fn1<Integer,Integer>> listFn =Streams.Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(StreamKind::narrowK);

        StreamKind<Integer> list = Streams.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Streams.Instances.functor().map((String v) ->v.length(), h))
                                     .applyHKT(h->Streams.Instances.zippingApplicative().ap(listFn, h))
                                     .convert(StreamKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       StreamKind<Integer> list  = Streams.Instances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(Stream.of(1,2,3)))
                                      .convert(StreamKind::narrowK);
    }
    @Test
    public void monad(){

        StreamKind<Integer> list = Streams.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Streams.Instances.monad().flatMap((String v) ->Streams.Instances.unit().unit(v.length()), h))
                                     .convert(StreamKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){

        StreamKind<String> list = Streams.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Streams.Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(StreamKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){

        StreamKind<String> list = Streams.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Streams.Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(StreamKind::narrowK);

        assertThat(list.toJavaList(),equalTo(Arrays.asList()));
    }

    @Test
    public void monadPlus(){
        StreamKind<Integer> list = Streams.Instances.<Integer>monadPlus()
                                      .plus(StreamKind.widen(Stream.empty()), StreamKind.widen(Stream.of(10)))
                                      .convert(StreamKind::narrowK);
        assertThat(list.toJavaList(),equalTo(Arrays.asList(10)));
    }

    @Test
    public void  foldLeft(){
        int sum  = Streams.Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, StreamKind.widen(Stream.of(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Streams.Instances.foldable()
                        .foldRight(0, (a,b)->a+b, StreamKind.widen(Stream.of(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       Maybe<Higher<stream, Integer>> res = Streams.Instances.traverse()
                                                         .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), StreamKind.just(1,2,3))
                                                         .convert(Maybe::narrowK);


       assertThat(res.map(i->i.convert(StreamKind::narrowK).toJavaList()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }

}
