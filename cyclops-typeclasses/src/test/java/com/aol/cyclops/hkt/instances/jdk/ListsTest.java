package com.aol.cyclops.hkt.instances.jdk;
import static com.aol.cyclops.hkt.jdk.ListType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.cyclops.MaybeType.µ;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.hkt.jdk.ListType;
import com.aol.cyclops.util.function.Lambda;

public class ListsTest {

    @Test
    public void unit(){
        
        ListType<String> list = ListInstances.unit()
                                     .unit("hello")
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        ListType<Integer> list = ListInstances.unit()
                                     .unit("hello")
                                     .then(h->ListInstances.functor().map((String v) ->v.length(), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void apSimple(){
        ListInstances.zippingApplicative()
            .ap(widen(asList(l1(this::multiplyByTwo))),widen(asList(1,2,3)));
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
        
        assertThat(list,equalTo(Arrays.asList("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       ListType<Integer> list  = ListInstances.monad()
                                      .flatMap(i->widen(ListX.range(0,i)), widen(Arrays.asList(1,2,3)))
                                      .convert(ListType::narrowK);
    }
    @Test
    public void monad(){
        
        ListType<Integer> list = ListInstances.unit()
                                     .unit("hello")
                                     .then(h->ListInstances.monad().flatMap((String v) ->ListInstances.unit().unit(v.length()), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        ListType<String> list = ListInstances.unit()
                                     .unit("hello")
                                     .then(h->ListInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(Arrays.asList("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        ListType<String> list = ListInstances.unit()
                                     .unit("hello")
                                     .then(h->ListInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(Arrays.asList()));
    }
    
    @Test
    public void monadPlus(){
        ListType<Integer> list = ListInstances.<Integer>monadPlus()
                                      .plus(ListType.widen(Arrays.asList()), ListType.widen(Arrays.asList(10)))
                                      .convert(ListType::narrowK);
        assertThat(list,equalTo(Arrays.asList(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<ListType<Integer>> m = Monoid.of(ListType.widen(Arrays.asList()), (a,b)->a.isEmpty() ? b : a);
        ListType<Integer> list = ListInstances.<Integer>monadPlus(m)
                                      .plus(ListType.widen(Arrays.asList(5)), ListType.widen(Arrays.asList(10)))
                                      .convert(ListType::narrowK);
        assertThat(list,equalTo(Arrays.asList(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = ListInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, ListType.widen(Arrays.asList(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = ListInstances.foldable()
                        .foldRight(0, (a,b)->a+b, ListType.widen(Arrays.asList(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<ListType.µ, Integer>> res = ListInstances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), ListType.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res,equalTo(Maybe.just(ListX.of(2,4,6))));
    }
    
}
