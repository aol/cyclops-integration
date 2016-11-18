package com.aol.cyclops.hkt.instances.jdk;
import static com.aol.cyclops.hkt.jdk.OptionalType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;
import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.cyclops.MaybeType;
import com.aol.cyclops.hkt.instances.cyclops.MaybeInstances;
import com.aol.cyclops.hkt.jdk.ListType;
import com.aol.cyclops.hkt.jdk.OptionalType;
import com.aol.cyclops.hkt.jdk.OptionalType;
import com.aol.cyclops.util.Optionals;
import com.aol.cyclops.util.function.Lambda;

public class OptionalsTest {

    @Test
    public void unit(){
        
        OptionalType<String> opt = OptionalInstances.unit()
                                            .unit("hello")
                                            .convert(OptionalType::narrowK);
        
        assertThat(opt,equalTo(Optional.of("hello")));
    }
    @Test
    public void functor(){
        
        OptionalType<Integer> opt = OptionalInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionalInstances.functor().map((String v) ->v.length(), h))
                                     .convert(OptionalType::narrowK);
        
        assertThat(opt,equalTo(Optional.of("hello".length())));
    }
    @Test
    public void apSimple(){
        OptionalInstances.applicative()
            .ap(widen(Optional.of(l1(this::multiplyByTwo))),widen(Optional.of(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        OptionalType<Function<Integer,Integer>> optFn =OptionalInstances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(OptionalType::narrowK);
        
        OptionalType<Integer> opt = OptionalInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionalInstances.functor().map((String v) ->v.length(), h))
                                     .then(h->OptionalInstances.applicative().ap(optFn, h))
                                     .convert(OptionalType::narrowK);
        
        assertThat(opt,equalTo(Optional.of("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       OptionalType<Integer> opt  = OptionalInstances.monad()
                                            .<Integer,Integer>flatMap(i->widen(Optional.of(i*2)), widen(Optional.of(3)))
                                            .convert(OptionalType::narrowK);
    }
    @Test
    public void monad(){
        
        OptionalType<Integer> opt = OptionalInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionalInstances.monad().flatMap((String v) ->OptionalInstances.unit().unit(v.length()), h))
                                     .convert(OptionalType::narrowK);
        
        assertThat(opt,equalTo(Optional.of("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        OptionalType<String> opt = OptionalInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionalInstances.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(OptionalType::narrowK);
        
        assertThat(opt,equalTo(Optional.of("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        OptionalType<String> opt = OptionalInstances.unit()
                                     .unit("hello")
                                     .then(h->OptionalInstances.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(OptionalType::narrowK);
        
        assertThat(opt,equalTo(Optional.empty()));
    }
    
    @Test
    public void monadPlus(){
        OptionalType<Integer> opt = OptionalInstances.<Integer>monadPlus()
                                      .plus(OptionalType.widen(Optional.empty()), OptionalType.widen(Optional.of(10)))
                                      .convert(OptionalType::narrowK);
        assertThat(opt,equalTo(Optional.of(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<OptionalType<Integer>> m = Monoid.of(OptionalType.widen(Optional.empty()), (a,b)->a.isPresent() ? b : a);
        OptionalType<Integer> opt = OptionalInstances.<Integer>monadPlus(m)
                                      .plus(OptionalType.widen(Optional.of(5)), OptionalType.widen(Optional.of(10)))
                                      .convert(OptionalType::narrowK);
        assertThat(opt,equalTo(Optional.of(10)));
    }
    @Test
    public void  foldLeft(){
        int sum  = OptionalInstances.foldable()
                        .foldLeft(0, (a,b)->a+b, OptionalType.widen(Optional.of(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = OptionalInstances.foldable()
                        .foldRight(0, (a,b)->a+b, OptionalType.widen(Optional.of(1)));
        
        assertThat(sum,equalTo(1));
    }
    @Test
    public void traverse(){
       MaybeType<Higher<OptionalType.µ, Integer>> res = OptionalInstances.traverse()
                                                                         .traverseA(MaybeInstances.applicative(), (Integer a)->MaybeType.just(a*2), OptionalType.of(1))
                                                                         .convert(MaybeType::narrowK);
       
       
       assertThat(res,equalTo(Maybe.just(Optional.of(2))));
    }
    
}
