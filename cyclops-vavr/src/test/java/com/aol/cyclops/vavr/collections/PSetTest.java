package com.aol.cyclops.vavr.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import com.oath.cyclops.types.persistent.PersistentSet;
import cyclops.collections.vavr.VavrHashSetX;
import cyclops.data.HashSet;
import org.junit.Before;
import org.junit.Test;

public class PSetTest {

    HashSet<Integer> org = null;
    PersistentSet<Integer> test=null;

    @Before
    public void setup(){
       org = HashSet.empty();
       test = VavrHashSetX.empty();

    }

    @Test
    public void empty(){
        assertThat(HashSet.empty(),equalTo(VavrHashSetX.empty()));
    }
    @Test
    public void singleton(){
        assertThat(HashSet.singleton(1),equalTo(VavrHashSetX.singleton(1)));
    }

    @Test
    public void plusMinus(){
        System.out.println(test.plusAll(Arrays.asList(1,2,3)));
        System.out.println(test.plusAll(Arrays.asList(1,2,3)).removeValue(1));

        assertThat(org.plus(1),equalTo(test.plus(1)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)),equalTo(test.plusAll(Arrays.asList(1,2,3))));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeValue(1),equalTo(test.plusAll(Arrays.asList(1,2,3)).removeValue(1)));
        //index 1
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeValue(1),equalTo(test.plusAll(Arrays.asList(1,2,3)).removeValue(1)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeValue(0),equalTo(test.plusAll(Arrays.asList(1,2,3)).removeValue(0)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeValue(2),equalTo(test.plusAll(Arrays.asList(1,2,3)).removeValue(2)));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeAll(Arrays.asList(2,3)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).removeAll(Arrays.asList(2,3))));



    }


}
