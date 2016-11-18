package com.aol.cyclops.hkt.instances.pcollections;
import static com.aol.cyclops.hkt.pcollections.PVectorType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.data.collections.extensions.persistent.PVectorX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.hkt.pcollections.PVectorType;
import com.aol.cyclops.util.function.Lambda;

public class PVectorsTest {

    @Test
    public void unit(){
        
        PVectorType<String> list = PVectors.unit()
                                     .unit("hello")
                                     .convert(PVectorType::narrowK);
        
        assertThat(list,equalTo(PVectorX.of("hello")));
    }
    @Test
    public void functor(){
        
        PVectorType<Integer> list = PVectors.unit()
                                     .unit("hello")
                                     .then(h->PVectors.functor().map((String v) ->v.length(), h))
                                     .convert(PVectorType::narrowK);
        
        assertThat(list,equalTo(PVectorX.of("hello".length())));
    }
    @Test
    public void apSimple(){
        PVectors.zippingApplicative()
            .ap(widen(PVectorX.of(l1(this::multiplyByTwo))),widen(PVectorX.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        PVectorType<Function<Integer,Integer>> listFn =PVectors.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(PVectorType::narrowK);
        
        PVectorType<Integer> list = PVectors.unit()
                                     .unit("hello")
                                     .then(h->PVectors.functor().map((String v) ->v.length(), h))
                                     .then(h->PVectors.zippingApplicative().ap(listFn, h))
                                     .convert(PVectorType::narrowK);
        
        assertThat(list,equalTo(PVectorX.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       PVectorType<Integer> list  = PVectors.monad()
                                      .flatMap(i->widen(PVectorX.range(0,i)), widen(PVectorX.of(1,2,3)))
                                      .convert(PVectorType::narrowK);
    }
    @Test
    public void monad(){
        
        PVectorType<Integer> list = PVectors.unit()
                                     .unit("hello")
                                     .then(h->PVectors.monad().flatMap((String v) ->PVectors.unit().unit(v.length()), h))
                                     .convert(PVectorType::narrowK);
        
        assertThat(list,equalTo(PVectorX.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        PVectorType<String> list = PVectors.unit()
                                     .unit("hello")
                                     .then(h->PVectors.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(PVectorType::narrowK);
        
        assertThat(list,equalTo(PVectorX.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        PVectorType<String> list = PVectors.unit()
                                     .unit("hello")
                                     .then(h->PVectors.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(PVectorType::narrowK);
        
        assertThat(list,equalTo(PVectorX.empty()));
    }
    
    @Test
    public void monadPlus(){
        PVectorType<Integer> list = PVectors.<Integer>monadPlus()
                                      .plus(PVectorType.widen(PVectorX.empty()), PVectorType.widen(PVectorX.of(10)))
                                      .convert(PVectorType::narrowK);
        assertThat(list,equalTo(PVectorX.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<PVectorType<Integer>> m = Monoid.of(PVectorType.widen(PVectorX.empty()), (a,b)->a.isEmpty() ? b : a);
        PVectorType<Integer> list = PVectors.<Integer>monadPlus(m)
                                      .plus(PVectorType.widen(PVectorX.of(5)), PVectorType.widen(PVectorX.of(10)))
                                      .convert(PVectorType::narrowK);
        assertThat(list,equalTo(PVectorX.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = PVectors.foldable()
                        .foldLeft(0, (a,b)->a+b, PVectorType.widen(PVectorX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = PVectors.foldable()
                        .foldRight(0, (a,b)->a+b, PVectorType.widen(PVectorX.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<PVectorType.µ, Integer>> res = PVectors.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), PVectorType.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res,equalTo(Maybe.just(PVectorX.of(2,4,6))));
    }
    
}
