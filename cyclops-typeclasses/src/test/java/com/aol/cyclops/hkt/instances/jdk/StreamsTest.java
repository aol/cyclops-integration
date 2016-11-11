package com.aol.cyclops.hkt.instances.jdk;
import static com.aol.cyclops.hkt.jdk.StreamType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.Maybes;
import com.aol.cyclops.hkt.jdk.ListType;
import com.aol.cyclops.hkt.jdk.StreamType;
import com.aol.cyclops.util.function.Lambda;

public class StreamsTest {

    @Test
    public void unit(){
        
        StreamType<String> list = Streams.unit()
                                     .unit("hello")
                                     .convert(StreamType::narrowK);
        
        assertThat(list.collect(Collectors.toList()),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        StreamType<Integer> list = Streams.unit()
                                     .unit("hello")
                                     .then(h->Streams.functor().map((String v) ->v.length(), h))
                                     .convert(StreamType::narrowK);
        
        assertThat(list.collect(Collectors.toList()),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        Streams.zippingApplicative()
            .ap(widen(Stream.of(l1(this::multiplyByTwo))),widen(Stream.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        StreamType<Function<Integer,Integer>> listFn =Streams.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(StreamType::narrowK);
        
        StreamType<Integer> list = Streams.unit()
                                     .unit("hello")
                                     .then(h->Streams.functor().map((String v) ->v.length(), h))
                                     .then(h->Streams.zippingApplicative().ap(listFn, h))
                                     .convert(StreamType::narrowK);
        
        assertThat(list.collect(Collectors.toList()),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       StreamType<Integer> list  = Streams.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(Stream.of(1,2,3)))
                                      .convert(StreamType::narrowK);
    }
    @Test
    public void monad(){
        
        StreamType<Integer> list = Streams.unit()
                                     .unit("hello")
                                     .then(h->Streams.monad().flatMap((String v) ->Streams.unit().unit(v.length()), h))
                                     .convert(StreamType::narrowK);
        
        assertThat(list.collect(Collectors.toList()),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        StreamType<String> list = Streams.unit()
                                     .unit("hello")
                                     .then(h->Streams.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(StreamType::narrowK);
        
        assertThat(list.collect(Collectors.toList()),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        StreamType<String> list = Streams.unit()
                                     .unit("hello")
                                     .then(h->Streams.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(StreamType::narrowK);
        
        assertThat(list.collect(Collectors.toList()),equalTo(Arrays.asList()));
    }
    
    @Test
    public void monadPlus(){
        StreamType<Integer> list = Streams.<Integer>monadPlus()
                                      .plus(StreamType.widen(Stream.of()), StreamType.widen(Stream.of(10)))
                                      .convert(StreamType::narrowK);
        assertThat(list.collect(Collectors.toList()),equalTo(Arrays.asList(10)));
    }
    /**
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<StreamType<Integer>> m = Monoid.of(StreamType.widen(Stream.of()), (a,b)->a.isEmpty() ? b : a);
        StreamType<Integer> list = Streams.<Integer>monadPlus(m)
                                      .plus(StreamType.widen(Stream.of(5)), StreamType.widen(Stream.of(10)))
                                      .convert(StreamType::narrowK);
        assertThat(list,equalTo(Arrays.asList(5)));
    }
**/
    @Test
    public void  foldLeft(){
        int sum  = Streams.foldable()
                        .foldLeft(0, (a,b)->a+b, StreamType.widen(Stream.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Streams.foldable()
                        .foldRight(0, (a,b)->a+b, StreamType.widen(Stream.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<StreamType.µ, Integer>> res = Streams.traverse()
                                                         .traverseA(Maybes.applicative(), (Integer a)->MaybeType.just(a*2), StreamType.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(i->i.convert(StreamType::narrowK).collect(Collectors.toList())),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }
    
}
