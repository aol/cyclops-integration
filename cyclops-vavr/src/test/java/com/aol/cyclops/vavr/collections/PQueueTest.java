package com.aol.cyclops.vavr.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import com.oath.cyclops.types.persistent.PersistentQueue;
import cyclops.collections.vavr.VavrQueueX;
import cyclops.data.BankersQueue;
import org.junit.Before;
import org.junit.Test;

public class PQueueTest {

    BankersQueue<Integer> org = null;
    PersistentQueue<Integer> test=null;

    @Before
    public void setup(){
       org = BankersQueue.empty();
       test = VavrQueueX.empty();

    }

    @Test
    public void empty(){
        assertThat(BankersQueue.empty().toArray(),equalTo(VavrQueueX.empty().toArray()));
    }
    @Test
    public void singleton(){
        assertThat(BankersQueue.empty().plus(1).toArray(),equalTo(VavrQueueX.singleton(1).toArray()));
    }

    @Test
    public void plusMinus(){
        System.out.println(test.plusAll(Arrays.asList(1,2,3)));
        System.out.println(test.plusAll(Arrays.asList(1,2,3)).removeValue(1));

        assertThat(org.plus(1).toArray(),equalTo(test.plus(1).stream().toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).stream().toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeValue(1).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).removeValue(1).stream().toArray()));
        //index 1
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeValue(1).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).removeValue(1).stream().toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeValue(0).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).removeValue(0).stream().toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeValue(2).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).removeValue(2).stream().toArray()));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).removeAll(Arrays.asList(2,3)).toArray(),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).removeAll(Arrays.asList(2,3)).stream().toArray()));



    }


}
