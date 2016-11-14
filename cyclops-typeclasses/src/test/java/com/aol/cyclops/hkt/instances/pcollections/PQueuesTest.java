package com.aol.cyclops.hkt.instances.pcollections;
import static com.aol.cyclops.hkt.pcollections.PQueueType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.data.collections.extensions.persistent.PQueueX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.Maybes;
import com.aol.cyclops.hkt.pcollections.PQueueType;
import com.aol.cyclops.util.function.Lambda;

public class PQueuesTest {

    @Test
    public void unit(){
        
        PQueueType<String> list = PQueues.unit()
                                     .unit("hello")
                                     .convert(PQueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(PQueueX.of("hello").toArray()));
    }
    @Test
    public void functor(){
        
        PQueueType<Integer> list = PQueues.unit()
                                     .unit("hello")
                                     .then(h->PQueues.functor().map((String v) ->v.length(), h))
                                     .convert(PQueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(PQueueX.of("hello".length()).toArray()));
    }
    @Test
    public void apSimple(){
        PQueues.zippingApplicative()
            .ap(widen(PQueueX.of(l1(this::multiplyByTwo))),widen(PQueueX.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        PQueueType<Function<Integer,Integer>> listFn =PQueues.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(PQueueType::narrowK);
        
        PQueueType<Integer> list = PQueues.unit()
                                     .unit("hello")
                                     .then(h->PQueues.functor().map((String v) ->v.length(), h))
                                     .then(h->PQueues.zippingApplicative().ap(listFn, h))
                                     .convert(PQueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(PQueueX.of("hello".length()*2).toArray()));
    }
    @Test
    public void monadSimple(){
       PQueueType<Integer> list  = PQueues.monad()
                                      .flatMap(i->widen(PQueueX.range(0,i)), widen(PQueueX.of(1,2,3)))
                                      .convert(PQueueType::narrowK);
    }
    @Test
    public void monad(){
        
        PQueueType<Integer> list = PQueues.unit()
                                     .unit("hello")
                                     .then(h->PQueues.monad().flatMap((String v) ->PQueues.unit().unit(v.length()), h))
                                     .convert(PQueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(PQueueX.of("hello".length()).toArray()));
    }
    @Test
    public void monadZeroFilter(){
        
        PQueueType<String> list = PQueues.unit()
                                     .unit("hello")
                                     .then(h->PQueues.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(PQueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(PQueueX.of("hello").toArray()));
    }
    @Test
    public void monadZeroFilterOut(){
        
        PQueueType<String> list = PQueues.unit()
                                     .unit("hello")
                                     .then(h->PQueues.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(PQueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(PQueueX.empty().toArray()));
    }
    
    @Test
    public void monadPlus(){
        PQueueType<Integer> list = PQueues.<Integer>monadPlus()
                                      .plus(PQueueType.widen(PQueueX.empty()), PQueueType.widen(PQueueX.of(10)))
                                      .convert(PQueueType::narrowK);
        assertThat(list.toArray(),equalTo(PQueueX.of(10).toArray()));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<PQueueType<Integer>> m = Monoid.of(PQueueType.widen(PQueueX.empty()), (a,b)->a.isEmpty() ? b : a);
        PQueueType<Integer> list = PQueues.<Integer>monadPlus(m)
                                      .plus(PQueueType.widen(PQueueX.of(5)), PQueueType.widen(PQueueX.of(10)))
                                      .convert(PQueueType::narrowK);
        assertThat(list.toArray(),equalTo(PQueueX.of(5).toArray()));
    }
    @Test
    public void  foldLeft(){
        int sum  = PQueues.foldable()
                        .foldLeft(0, (a,b)->a+b, PQueueType.widen(PQueueX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = PQueues.foldable()
                        .foldRight(0, (a,b)->a+b, PQueueType.widen(PQueueX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<PQueueType.µ, Integer>> res = PQueues.traverse()
                                                         .traverseA(Maybes.applicative(), (Integer a)->MaybeType.just(a*2), PQueueType.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(q->PQueueType.narrow(q)
                                       .toArray()).get(),equalTo(Maybe.just(PQueueX.of(2,4,6).toArray()).get()));
    }
    
}
