package com.oath.cyclops.vavr.hkt.typeclesses.instances;


import static com.oath.cyclops.vavr.hkt.VectorKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import cyclops.companion.vavr.Vectors;
import cyclops.monads.VavrWitness.vector;
import org.junit.Test;

import com.oath.cyclops.hkt.Higher;
import cyclops.control.Maybe;
import cyclops.function.Function1;
import cyclops.function.Lambda;
import cyclops.function.Monoid;

import io.vavr.collection.Vector;

public class VectorsTest {

    @Test
    public void unit(){

        VectorKind<String> list = Vectors.Instances.unit()
                                     .unit("hello")
                                     .convert(VectorKind::narrowK);

        assertThat(list,equalTo(Vector.of("hello")));
    }
    @Test
    public void functor(){

        VectorKind<Integer> list = Vectors.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Vectors.Instances.functor().map((String v) ->v.length(), h))
                                     .convert(VectorKind::narrowK);

        assertThat(list,equalTo(Vector.of("hello".length())));
    }
    @Test
    public void apSimple(){
        Vectors.Instances.zippingApplicative()
            .ap(widen(Vector.of(l1(this::multiplyByTwo))),widen(Vector.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        VectorKind<Function1<Integer,Integer>> listFn = Vectors.Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(VectorKind::narrowK);

        VectorKind<Integer> list = Vectors.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Vectors.Instances.functor().map((String v) ->v.length(), h))
                                     .applyHKT(h-> Vectors.Instances.zippingApplicative().ap(listFn, h))
                                     .convert(VectorKind::narrowK);

        assertThat(list,equalTo(Vector.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       VectorKind<Integer> list  = Vectors.Instances.monad()
                                      .flatMap(i->widen(Vector.range(0,i)), widen(Vector.of(1,2,3)))
                                      .convert(VectorKind::narrowK);
    }
    @Test
    public void monad(){

        VectorKind<Integer> list = Vectors.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Vectors.Instances.monad().flatMap((String v) -> Vectors.Instances.unit().unit(v.length()), h))
                                     .convert(VectorKind::narrowK);

        assertThat(list,equalTo(Vector.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){

        VectorKind<String> list = Vectors.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Vectors.Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(VectorKind::narrowK);

        assertThat(list,equalTo(Vector.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){

        VectorKind<String> list = Vectors.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h-> Vectors.Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(VectorKind::narrowK);

        assertThat(list,equalTo(Vector.empty()));
    }

    @Test
    public void monadPlus(){
        VectorKind<Integer> list = Vectors.Instances.<Integer>monadPlus()
                                      .plus(VectorKind.widen(Vector.empty()), VectorKind.widen(Vector.of(10)))
                                      .convert(VectorKind::narrowK);
        assertThat(list,equalTo(Vector.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){

        Monoid<VectorKind<Integer>> m = Monoid.of(VectorKind.widen(Vector.empty()), (a, b)->a.isEmpty() ? b : a);
        VectorKind<Integer> list = Vectors.Instances.<Integer>monadPlusK(m)
                                      .plus(VectorKind.widen(Vector.of(5)), VectorKind.widen(Vector.of(10)))
                                      .convert(VectorKind::narrowK);
        assertThat(list,equalTo(Vector.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = Vectors.Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, VectorKind.widen(Vector.of(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Vectors.Instances.foldable()
                        .foldRight(0, (a,b)->a+b, VectorKind.widen(Vector.of(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }

    @Test
    public void traverse(){
       Maybe<Higher<vector, Integer>> res = Vectors.Instances.traverse()
                                                         .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), VectorKind.of(1,2,3))
                                                         .convert(Maybe::narrowK);

       assertThat(res,equalTo(Maybe.just(Vector.of(2,4,6))));
    }

}
