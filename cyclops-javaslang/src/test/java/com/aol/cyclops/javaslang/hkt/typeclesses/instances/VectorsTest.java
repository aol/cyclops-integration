package com.aol.cyclops.javaslang.hkt.typeclesses.instances;


import static com.aol.cyclops.javaslang.hkt.VectorType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.Maybes;
import com.aol.cyclops.javaslang.hkt.VectorType;
import com.aol.cyclops.javaslang.hkt.typeclasses.instances.Vectors;
import com.aol.cyclops.util.function.Lambda;

import javaslang.collection.Vector;

public class VectorsTest {

    @Test
    public void unit(){
        
        VectorType<String> list = Vectors.unit()
                                     .unit("hello")
                                     .convert(VectorType::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello")));
    }
    @Test
    public void functor(){
        
        VectorType<Integer> list = Vectors.unit()
                                     .unit("hello")
                                     .then(h->Vectors.functor().map((String v) ->v.length(), h))
                                     .convert(VectorType::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello".length())));
    }
    @Test
    public void apSimple(){
        Vectors.zippingApplicative()
            .ap(widen(Vector.of(l1(this::multiplyByTwo))),widen(Vector.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        VectorType<Function<Integer,Integer>> listFn =Vectors.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(VectorType::narrowK);
        
        VectorType<Integer> list = Vectors.unit()
                                     .unit("hello")
                                     .then(h->Vectors.functor().map((String v) ->v.length(), h))
                                     .then(h->Vectors.zippingApplicative().ap(listFn, h))
                                     .convert(VectorType::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       VectorType<Integer> list  = Vectors.monad()
                                      .flatMap(i->widen(Vector.range(0,i)), widen(Vector.of(1,2,3)))
                                      .convert(VectorType::narrowK);
    }
    @Test
    public void monad(){
        
        VectorType<Integer> list = Vectors.unit()
                                     .unit("hello")
                                     .then(h->Vectors.monad().flatMap((String v) ->Vectors.unit().unit(v.length()), h))
                                     .convert(VectorType::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        VectorType<String> list = Vectors.unit()
                                     .unit("hello")
                                     .then(h->Vectors.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(VectorType::narrowK);
        
        assertThat(list,equalTo(Vector.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        VectorType<String> list = Vectors.unit()
                                     .unit("hello")
                                     .then(h->Vectors.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(VectorType::narrowK);
        
        assertThat(list,equalTo(Vector.empty()));
    }
    
    @Test
    public void monadPlus(){
        VectorType<Integer> list = Vectors.<Integer>monadPlus()
                                      .plus(VectorType.widen(Vector.empty()), VectorType.widen(Vector.of(10)))
                                      .convert(VectorType::narrowK);
        assertThat(list,equalTo(Vector.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<VectorType<Integer>> m = Monoid.of(VectorType.widen(Vector.empty()), (a,b)->a.isEmpty() ? b : a);
        VectorType<Integer> list = Vectors.<Integer>monadPlus(m)
                                      .plus(VectorType.widen(Vector.of(5)), VectorType.widen(Vector.of(10)))
                                      .convert(VectorType::narrowK);
        assertThat(list,equalTo(Vector.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = Vectors.foldable()
                        .foldLeft(0, (a,b)->a+b, VectorType.widen(Vector.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Vectors.foldable()
                        .foldRight(0, (a,b)->a+b, VectorType.widen(Vector.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<VectorType.µ, Integer>> res = Vectors.traverse()
                                                         .traverseA(Maybes.applicative(), (Integer a)->MaybeType.just(a*2), VectorType.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
            
       assertThat(res,equalTo(Maybe.just(Vector.of(2,4,6))));
    }
    
}
