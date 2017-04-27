package com.aol.cyclops.reactor.hkt.typeclasses.instances;
import static com.aol.cyclops.reactor.hkt.FluxKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.aol.cyclops.reactor.hkt.FluxKind;
import com.aol.cyclops2.hkt.Higher;
import cyclops.collections.ListX;
import cyclops.control.Maybe;
import cyclops.function.Fn1;
import cyclops.function.Lambda;
import cyclops.stream.ReactiveSeq;
import org.junit.Test;


import reactor.core.publisher.Flux;

public class FluxTest {

    @Test
    public void unit(){
        
        FluxKind<String> list = Instances.unit()
                                     .unit("hello")
                                     .convert(FluxKind::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        FluxKind<Integer> list = Instances.unit()
                                     .unit("hello")
                                     .transform(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .convert(FluxKind::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        Instances.zippingApplicative()
            .ap(widen(Flux.just(l1(this::multiplyByTwo))),widen(Flux.just(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        FluxKind<Fn1<Integer, Integer>> listFn = Instances.unit()
                                                              .unit(Lambda.l1((Integer i) -> i * 2))
                                                              .convert(FluxKind::narrowK);
        
        FluxKind<Integer> list = Instances.unit()
                                     .unit("hello")
                                     .transform(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .transform(h-> Instances.zippingApplicative().ap(listFn, h))
                                     .convert(FluxKind::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       FluxKind<Integer> list  = Instances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(Flux.just(1,2,3)))
                                      .convert(FluxKind::narrowK);
    }
    @Test
    public void monad(){
        
        FluxKind<Integer> list = Instances.unit()
                                     .unit("hello")
                                     .transform(h-> Instances.monad().flatMap((String v) -> Instances.unit().unit(v.length()), h))
                                     .convert(FluxKind::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        FluxKind<String> list = Instances.unit()
                                     .unit("hello")
                                     .transform(h-> Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FluxKind::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        FluxKind<String> list = Instances.unit()
                                     .unit("hello")
                                     .transform(h-> Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(FluxKind::narrowK);
        
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList()));
    }
    
    @Test
    public void monadPlus(){
        FluxKind<Integer> list = Instances.<Integer>monadPlus()
                                      .plus(FluxKind.widen(Flux.empty()), FluxKind.widen(Flux.just(10)))
                                      .convert(FluxKind::narrowK);
        assertThat(list.collect(Collectors.toList()).block(),equalTo(Arrays.asList(10)));
    }
/**
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<FluxKind<Integer>> m = Monoid.of(FluxKind.widen(Flux.empty()), (a,b)->a.isEmpty() ? b : a);
        FluxKind<Integer> list = Instances.<Integer>monadPlus(m)
                                      .plus(FluxKind.widen(Flux.of(5)), FluxKind.widen(Flux.of(10)))
                                      .convert(FluxKind::narrowK);
        assertThat(list,equalTo(Arrays.asList(5)));
    }
**/
    @Test
    public void  foldLeft(){
        int sum  = Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, FluxKind.widen(Flux.just(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Instances.foldable()
                        .foldRight(0, (a,b)->a+b, FluxKind.widen(Flux.just(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){

       Maybe<Higher<FluxKind.µ, Integer>> res = Instances.traverse()
                                                         .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), FluxKind.just(1,2,3))
                                                         .convert(Maybe::narrowK);
       
       
       assertThat(res.map(i->i.convert(FluxKind::narrowK).collect(Collectors.toList()).block()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }
    
}
