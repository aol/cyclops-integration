package com.aol.cyclops.vavr.hkt.typeclesses.instances;


import static com.aol.cyclops.vavr.hkt.VectorKind.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import com.aol.cyclops.vavr.hkt.VectorKind;
import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.vavr.hkt.typeclasses.instances.VectorInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.collection.Vector;

public class VectorsTest {

    @Test
    public void unit(){
        
        VectorKind<String> list = VectorInstances.unit()
                                     .unit("hello")
                                     .convert(VectorKind::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello")));
    }
    @Test
    public void functor(){
        
        VectorKind<Integer> list = VectorInstances.unit()
                                     .unit("hello")
                                     .then(h->VectorInstances.functor().map((String v) ->v.length(), h))
                                     .convert(VectorKind::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello".length())));
    }
    @Test
    public void apSimple(){
        VectorInstances.zippingApplicative()
            .ap(widen(Vector.of(l1(this::multiplyByTwo))),widen(Vector.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        VectorKind<Function<Integer,Integer>> listFn =VectorInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(VectorKind::narrowK);
        
        VectorKind<Integer> list = VectorInstances.unit()
                                     .unit("hello")
                                     .then(h->VectorInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->VectorInstances.zippingApplicative().ap(listFn, h))
                                     .convert(VectorKind::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       VectorKind<Integer> list  = VectorInstances.monad()
                                      .flatMap(i->widen(Vector.range(0,i)), widen(Vector.of(1,2,3)))
                                      .convert(VectorKind::narrowK);
    }
    @Test
    public void monad(){
        
        VectorKind<Integer> list = VectorInstances.unit()
                                     .unit("hello")
                                     .then(h->VectorInstances.monad().flatMap((String v) ->VectorInstances.unit().unit(v.length()), h))
                                     .convert(VectorKind::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        VectorKind<String> list = VectorInstances.unit()
                                     .unit("hello")
                                     .then(h->VectorInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(VectorKind::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        VectorKind<String> list = VectorInstances.unit()
                                     .unit("hello")
                                     .then(h->VectorInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(VectorKind::narrowK);
        
        assertThat(list,equalTo(Vector.empty()));
    }
    
    @Test
    public void monadPlus(){
        VectorKind<Integer> list = VectorInstances.<Integer>monadPlus()
                                      .plus(VectorKind.widen(Vector.empty()), VectorKind.widen(Vector.of(10)))
                                      .convert(VectorKind::narrowK);
        assertThat(list,equalTo(Vector.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<VectorKind<Integer>> m = Monoid.of(VectorKind.widen(Vector.empty()), (a, b)->a.isEmpty() ? b : a);
        VectorKind<Integer> list = VectorInstances.<Integer>monadPlus(m)
                                      .plus(VectorKind.widen(Vector.of(5)), VectorKind.widen(Vector.of(10)))
                                      .convert(VectorKind::narrowK);
        assertThat(list,equalTo(Vector.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = VectorInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, VectorKind.widen(Vector.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = VectorInstances.foldable()
                        .foldRight(0, (a,b)->a+b, VectorKind.widen(Vector.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<VectorKind.µ, Integer>> res = VectorInstances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), VectorKind.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
            
       assertThat(res,equalTo(Maybe.just(Vector.of(2,4,6))));
    }
    
}
