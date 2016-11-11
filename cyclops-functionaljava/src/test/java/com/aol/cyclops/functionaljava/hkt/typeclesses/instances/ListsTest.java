package com.aol.cyclops.functionaljava.hkt.typeclesses.instances;

import static com.aol.cyclops.functionaljava.hkt.ListType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.functionaljava.hkt.ListType;
import com.aol.cyclops.functionaljava.hkt.typeclassess.instances.Lists;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.Maybes;
import com.aol.cyclops.util.function.Lambda;

import fj.data.List;
import fj.data.Option;

public class ListsTest {

    @Test
    public void unit(){
        
        ListType<String> list = Lists.unit()
                                     .unit("hello")
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(List.list("hello")));
    }
    @Test
    public void functor(){
        
        ListType<Integer> list = Lists.unit()
                                     .unit("hello")
                                     .then(h->Lists.functor().map((String v) ->v.length(), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(List.list("hello".length())));
    }
    @Test
    public void apSimple(){
        Lists.zippingApplicative()
            .ap(widen(List.list(l1(this::multiplyByTwo))),widen(List.list(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        ListType<Function<Integer,Integer>> listFn =Lists.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(ListType::narrowK);
        
        ListType<Integer> list = Lists.unit()
                                     .unit("hello")
                                     .then(h->Lists.functor().map((String v) ->v.length(), h))
                                     .then(h->Lists.zippingApplicative().ap(listFn, h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(List.list("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       ListType<Integer> list  = Lists.monad()
                                      .flatMap(i->widen(List.range(0,i)), widen(List.list(1,2,3)))
                                      .convert(ListType::narrowK);
    }
    @Test
    public void monad(){
        
        ListType<Integer> list = Lists.unit()
                                     .unit("hello")
                                     .then(h->Lists.monad().flatMap((String v) ->Lists.unit().unit(v.length()), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(List.list("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        ListType<String> list = Lists.unit()
                                     .unit("hello")
                                     .then(h->Lists.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(List.list("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        ListType<String> list = Lists.unit()
                                     .unit("hello")
                                     .then(h->Lists.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(List.list()));
    }
    
    @Test
    public void monadPlus(){
        ListType<Integer> list = Lists.<Integer>monadPlus()
                                      .plus(ListType.widen(List.list()), ListType.widen(List.list(10)))
                                      .convert(ListType::narrowK);
        assertThat(list,equalTo(List.list(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<ListType<Integer>> m = Monoid.of(ListType.widen(List.list()), (a,b)->a.isEmpty() ? b : a);
        ListType<Integer> list = Lists.<Integer>monadPlus(m)
                                      .plus(ListType.widen(List.list(5)), ListType.widen(List.list(10)))
                                      .convert(ListType::narrowK);
        assertThat(list,equalTo(List.list(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = Lists.foldable()
                        .foldLeft(0, (a,b)->a+b, ListType.widen(List.list(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Lists.foldable()
                        .foldRight(0, (a,b)->a+b, ListType.widen(List.list(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<ListType.µ, Integer>> res = Lists.traverse()
                                                         .traverseA(Maybes.applicative(), (Integer a)->MaybeType.just(a*2), ListType.list(1,2,3))
                                                         .convert(MaybeType::narrowK);
            
       assertThat(res,equalTo(Maybe.just(List.list(6,4,2))));
    }
    
}
