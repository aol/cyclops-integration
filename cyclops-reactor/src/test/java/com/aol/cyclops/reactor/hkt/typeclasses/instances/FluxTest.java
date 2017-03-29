package com.aol.cyclops.reactor.hkt.typeclasses.instances;
import static com.aol.cyclops.reactor.hkt.FluxType.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.aol.cyclops2.hkt.Higher;
import cyclops.collections.ListX;
import cyclops.control.Maybe;
import cyclops.function.Fn1;
import cyclops.function.Lambda;
import cyclops.stream.ReactiveSeq;
import org.junit.Test;

import com.aol.cyclops.reactor.hkt.FluxType;


import reactor.core.publisher.Flux;

public class FluxTest {

    @Test
    public void unit(){
        
        FluxType<String> list = FluxInstances.unit()
                                     .unit("hello")
                                     .convert(FluxType::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        FluxType<Integer> list = FluxInstances.unit()
                                     .unit("hello")
                                     .transform(h->FluxInstances.functor().map((String v) ->v.length(), h))
                                     .convert(FluxType::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        FluxInstances.zippingApplicative()
            .ap(widen(Flux.just(l1(this::multiplyByTwo))),widen(Flux.just(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        FluxType<Fn1<Integer, Integer>> listFn = FluxInstances.unit()
                                                              .unit(Lambda.l1((Integer i) -> i * 2))
                                                              .convert(FluxType::narrowK);
        
        FluxType<Integer> list = FluxInstances.unit()
                                     .unit("hello")
                                     .transform(h->FluxInstances.functor().map((String v) ->v.length(), h))
                                     .transform(h->FluxInstances.zippingApplicative().ap(listFn, h))
                                     .convert(FluxType::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       FluxType<Integer> list  = FluxInstances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(Flux.just(1,2,3)))
                                      .convert(FluxType::narrowK);
    }
    @Test
    public void monad(){
        
        FluxType<Integer> list = FluxInstances.unit()
                                     .unit("hello")
                                     .transform(h->FluxInstances.monad().flatMap((String v) ->FluxInstances.unit().unit(v.length()), h))
                                     .convert(FluxType::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        FluxType<String> list = FluxInstances.unit()
                                     .unit("hello")
                                     .transform(h->FluxInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FluxType::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        FluxType<String> list = FluxInstances.unit()
                                     .unit("hello")
                                     .transform(h->FluxInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(FluxType::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList()));
    }
    
    @Test
    public void monadPlus(){
        FluxType<Integer> list = FluxInstances.<Integer>monadPlus()
                                      .plus(FluxType.widen(Flux.empty()), FluxType.widen(Flux.just(10)))
                                      .convert(FluxType::narrowK);
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList(10)));
    }
/**
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<FluxType<Integer>> m = Monoid.of(FluxType.widen(Flux.empty()), (a,b)->a.isEmpty() ? b : a);
        FluxType<Integer> list = FluxInstances.<Integer>monadPlus(m)
                                      .plus(FluxType.widen(Flux.of(5)), FluxType.widen(Flux.of(10)))
                                      .convert(FluxType::narrowK);
        assertThat(list,equalTo(Arrays.asList(5)));
    }
**/
    @Test
    public void  foldLeft(){
        int sum  = FluxInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, FluxType.widen(Flux.just(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = FluxInstances.foldable()
                        .foldRight(0, (a,b)->a+b, FluxType.widen(Flux.just(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){

       Maybe<Higher<FluxType.µ, Integer>> res = FluxInstances.traverse()
                                                         .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), FluxType.just(1,2,3))
                                                         .convert(Maybe::narrowK);
       
       
       assertThat(res.map(i->i.convert(FluxType::narrowK).collect(Collectors.toList()).block()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }
    
}
