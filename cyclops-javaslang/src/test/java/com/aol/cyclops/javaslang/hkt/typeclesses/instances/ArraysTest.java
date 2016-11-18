package com.aol.cyclops.javaslang.hkt.typeclesses.instances;


import static com.aol.cyclops.javaslang.hkt.ArrayType.widen;
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
import com.aol.cyclops.javaslang.hkt.ArrayType;
import com.aol.cyclops.javaslang.hkt.typeclasses.instances.ArrayInstances;
import com.aol.cyclops.util.function.Lambda;

import javaslang.collection.Array;

public class ArraysTest {

    @Test
    public void unit(){
        
        ArrayType<String> list = ArrayInstances.unit()
                                     .unit("hello")
                                     .convert(ArrayType::narrowK);
        
        assertThat(list,equalTo(Array.of("hello")));
    }
    @Test
    public void functor(){
        
        ArrayType<Integer> list = ArrayInstances.unit()
                                     .unit("hello")
                                     .then(h->ArrayInstances.functor().map((String v) ->v.length(), h))
                                     .convert(ArrayType::narrowK);
        
        assertThat(list,equalTo(Array.of("hello".length())));
    }
    @Test
    public void apSimple(){
        ArrayInstances.zippingApplicative()
            .ap(widen(Array.of(l1(this::multiplyByTwo))),widen(Array.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        ArrayType<Function<Integer,Integer>> listFn =ArrayInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(ArrayType::narrowK);
        
        ArrayType<Integer> list = ArrayInstances.unit()
                                     .unit("hello")
                                     .then(h->ArrayInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->ArrayInstances.zippingApplicative().ap(listFn, h))
                                     .convert(ArrayType::narrowK);
        
        assertThat(list,equalTo(Array.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       ArrayType<Integer> list  = ArrayInstances.monad()
                                      .flatMap(i->widen(Array.range(0,i)), widen(Array.of(1,2,3)))
                                      .convert(ArrayType::narrowK);
    }
    @Test
    public void monad(){
        
        ArrayType<Integer> list = ArrayInstances.unit()
                                     .unit("hello")
                                     .then(h->ArrayInstances.monad().flatMap((String v) ->ArrayInstances.unit().unit(v.length()), h))
                                     .convert(ArrayType::narrowK);
        
        assertThat(list,equalTo(Array.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        ArrayType<String> list = ArrayInstances.unit()
                                     .unit("hello")
                                     .then(h->ArrayInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ArrayType::narrowK);
        
        assertThat(list,equalTo(Array.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        ArrayType<String> list = ArrayInstances.unit()
                                     .unit("hello")
                                     .then(h->ArrayInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(ArrayType::narrowK);
        
        assertThat(list,equalTo(Array.empty()));
    }
    
    @Test
    public void monadPlus(){
        ArrayType<Integer> list = ArrayInstances.<Integer>monadPlus()
                                      .plus(ArrayType.widen(Array.empty()), ArrayType.widen(Array.of(10)))
                                      .convert(ArrayType::narrowK);
        assertThat(list,equalTo(Array.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<ArrayType<Integer>> m = Monoid.of(ArrayType.widen(Array.empty()), (a,b)->a.isEmpty() ? b : a);
        ArrayType<Integer> list = ArrayInstances.<Integer>monadPlus(m)
                                      .plus(ArrayType.widen(Array.of(5)), ArrayType.widen(Array.of(10)))
                                      .convert(ArrayType::narrowK);
        assertThat(list,equalTo(Array.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = ArrayInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, ArrayType.widen(Array.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = ArrayInstances.foldable()
                        .foldRight(0, (a,b)->a+b, ArrayType.widen(Array.of(1,2,3,4)));
        
        assertThat(sum,equalTo(10));
    }
    
    @Test
    public void traverse(){
       MaybeType<Higher<ArrayType.µ, Integer>> res = ArrayInstances.traverse()
                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), ArrayType.of(1,2,3))
                                                         .convert(MaybeType::narrowK);
            
       assertThat(res,equalTo(Maybe.just(Array.of(2,4,6))));
    }
    
}
