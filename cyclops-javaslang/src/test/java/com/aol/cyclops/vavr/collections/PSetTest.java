package com.aol.cyclops.vavr.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.pcollections.HashTreePSet;
import org.pcollections.MapPSet;
import org.pcollections.PSet;
public class PSetTest {

    MapPSet<Integer> org = null;
    PSet<Integer> test=null;
    
    @Before
    public void setup(){
       org = HashTreePSet.empty();
       test = JavaSlangPSet.empty();
     
    }
    
    @Test
    public void empty(){
        assertThat(HashTreePSet.empty(),equalTo(JavaSlangPSet.empty()));
    }
    @Test
    public void singleton(){
        assertThat(HashTreePSet.singleton(1),equalTo(JavaSlangPSet.singleton(1)));
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
