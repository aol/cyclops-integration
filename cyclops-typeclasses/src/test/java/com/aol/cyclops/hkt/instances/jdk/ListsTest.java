package com.aol.cyclops.hkt.instances.jdk;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.hkt.jdk.ListType;
import com.aol.cyclops.util.function.Lambda;

public class ListsTest {

    @Test
    public void unit(){
        
        ListType<String> list = Lists.unit()
                                     .unit("hello")
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(Arrays.asList("hello")));
    }
    @Test
    public void functor(){
        
        ListType<Integer> list = Lists.unit()
                                     .unit("hello")
                                     .then(h->Lists.functor().map((String v) ->v.length(), h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(Arrays.asList("hello".length())));
    }
    @Test
    public void applicative(){
        
        ListType<Function<Integer,Integer>> listFn =Lists.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(ListType::narrowK);
        
        ListType<Integer> list = Lists.unit()
                                     .unit("hello")
                                     .then(h->Lists.functor().map((String v) ->v.length(), h))
                                     .then(h->Lists.zippingApplicative().ap(listFn, h))
                                     .convert(ListType::narrowK);
        
        assertThat(list,equalTo(Arrays.asList("hello".length()*2)));
    }
    
    
}
