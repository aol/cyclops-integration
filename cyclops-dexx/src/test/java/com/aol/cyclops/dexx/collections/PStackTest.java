package com.aol.cyclops.dexx.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.pcollections.ConsPStack;
import org.pcollections.PStack;

import com.aol.cyclops.data.collections.extensions.persistent.PStackX;
import com.aol.cyclops.reactor.collections.extensions.persistent.LazyPStackX;
public class PStackTest {

    ConsPStack<Integer> org = null;
    PStack<Integer> test=null;
    
    @Before
    public void setup(){
       org = ConsPStack.empty();
       test = DexxPStack.empty();
     
    }
    
    @Test
    public void empty(){
        assertThat(ConsPStack.empty(),equalTo(DexxPVector.empty()));
    }
    @Test
    public void singleton(){
        assertThat(ConsPStack.singleton(1),equalTo(DexxPVector.singleton(1)));
    }
    @Test
    public void ofTest(){
        assertThat(PStackX.of(1,2,3),equalTo(DexxPVector.of(1,2,3)));
    }
    @Test
    public void plusi(){
        List<Integer> values = Arrays.asList(1,2,3,4,5,6);
        LazyPStackX<Integer> list = DexxPStack.empty();
        for (Integer next : values) {
            list = list.plus(list.size(), next);
            System.out.println("List " + list);
        }
        list = list.efficientOpsOff();
        System.out.println("List " + list);
        
        LazyPStackX<Integer> list2 = LazyPStackX.empty();
        for (Integer next : values) {
            list2 = list2.plus(list2.size(), next);
        }
        list2 = list2.efficientOpsOff();
        System.out.println("List2 " + list2);
       assertThat(list,equalTo(list2));
    }
    
    @Test
    public void plusi2(){
        assertThat(PStackX.of(1,2,3).plus(1,10),
                   equalTo(DexxPStack.of(1,2,3).plus(1,10)));
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
        
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7))));
        
    }
    
    @Test
    public void subList(){
        
        
        assertThat(org.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(3,5),
                   equalTo(test.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(3,5)));
        assertThat(org.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(0,1),
                   equalTo(test.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(0,1)));
        assertThat(org.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(0,6),
                   equalTo(test.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(0,6)));
        
    }
}
