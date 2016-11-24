package com.aol.cyclops.hkt.instances.jdk;
import static com.aol.cyclops.hkt.jdk.QueueType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.data.collections.extensions.standard.QueueX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.hkt.jdk.QueueType;
import com.aol.cyclops.util.function.Lambda;

public class QueuesTest {

    @Test
    public void unit(){
        
        QueueType<String> list = QueueInstances.unit()
                                     .unit("hello")
                                     .convert(QueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(QueueX.of("hello").toArray()));
    }
    @Test
    public void functor(){
        
        QueueType<Integer> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.functor().map((String v) ->v.length(), h))
                                     .convert(QueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(QueueX.of("hello".length()).toArray()));
    }
    @Test
    public void apSimple(){
        QueueInstances.zippingApplicative()
            .ap(widen(QueueX.of(l1(this::multiplyByTwo))),widen(QueueX.of(1,2,3)));
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
        
        assertThat(list.toArray(),equalTo(QueueX.of("hello".length()*2).toArray()));
    }
    @Test
    public void monadSimple(){
       QueueType<Integer> list  = QueueInstances.monad()
                                      .flatMap(i->widen(QueueX.range(0,i)), widen(QueueX.of(1,2,3)))
                                      .convert(QueueType::narrowK);
    }
    @Test
    public void monad(){
        
        QueueType<Integer> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.monad().flatMap((String v) ->QueueInstances.unit().unit(v.length()), h))
                                     .convert(QueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(QueueX.of("hello".length()).toArray()));
    }
    @Test
    public void monadZeroFilter(){
        
        QueueType<String> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(QueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(QueueX.of("hello").toArray()));
    }
    @Test
    public void monadZeroFilterOut(){
        
        QueueType<String> list = QueueInstances.unit()
                                     .unit("hello")
                                     .then(h->QueueInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(QueueType::narrowK);
        
        assertThat(list.toArray(),equalTo(QueueX.of().toArray()));
    }
    
    @Test
    public void monadPlus(){
        QueueType<Integer> list = QueueInstances.<Integer>monadPlus()
                                      .plus(QueueType.widen(QueueX.of()), QueueType.widen(QueueX.of(10)))
                                      .convert(QueueType::narrowK);
        assertThat(list.toArray(),equalTo(QueueX.of(10).toArray()));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<QueueType<Integer>> m = Monoid.of(QueueType.widen(QueueX.of()), (a,b)->a.isEmpty() ? b : a);
        QueueType<Integer> list = QueueInstances.<Integer>monadPlus(m)
                                      .plus(QueueType.widen(QueueX.of(5)), QueueType.widen(QueueX.of(10)))
                                      .convert(QueueType::narrowK);
        assertThat(list.toArray(),equalTo(QueueX.of(5).toArray()));
    }
    @Test
    public void  foldLeft(){
        int sum  = QueueInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, QueueType.widen(QueueX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = QueueInstances.foldable()
                        .foldRight(0, (a,b)->a+b, QueueType.widen(QueueX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<QueueType.µ, Integer>> res = QueueInstances.traverse()
                                                           .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), QueueType.of(1,2,3))
                                                            .convert(MaybeType::narrowK);
       
       
       assertThat(res.map(h->QueueX.fromIterable(h.convert(QueueType::narrowK)).toList()),
                  equalTo(Maybe.just(QueueX.of(2,4,6).toList())));
    }
    
}
