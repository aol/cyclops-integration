package com.oath.cyclops.vavr.hkt.typeclesses.instances;


import cyclops.companion.vavr.Arrays;
import com.oath.cyclops.vavr.hkt.ArrayKind;
import com.oath.cyclops.hkt.Higher;
import cyclops.control.Maybe;
import cyclops.function.Function1;
import cyclops.function.Lambda;
import cyclops.function.Monoid;
import cyclops.monads.VavrWitness.array;
import io.vavr.collection.Array;
import org.junit.Test;

import static com.oath.cyclops.vavr.hkt.ArrayKind.widen;
import static cyclops.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ArraysTest {

    @Test
    public void unit(){

        ArrayKind<String> list = Arrays.Instances.unit()
                                     .unit("hello")
                                     .convert(ArrayKind::narrowK);

        assertThat(list,equalTo(Array.of("hello")));
    }
    @Test
    public void functor(){

        ArrayKind<Integer> list = Arrays.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Arrays.Instances.functor().map((String v) ->v.length(), h))
                                     .convert(ArrayKind::narrowK);

        assertThat(list,equalTo(Array.of("hello".length())));
    }
    @Test
    public void apSimple(){
        Arrays.Instances.zippingApplicative()
            .ap(widen(Array.of(l1(this::multiplyByTwo))),widen(Array.of(1,2,3)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){

        ArrayKind<Function1<Integer,Integer>> listFn =Arrays.Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(ArrayKind::narrowK);

        ArrayKind<Integer> list = Arrays.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Arrays.Instances.functor().map((String v) ->v.length(), h))
                                     .applyHKT(h->Arrays.Instances.zippingApplicative().ap(listFn, h))
                                     .convert(ArrayKind::narrowK);

        assertThat(list,equalTo(Array.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       ArrayKind<Integer> list  = Arrays.Instances.monad()
                                      .flatMap(i->widen(Array.range(0,i)), widen(Array.of(1,2,3)))
                                      .convert(ArrayKind::narrowK);
    }
    @Test
    public void monad(){

        ArrayKind<Integer> list = Arrays.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Arrays.Instances.monad().flatMap((String v) ->Arrays.Instances.unit().unit(v.length()), h))
                                     .convert(ArrayKind::narrowK);

        assertThat(list,equalTo(Array.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){

        ArrayKind<String> list = Arrays.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Arrays.Instances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(ArrayKind::narrowK);

        assertThat(list,equalTo(Array.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){

        ArrayKind<String> list = Arrays.Instances.unit()
                                     .unit("hello")
                                     .applyHKT(h->Arrays.Instances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(ArrayKind::narrowK);

        assertThat(list,equalTo(Array.empty()));
    }

    @Test
    public void monadPlus(){
        ArrayKind<Integer> list = Arrays.Instances.<Integer>monadPlus()
                                      .plus(ArrayKind.widen(Array.empty()), ArrayKind.widen(Array.of(10)))
                                      .convert(ArrayKind::narrowK);
        assertThat(list,equalTo(Array.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){

        Monoid<ArrayKind<Integer>> m = Monoid.of(ArrayKind.widen(Array.empty()), (a, b)->a.isEmpty() ? b : a);
        ArrayKind<Integer> list = Arrays.Instances.<Integer>monadPlusK(m)
                                      .plus(ArrayKind.widen(Array.of(5)), ArrayKind.widen(Array.of(10)))
                                      .convert(ArrayKind::narrowK);
        assertThat(list,equalTo(Array.of(5)));
    }
    @Test
    public void  foldLeft(){
        int sum  = Arrays.Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, ArrayKind.widen(Array.of(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Arrays.Instances.foldable()
                        .foldRight(0, (a,b)->a+b, ArrayKind.widen(Array.of(1,2,3,4)));

        assertThat(sum,equalTo(10));
    }

    @Test
    public void traverse(){
       Maybe<Higher<array, Integer>> res = Arrays.Instances.traverse()
                                                         .traverseA(Maybe.Instances.applicative(), (Integer a)->Maybe.just(a*2), ArrayKind.of(1,2,3))
                                                         .convert(Maybe::narrowK);

       assertThat(res,equalTo(Maybe.just(Array.of(2,4,6))));
    }

}
