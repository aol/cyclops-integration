package com.aol.cyclops.vavr.hkt.typeclesses.instances;


import static com.aol.cyclops.vavr.hkt.QueueKind.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.QueueKind;
import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.vavr.hkt.typeclasses.instances.QueueInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.collection.Queue;

public class QueuesTest {

    @Test
    public void unit(){
        
        QueueKind<String> list = QueueInstances.unit()
                                     .unit("hello")
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello")));
    }
    @Test
    public void functor(){
        
        QueueKind<Integer> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.functor().map((String v) ->v.length(), h))
                                     .convert(QueueKind::narrowK);
        
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
        
        QueueKind<Function<Integer,Integer>> listFn =QueueInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(QueueKind::narrowK);
        
        QueueKind<Integer> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->QueueInstances.zippingApplicative().ap(listFn, h))
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       QueueKind<Integer> list  = QueueInstances.monad()
                                      .flatMap(i->widen(Queue.range(0,i)), widen(Queue.of(1,2,3)))
                                      .convert(QueueKind::narrowK);
    }
    @Test
    public void monad(){
        
        QueueKind<Integer> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.monad().flatMap((String v) ->QueueInstances.unit().unit(v.length()), h))
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        QueueKind<String> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        QueueKind<String> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(QueueKind::narrowK);
        
        assertThat(list,equalTo(Queue.empty()));
    }
    
    @Test
    public void monadPlus(){
        QueueKind<Integer> list = QueueInstances.<Integer>monadPlus()
                                      .plus(QueueKind.widen(Queue.empty()), QueueKind.widen(Queue.of(10)))
                                      .convert(QueueKind::narrowK);
        assertThat(list,equalTo(Queue.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<QueueKind<Integer>> m = Monoid.of(QueueKind.widen(Queue.empty()), (a, b)->a.isEmpty() ? b : a);
        QueueKind<Integer> list = QueueInstances.<Integer>monadPlus(m)
                                      .plus(QueueKind.widen(Queue.of(5)), QueueKind.widen(Queue.of(10)))
                                      .convert(QueueKind::narrowK);
        assertThat(list,equalTo(Queue.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = QueueInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, QueueKind.widen(Queue.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = QueueInstances.foldable()
                        .foldRight(0, (a,b)->a+b, QueueKind.widen(Queue.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<QueueKind.µ, Integer>> res = QueueInstances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), QueueKind.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
            
       assertThat(res,equalTo(Maybe.just(Queue.of(2,4,6))));
    }
    
}
