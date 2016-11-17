package com.aol.cyclops.javaslang.hkt.typeclesses.instances;
import static com.aol.cyclops.javaslang.hkt.StreamType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.Maybes;
import com.aol.cyclops.javaslang.hkt.StreamType;
import com.aol.cyclops.javaslang.hkt.typeclasses.instances.StreamInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.collection.Stream;

public class StreamTest {

    @Test
    public void unit(){
        
        StreamType<String> list = StreamInstances.unit()
                                     .unit("hello")
                                     .convert(StreamType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        StreamType<Integer> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.functor().map((String v) ->v.length(), h))
                                     .convert(StreamType::narrowK);
        
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
        
        StreamType<Function<Integer,Integer>> listFn =StreamInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(StreamType::narrowK);
        
        StreamType<Integer> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->StreamInstances.zippingApplicative().ap(listFn, h))
                                     .convert(StreamType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       StreamType<Integer> list  = StreamInstances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(Stream.of(1,2,3)))
                                      .convert(StreamType::narrowK);
    }
    @Test
    public void monad(){
        
        StreamType<Integer> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.monad().flatMap((String v) ->StreamInstances.unit().unit(v.length()), h))
                                     .convert(StreamType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        StreamType<String> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(StreamType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        StreamType<String> list = StreamInstances.unit()
                                     .unit("hello")
                                     .then(h->StreamInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(StreamType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList()));
    }
    
    @Test
    public void monadPlus(){
        StreamType<Integer> list = StreamInstances.<Integer>monadPlus()
                                      .plus(StreamType.widen(Stream.empty()), StreamType.widen(Stream.of(10)))
                                      .convert(StreamType::narrowK);
        assertThat(list.toJavaList(),equalTo(Arrays.asList(10)));
    }
/**
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<StreamType<Integer>> m = Monoid.of(StreamType.widen(Stream.empty()), (a,b)->a.isEmpty() ? b : a);
        StreamType<Integer> list = StreamInstances.<Integer>monadPlus(m)
                                      .plus(StreamType.widen(Stream.of(5)), StreamType.widen(Stream.of(10)))
                                      .convert(StreamType::narrowK);
        assertThat(list,equalTo(Arrays.asList(5)));
    }
**/
    @Test
    public void  foldLeft(){
        int sum  = StreamInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, StreamType.widen(Stream.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = StreamInstances.foldable()
                        .foldRight(0, (a,b)->a+b, StreamType.widen(Stream.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<StreamType.µ, Integer>> res = StreamInstances.traverse()
                                                         .traverseA(Maybes.applicative(), (Integer a)->MaybeType.just(a*2), StreamType.just(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(i->i.convert(StreamType::narrowK).toJavaList()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }
    
}
