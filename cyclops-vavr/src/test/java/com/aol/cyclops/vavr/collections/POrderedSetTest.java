package com.aol.cyclops.vavr.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.collections.vavr.VavrTreeSetX;
import org.junit.Before;
import org.junit.Test;
import org.pcollections.HashTreePSet;
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;
public class POrderedSetTest {

    OrderedPSet<Integer> org = null;
    POrderedSet<Integer> test=null;
    
    @Before
    public void setup(){
       org = OrderedPSet.empty();
       test = VavrTreeSetX.empty();
     
    }
    
    @Test
    public void empty(){
        assertThat(HashTreePSet.empty(),equalTo(VavrTreeSetX.empty()));
    }
    @Test
    public void singleton(){
        assertThat(OrderedPSet.singleton(1),equalTo(VavrTreeSetX.singleton(1)));
    }
    
    @Test
    public void plusMinus(){
        System.out.println(test.plusAll(Arrays.asList(1,2,3)));
        System.out.println(test.plusAll(Arrays.asList(1,2,3)).minus(1));
        
        assertThat(org.plus(1),equalTo(test.plus(1)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)),equalTo(test.plusAll(Arrays.asList(1,2,3))));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus((Object)1),equalTo(test.plusAll(Arrays.asList(1,2,3)).minus((Object)1)));
        //index 1
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus(1),equalTo(test.plusAll(Arrays.asList(1,2,3)).minus(1)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus(0),equalTo(test.plusAll(Arrays.asList(1,2,3)).minus(0)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus(2),equalTo(test.plusAll(Arrays.asList(1,2,3)).minus(2)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minusAll(Arrays.asList(2,3)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minusAll(Arrays.asList(2,3))));
        
        
        
    }
    
   
}
