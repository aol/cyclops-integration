package com.aol.cyclops.functionaljava.hkt.typeclesses.instances;

import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import com.aol.cyclops.functionaljava.hkt.NonEmptyListKind;
import org.junit.Test;

import com.aol.cyclops.functionaljava.hkt.typeclassess.instances.Instances;
import com.aol.cyclops.util.function.Lambda;

import fj.data.List;
import fj.data.NonEmptyList;

public class NonEmptyListsTest {

    @Test
    public void unit(){
        
        NonEmptyListKind<String> list = Instances.unit()
                                     .unit("hello")
                                     .convert(NonEmptyListKind::narrowK);
        
        assertThat(list,equalTo(list("hello")));
    }
    @Test
    public void functor(){
        
        NonEmptyListKind<Integer> list = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .convert(NonEmptyListKind::narrowK);
        
        assertThat(list,equalTo(NonEmptyList.fromList(List.list("hello".length())).some()));
    }
    @Test
    public void apSimple(){
        Instances.zippingApplicative()
            .ap(NonEmptyListKind.of(l1(this::multiplyByTwo)), NonEmptyListKind.of(1,2,3));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        NonEmptyListKind<Function<Integer,Integer>> listFn = Instances.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(NonEmptyListKind::narrowK);
        
        NonEmptyListKind<Integer> list = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.functor().map((String v) ->v.length(), h))
                                     .then(h-> Instances.zippingApplicative().ap(listFn, h))
                                     .convert(NonEmptyListKind::narrowK);
        
        assertThat(list,equalTo(list("hello".length()*2)));
    }
    private <T> NonEmptyList<T> list(T... values) {
        return NonEmptyList.fromList(List.list(values)).some();
                
    }
    private NonEmptyListKind<Integer> range(int start, int end){
        return NonEmptyListKind.widen(NonEmptyList.fromList(List.range(start,end)).some());
    }
    @Test
    public void monadSimple(){
       NonEmptyListKind<Integer> list  = Instances.monad()
                                      .flatMap(i->range(0,i), NonEmptyListKind.of(1,2,3))
                                      .convert(NonEmptyListKind::narrowK);
    }
    @Test
    public void monad(){
        
        NonEmptyListKind<Integer> list = Instances.unit()
                                     .unit("hello")
                                     .then(h-> Instances.monad().flatMap((String v) -> Instances.unit().unit(v.length()), h))
                                     .convert(NonEmptyListKind::narrowK);
        
        assertThat(list,equalTo(list("hello".length())));
    }

    @Test
    public void  foldLeft(){
        int sum  = Instances.foldable()
                        .foldLeft(0, (a,b)->a+b, NonEmptyListKind.of(1,2,3,4));
        
        assertThat(sum,equalTo(10));
    }
    @Test
    public void  foldRight(){
        int sum  = Instances.foldable()
                        .foldRight(0, (a,b)->a+b, NonEmptyListKind.of(1,2,3,4));
        
        assertThat(sum,equalTo(10));
    }
    
   
    
}
