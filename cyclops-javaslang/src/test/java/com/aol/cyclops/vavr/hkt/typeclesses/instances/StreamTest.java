package com.aol.cyclops.vavr.hkt.typeclesses.instances;
import static com.aol.cyclops.vavr.hkt.StreamKind.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.StreamKind;
import org.junit.Test;

import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.vavr.hkt.typeclasses.instances.StreamInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.collection.Stream;

public class StreamTest {

    @Test
    public void unit(){
        
        StreamKind<String> list = StreamInstances.unit()
                                     .unit("hello")
                                     .convert(StreamKind::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        StreamKind<Integer> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.functor().map((String v) ->v.length(), h))
                                     .convert(StreamKind::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        StreamInstances.zippingApplicative()
            .ap(widen(Stream.of(l1(this::multiplyByTwo))),widen(Stream.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        StreamKind<Function<Integer,Integer>> listFn =StreamInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(StreamKind::narrowK);
        
        StreamKind<Integer> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->StreamInstances.zippingApplicative().ap(listFn, h))
                                     .convert(StreamKind::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       StreamKind<Integer> list  = StreamInstances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(Stream.of(1,2,3)))
                                      .convert(StreamKind::narrowK);
    }
    @Test
    public void monad(){
        
        StreamKind<Integer> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.monad().flatMap((String v) ->StreamInstances.unit().unit(v.length()), h))
                                     .convert(StreamKind::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        StreamKind<String> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(StreamKind::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        StreamKind<String> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(StreamKind::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList()));
    }
    
    @Test
    public void monadPlus(){
        StreamKind<Integer> list = StreamInstances.<Integer>monadPlus()
                                      .plus(StreamKind.widen(Stream.empty()), StreamKind.widen(Stream.of(10)))
                                      .convert(StreamKind::narrowK);
        assertThat(list.toJavaList(),equalTo(Arrays.asList(10)));
    }
/**
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<StreamKind<Integer>> m = Monoid.of(StreamKind.widen(Stream.empty()), (a,b)->a.isEmpty() ? b : a);
        StreamKind<Integer> list = StreamInstances.<Integer>monadPlus(m)
                                      .plus(StreamKind.widen(Stream.of(5)), StreamKind.widen(Stream.of(10)))
                                      .convert(StreamKind::narrowK);
        assertThat(list,equalTo(Arrays.asList(5)));
    }
**/
    @Test
    public void  foldLeft(){
        int sum  = StreamInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, StreamKind.widen(Stream.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = StreamInstances.foldable()
                        .foldRight(0, (a,b)->a+b, StreamKind.widen(Stream.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<StreamKind.µ, Integer>> res = StreamInstances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), StreamKind.just(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(i->i.convert(StreamKind::narrowK).toJavaList()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }
    
}
