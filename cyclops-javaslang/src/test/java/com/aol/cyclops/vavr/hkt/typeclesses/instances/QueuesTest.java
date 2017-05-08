package com.aol.cyclops.vavr.hkt.typeclesses.instances;


import static com.aol.cyclops.vavr.hkt.QueueKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import com.aol.cyclops.vavr.Queues;
import com.aol.cyclops.vavr.hkt.QueueKind;
import org.junit.Test;

import com.aol.cyclops2.hkt.Higher;
import cyclops.control.Maybe;
import cyclops.function.Fn1;
import cyclops.function.Lambda;
import cyclops.function.Monoid;

import javaslang.collection.Queue;

public class QueuesTest {

    @Test
    public void unit(){
        
        QueueKind<String> list = Queues.Instances.unit()
                                     .unit("hello")
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello")));
    }
    @Test
    public void functor(){
        
        QueueKind<Integer> list = Queues.Instances.unit()
                                     .unit("hello")
                                     .apply(h->Queues.Instances.functor().map((String v) ->v.length(), h))
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello".length())));
    }
    @Test
    public void apSimple(){
        Queues.Instances.zippingApplicative()
            .ap(widen(Queue.of(l1(this::multiplyByTwo))),widen(Queue.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        QueueKind<Fn1<Integer,Integer>> listFn =Queues.Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(QueueKind::narrowK);
        
        QueueKind<Integer> list = Queues.Instances.unit()
                                     .unit("hello")
                                     .apply(h->Queues.Instances.functor().map((String v) ->v.length(), h))
                                     .apply(h->Queues.Instances.zippingApplicative().ap(listFn, h))
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       QueueKind<Integer> list  = Queues.Instances.monad()
                                      .flatMap(i->widen(Queue.range(0,i)), widen(Queue.of(1,2,3)))
                                      .convert(QueueKind::narrowK);
    }
    @Test
    public void monad(){
        
        QueueKind<Integer> list = Queues.Instances.unit()
                                     .unit("hello")
                                     .apply(h->Queues.Instances.monad().flatMap((String v) ->Queues.Instances.unit().unit(v.length()), h))
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        QueueKind<String> list = Queues.Instances.unit()
                                     .unit("hello")
                                     .apply(h->Queues.Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        QueueKind<String> list = Queues.Instances.unit()
                                     .unit("hello")
                                     .apply(h->Queues.Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.empty()));
    }
    
    @Test
    public void monadPlus(){
        QueueKind<Integer> list = Queues.Instances.<Integer>monadPlus()
                                      .plus(QueueKind.widen(Queue.empty()), QueueKind.widen(Queue.of(10)))
                                      .convert(QueueKind::narrowK);
        assertThat(list,equalTo(Queue.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<QueueKind<Integer>> m = Monoid.of(QueueKind.widen(Queue.empty()), (a, b)->a.isEmpty() ? b : a);
        QueueKind<Integer> list = Queues.Instances.<Integer>monadPlus(m)
                                      .plus(QueueKind.widen(Queue.of(5)), QueueKind.widen(Queue.of(10)))
                                      .convert(QueueKind::narrowK);
        assertThat(list,equalTo(Queue.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = Queues.Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, QueueKind.widen(Queue.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Queues.Instances.foldable()
                        .foldRight(0, (a,b)->a+b, QueueKind.widen(Queue.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       Maybe<Higher<QueueKind.µ, Integer>> res = Queues.Instances.traverse()
                                                         .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), QueueKind.of(1,2,3))
                                                         .convert(Maybe::narrowK);
            
       assertThat(res,equalTo(Maybe.just(Queue.of(2,4,6))));
    }
    
}
