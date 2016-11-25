package com.aol.cyclops.scala.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.pcollections.AmortizedPQueue;
import org.pcollections.PQueue;
import org.pcollections.TreePVector;
public class PQueueTest {

    AmortizedPQueue<Integer> org = null;
    PQueue<Integer> test=null;
    
    @Before
    public void setup(){
       org = AmortizedPQueue.empty();
       test = ScalaPQueue.empty();
     
    }
    
    @Test
    public void empty(){
        assertThat(TreePVector.empty(),equalTo(ScalaPVector.empty()));
    }
    @Test
    public void singleton(){
        assertThat(TreePVector.singleton(1),equalTo(ScalaPVector.singleton(1)));
    }
    
    @Test
    public void plusMinus(){
        System.out.println(test.plusAll(Arrays.asList(1,2,3)));
        System.out.println(test.plusAll(Arrays.asList(1,2,3)).minus(1));
        
        assertThat(org.plus(1).toArray(),equalTo(test.plus(1).toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus((Object)1).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minus((Object)1).toArray()));
        //index 1
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus(1).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minus(1).toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus(0).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minus(0).toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus(2).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minus(2).toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minusAll(Arrays.asList(2,3)).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minusAll(Arrays.asList(2,3)).toArray()));
        
        
        
    }
    
   
}
