package com.aol.cyclops.hkt.instances.jdk;
import static com.aol.cyclops.hkt.jdk.DequeType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.data.collections.extensions.standard.DequeX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.hkt.jdk.DequeType;
import com.aol.cyclops.util.function.Lambda;

public class DequesTest {

    @Test
    public void unit(){
        
        DequeType<String> list = DequeInstances.unit()
                                     .unit("hello")
                                     .convert(DequeType::narrowK);
        
        assertThat(list.toArray(),equalTo(DequeX.of("hello").toArray()));
    }
    @Test
    public void functor(){
        
        DequeType<Integer> list = DequeInstances.unit()
                                     .unit("hello")
                                     .then(h->DequeInstances.functor().map((String v) ->v.length(), h))
                                     .convert(DequeType::narrowK);
        
        assertThat(list.toArray(),equalTo(DequeX.of("hello".length()).toArray()));
    }
    @Test
    public void apSimple(){
        DequeInstances.zippingApplicative()
            .ap(widen(DequeX.of(l1(this::multiplyByTwo))),widen(DequeX.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        DequeType<Function<Integer,Integer>> listFn =DequeInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(DequeType::narrowK);
        
        DequeType<Integer> list = DequeInstances.unit()
                                     .unit("hello")
                                     .then(h->DequeInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->DequeInstances.zippingApplicative().ap(listFn, h))
                                     .convert(DequeType::narrowK);
        
        assertThat(list.toArray(),equalTo(DequeX.of("hello".length()*2).toArray()));
    }
    @Test
    public void monadSimple(){
       DequeType<Integer> list  = DequeInstances.monad()
                                      .flatMap(i->widen(DequeX.range(0,i)), widen(DequeX.of(1,2,3)))
                                      .convert(DequeType::narrowK);
    }
    @Test
    public void monad(){
        
        DequeType<Integer> list = DequeInstances.unit()
                                     .unit("hello")
                                     .then(h->DequeInstances.monad().flatMap((String v) ->DequeInstances.unit().unit(v.length()), h))
                                     .convert(DequeType::narrowK);
        
        assertThat(list.toArray(),equalTo(DequeX.of("hello".length()).toArray()));
    }
    @Test
    public void monadZeroFilter(){
        
        DequeType<String> list = DequeInstances.unit()
                                     .unit("hello")
                                     .then(h->DequeInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(DequeType::narrowK);
        
        assertThat(list.toArray(),equalTo(DequeX.of("hello").toArray()));
    }
    @Test
    public void monadZeroFilterOut(){
        
        DequeType<String> list = DequeInstances.unit()
                                     .unit("hello")
                                     .then(h->DequeInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(DequeType::narrowK);
        
        assertThat(list.toArray(),equalTo(DequeX.of().toArray()));
    }
    
    @Test
    public void monadPlus(){
        DequeType<Integer> list = DequeInstances.<Integer>monadPlus()
                                      .plus(DequeType.widen(DequeX.of()), DequeType.widen(DequeX.of(10)))
                                      .convert(DequeType::narrowK);
        assertThat(list.toArray(),equalTo(DequeX.of(10).toArray()));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<DequeType<Integer>> m = Monoid.of(DequeType.widen(DequeX.of()), (a,b)->a.isEmpty() ? b : a);
        DequeType<Integer> list = DequeInstances.<Integer>monadPlus(m)
                                      .plus(DequeType.widen(DequeX.of(5)), DequeType.widen(DequeX.of(10)))
                                      .convert(DequeType::narrowK);
        assertThat(list.toArray(),equalTo(DequeX.of(5).toArray()));
    }
    @Test
    public void  foldLeft(){
        int sum  = DequeInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, DequeType.widen(DequeX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = DequeInstances.foldable()
                        .foldRight(0, (a,b)->a+b, DequeType.widen(DequeX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<DequeType.µ, Integer>> res = DequeInstances.traverse()
                                                           .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), DequeType.of(1,2,3))
                                                            .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(h->DequeX.fromIterable(h.convert(DequeType::narrowK)).toList()),
                  equalTo(Maybe.just(DequeX.of(2,4,6).toList())));
    }
    
}
