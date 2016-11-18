package com.aol.cyclops.guava.hkt.typeclasses.instances;
import static com.aol.cyclops.guava.hkt.FluentIterableType.widen;
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
import com.aol.cyclops.guava.hkt.FluentIterableType;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.util.function.Lambda;
import com.google.common.collect.FluentIterable;

public class FluentIterablesTest {

    @Test
    public void unit(){
        
        FluentIterableType<String> list = FluentIterableInstances.unit()
                                     .unit("hello")
                                     .convert(FluentIterableType::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        FluentIterableType<Integer> list = FluentIterableInstances.unit()
                                     .unit("hello")
                                     .then(h->FluentIterableInstances.functor().map((String v) ->v.length(), h))
                                     .convert(FluentIterableType::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        FluentIterableInstances.zippingApplicative()
            .ap(widen(FluentIterable.of(l1(this::multiplyByTwo))),widen(FluentIterable.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        FluentIterableType<Function<Integer,Integer>> listFn =FluentIterableInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(FluentIterableType::narrowK);
        
        FluentIterableType<Integer> list = FluentIterableInstances.unit()
                                     .unit("hello")
                                     .then(h->FluentIterableInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->FluentIterableInstances.zippingApplicative().ap(listFn, h))
                                     .convert(FluentIterableType::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       FluentIterableType<Integer> list  = FluentIterableInstances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(FluentIterable.of(1,2,3)))
                                      .convert(FluentIterableType::narrowK);
    }
    @Test
    public void monad(){
        
        FluentIterableType<Integer> list = FluentIterableInstances.unit()
                                     .unit("hello")
                                     .then(h->FluentIterableInstances.monad().flatMap((String v) ->FluentIterableInstances.unit().unit(v.length()), h))
                                     .convert(FluentIterableType::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        FluentIterableType<String> list = FluentIterableInstances.unit()
                                     .unit("hello")
                                     .then(h->FluentIterableInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FluentIterableType::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        FluentIterableType<String> list = FluentIterableInstances.unit()
                                     .unit("hello")
                                     .then(h->FluentIterableInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(FluentIterableType::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList()));
    }
    
    @Test
    public void monadPlus(){
        FluentIterableType<Integer> list = FluentIterableInstances.<Integer>monadPlus()
                                      .plus(FluentIterableType.widen(FluentIterable.of()), FluentIterableType.widen(FluentIterable.of(10)))
                                      .convert(FluentIterableType::narrowK);
        assertThat(list.toList(),equalTo(Arrays.asList(10)));
    }
/**
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<FluentIterableType<Integer>> m = Monoid.of(FluentIterableType.widen(FluentIterable.empty()), (a,b)->a.isEmpty() ? b : a);
        FluentIterableType<Integer> list = FluentIterableInstances.<Integer>monadPlus(m)
                                      .plus(FluentIterableType.widen(FluentIterable.of(5)), FluentIterableType.widen(FluentIterable.of(10)))
                                      .convert(FluentIterableType::narrowK);
        assertThat(list,equalTo(Arrays.asList(5)));
    }
**/
    @Test
    public void  foldLeft(){
        int sum  = FluentIterableInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, FluentIterableType.widen(FluentIterable.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = FluentIterableInstances.foldable()
                        .foldRight(0, (a,b)->a+b, FluentIterableType.widen(FluentIterable.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<FluentIterableType.µ, Integer>> res = FluentIterableInstances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), FluentIterableType.just(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(i->i.convert(FluentIterableType::narrowK).toList()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }
    
}
