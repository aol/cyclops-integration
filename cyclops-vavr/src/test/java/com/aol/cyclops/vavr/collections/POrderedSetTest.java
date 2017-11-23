package com.aol.cyclops.vavr.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import com.oath.cyclops.types.persistent.PersistentSortedSet;
import cyclops.collections.vavr.VavrTreeSetX;
import cyclops.data.TreeSet;
import org.junit.Before;
import org.junit.Test;

public class POrderedSetTest {

    TreeSet<Integer> org = null;
    PersistentSortedSet<Integer> test=null;

    @Before
    public void setup(){
       org = TreeSet.empty();
       test = VavrTreeSetX.empty();

    }

    @Test
    public void empty(){
        assertThat(TreeSet.empty(),equalTo(VavrTreeSetX.empty()));
    }
    @Test
    public void singleton(){
        assertThat(TreeSet.singleton(1),equalTo(VavrTreeSetX.singleton(1)));
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
