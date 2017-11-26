package com.oath.cyclops.vavr.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.collections.vavr.VavrVectorX;
import cyclops.data.Vector;
import org.junit.Before;
import org.junit.Test;
import com.oath.cyclops.types.persistent.PersistentList;

public class PVectorTest {

    Vector<Integer> org = null;
    PersistentList<Integer> test=null;

    @Before
    public void setup(){

       org = Vector.empty();
       test = VavrVectorX.empty();

    }

    @Test
    public void empty(){
        assertThat(Vector.empty(),equalTo( VavrVectorX.empty()));
    }
    @Test
    public void singleton(){
        assertThat(Vector.of(1),equalTo( VavrVectorX.singleton(1)));
    }

    @Test
    public void plusMinus(){
        System.out.println(test.plusAll(Arrays.asList(1,2,3)));
        System.out.println(test.plusAll(Arrays.asList(1,2,3)).removeAt(1));

        assertThat(org.plus(1),equalTo(test.plus(1)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)),equalTo(test.plusAll(Arrays.asList(1,2,3))));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeValue(1),equalTo(test.plusAll(Arrays.asList(1,2,3)).removeValue(1)));
        //index 1
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeAt(1),equalTo(test.plusAll(Arrays.asList(1,2,3)).removeAt(1)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeAt(0),equalTo(test.plusAll(Arrays.asList(1,2,3)).removeAt(0)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeAt(2),equalTo(test.plusAll(Arrays.asList(1,2,3)).removeAt(2)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeAll(Arrays.asList(2,3)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).removeAll(Arrays.asList(2,3))));

        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).insertAt(1,Arrays.asList(5,6,7))));

    }
/**
    @Test
    public void subList(){


        assertThat(org.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(3,5),
                   equalTo(test.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(3,5)));
        assertThat(org.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(0,1),
                   equalTo(test.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(0,1)));
        assertThat(org.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(0,6),
                   equalTo(test.plusAll(Arrays.asList(1,2,3,4,5,6,7)).subList(0,6)));

    }
    **/
}
