package com.aol.cyclops.guava.hkt.typeclasses.instances;
import static com.aol.cyclops.guava.hkt.FluentIterableKind.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.function.Function;

import com.aol.cyclops.guava.hkt.FluentIterableKind;
import org.junit.Test;

import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.util.function.Lambda;
import com.google.common.collect.FluentIterable;

public class FluentIterablesTest {

    @Test
    public void unit(){
        
        FluentIterableKind<String> list = Instances.unit()
                                     .unit("hello")
                                     .convert(FluentIterableKind::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        FluentIterableKind<Integer> list = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .convert(FluentIterableKind::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        Instances.zippingApplicative()
            .ap(widen(FluentIterable.of(l1(this::multiplyByTwo))),widen(FluentIterable.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        FluentIterableKind<Function<Integer,Integer>> listFn = Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(FluentIterableKind::narrowK);
        
        FluentIterableKind<Integer> list = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .then(h-> Instances.zippingApplicative().ap(listFn, h))
                                     .convert(FluentIterableKind::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       FluentIterableKind<Integer> list  = Instances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(FluentIterable.of(1,2,3)))
                                      .convert(FluentIterableKind::narrowK);
    }
    @Test
    public void monad(){
        
        FluentIterableKind<Integer> list = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.monad().flatMap((String v) -> Instances.unit().unit(v.length()), h))
                                     .convert(FluentIterableKind::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        FluentIterableKind<String> list = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(FluentIterableKind::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        FluentIterableKind<String> list = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(FluentIterableKind::narrowK);
        
        assertThat(list.toList(),equalTo(Arrays.asList()));
    }
    
    @Test
    public void monadPlus(){
        FluentIterableKind<Integer> list = Instances.<Integer>monadPlus()
                                      .plus(FluentIterableKind.widen(FluentIterable.of()), FluentIterableKind.widen(FluentIterable.of(10)))
                                      .convert(FluentIterableKind::narrowK);
        assertThat(list.toList(),equalTo(Arrays.asList(10)));
    }
/**
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<FluentIterableKind<Integer>> m = Monoid.of(FluentIterableKind.widen(FluentIterable.empty()), (a,b)->a.isEmpty() ? b : a);
        FluentIterableKind<Integer> list = Instances.<Integer>monadPlus(m)
                                      .plus(FluentIterableKind.widen(FluentIterable.of(5)), FluentIterableKind.widen(FluentIterable.of(10)))
                                      .convert(FluentIterableKind::narrowK);
        assertThat(list,equalTo(Arrays.asList(5)));
    }
**/
    @Test
    public void  foldLeft(){
        int sum  = Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, FluentIterableKind.widen(FluentIterable.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Instances.foldable()
                        .foldRight(0, (a,b)->a+b, FluentIterableKind.widen(FluentIterable.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<FluentIterableKind.µ, Integer>> res = Instances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), FluentIterableKind.just(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(i->i.convert(FluentIterableKind::narrowK).toList()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }
    
}
