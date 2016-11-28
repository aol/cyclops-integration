package com.aol.cyclops.clojure.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;
import org.pcollections.HashTreePSet;
import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;

import clojure.lang.PersistentTreeSet;
import clojure.lang.PersistentVector;
public class POrderedSetTest {

    OrderedPSet<Integer> org = null;
    POrderedSet<Integer> test=null;
    
    @Before
    public void setup(){
       org = OrderedPSet.empty();
       test = ClojureTreePOrderedSet.<Integer>empty(Comparator.naturalOrder());
     
    }
    
    @Test
    public void empty(){
        assertThat(HashTreePSet.empty(),equalTo(ClojureTreePOrderedSet.<Integer>empty(Comparator.naturalOrder())));
    }
    @Test
    public void singleton(){
        assertThat(OrderedPSet.singleton(1),equalTo(ClojureTreePOrderedSet.singleton(Comparator.naturalOrder(),1)));
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
    @Test
    public void plusAllScala(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(Arrays.asList(5,6,7)).toArray(),
                   equalTo(test.plusAll(ClojureTreePOrderedSet.of(Comparator.naturalOrder(),1,2,3)).plusAll(Arrays.asList(5,6,7)).toArray()));
    }
    
   
}
