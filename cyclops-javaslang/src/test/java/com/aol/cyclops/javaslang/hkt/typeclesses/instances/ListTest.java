package com.aol.cyclops.javaslang.hkt.typeclesses.instances;
import static com.aol.cyclops.javaslang.hkt.ListType.widen;
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
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.javaslang.hkt.ListType;
import com.aol.cyclops.javaslang.hkt.typeclasses.instances.ListInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.collection.List;

public class ListTest {

    @Test
    public void unit(){
        
        ListType<String> list = ListInstances.unit()
                                     .unit("hello")
                                     .convert(ListType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        ListType<Integer> list = ListInstances.unit()
                                     .unit("hello")
                                     .then(h->ListInstances.functor().map((String v) ->v.length(), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        ListInstances.zippingApplicative()
            .ap(widen(List.of(l1(this::multiplyByTwo))),widen(List.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        ListType<Function<Integer,Integer>> listFn =ListInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(ListType::narrowK);
        
        ListType<Integer> list = ListInstances.unit()
                                     .unit("hello")
                                     .then(h->ListInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->ListInstances.zippingApplicative().ap(listFn, h))
                                     .convert(ListType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       ListType<Integer> list  = ListInstances.monad()
                                      .flatMap(i->widen(ReactiveSeq.range(0,i)), widen(List.of(1,2,3)))
                                      .convert(ListType::narrowK);
    }
    @Test
    public void monad(){
        
        ListType<Integer> list = ListInstances.unit()
                                     .unit("hello")
                                     .then(h->ListInstances.monad().flatMap((String v) ->ListInstances.unit().unit(v.length()), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        ListType<String> list = ListInstances.unit()
                                     .unit("hello")
                                     .then(h->ListInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        ListType<String> list = ListInstances.unit()
                                     .unit("hello")
                                     .then(h->ListInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list.toJavaList(),equalTo(Arrays.asList()));
    }
    
    @Test
    public void monadPlus(){
        ListType<Integer> list = ListInstances.<Integer>monadPlus()
                                      .plus(ListType.widen(List.empty()), ListType.widen(List.of(10)))
                                      .convert(ListType::narrowK);
        assertThat(list.toJavaList(),equalTo(Arrays.asList(10)));
    }
/**
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<ListType<Integer>> m = Monoid.of(ListType.widen(List.empty()), (a,b)->a.isEmpty() ? b : a);
        ListType<Integer> list = ListInstances.<Integer>monadPlus(m)
                                      .plus(ListType.widen(List.of(5)), ListType.widen(List.of(10)))
                                      .convert(ListType::narrowK);
        assertThat(list,equalTo(Arrays.asList(5)));
    }
**/
    @Test
    public void  foldLeft(){
        int sum  = ListInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, ListType.widen(List.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = ListInstances.foldable()
                        .foldRight(0, (a,b)->a+b, ListType.widen(List.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<ListType.µ, Integer>> res = ListInstances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), ListType.just(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(i->i.convert(ListType::narrowK).toJavaList()),
                  equalTo(Maybe.just(ListX.of(2,4,6))));
    }
    
}
