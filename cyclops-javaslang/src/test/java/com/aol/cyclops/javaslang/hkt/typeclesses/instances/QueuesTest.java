package com.aol.cyclops.javaslang.hkt.typeclesses.instances;


import static com.aol.cyclops.javaslang.hkt.QueueType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.javaslang.hkt.QueueType;
import com.aol.cyclops.javaslang.hkt.typeclasses.instances.QueueInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.collection.Queue;

public class QueuesTest {

    @Test
    public void unit(){
        
        QueueType<String> list = QueueInstances.unit()
                                     .unit("hello")
                                     .convert(QueueType::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello")));
    }
    @Test
    public void functor(){
        
        QueueType<Integer> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.functor().map((String v) ->v.length(), h))
                                     .convert(QueueType::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello".length())));
    }
    @Test
    public void apSimple(){
        QueueInstances.zippingApplicative()
            .ap(widen(Queue.of(l1(this::multiplyByTwo))),widen(Queue.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        QueueType<Function<Integer,Integer>> listFn =QueueInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(QueueType::narrowK);
        
        QueueType<Integer> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->QueueInstances.zippingApplicative().ap(listFn, h))
                                     .convert(QueueType::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       QueueType<Integer> list  = QueueInstances.monad()
                                      .flatMap(i->widen(Queue.range(0,i)), widen(Queue.of(1,2,3)))
                                      .convert(QueueType::narrowK);
    }
    @Test
    public void monad(){
        
        QueueType<Integer> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.monad().flatMap((String v) ->QueueInstances.unit().unit(v.length()), h))
                                     .convert(QueueType::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        QueueType<String> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(QueueType::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        QueueType<String> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(QueueType::narrowK);
        
        assertThat(list,equalTo(Queue.empty()));
    }
    
    @Test
    public void monadPlus(){
        QueueType<Integer> list = QueueInstances.<Integer>monadPlus()
                                      .plus(QueueType.widen(Queue.empty()), QueueType.widen(Queue.of(10)))
                                      .convert(QueueType::narrowK);
        assertThat(list,equalTo(Queue.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<QueueType<Integer>> m = Monoid.of(QueueType.widen(Queue.empty()), (a,b)->a.isEmpty() ? b : a);
        QueueType<Integer> list = QueueInstances.<Integer>monadPlus(m)
                                      .plus(QueueType.widen(Queue.of(5)), QueueType.widen(Queue.of(10)))
                                      .convert(QueueType::narrowK);
        assertThat(list,equalTo(Queue.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = QueueInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, QueueType.widen(Queue.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = QueueInstances.foldable()
                        .foldRight(0, (a,b)->a+b, QueueType.widen(Queue.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<QueueType.µ, Integer>> res = QueueInstances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), QueueType.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
            
       assertThat(res,equalTo(Maybe.just(Queue.of(2,4,6))));
    }
    
}
