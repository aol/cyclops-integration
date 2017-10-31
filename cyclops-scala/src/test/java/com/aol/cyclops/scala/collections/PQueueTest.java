package com.aol.cyclops.scala.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.collections.scala.ScalaQueueX;
import org.junit.Before;
import org.junit.Test;
import org.pcollections.AmortizedPersistentQueue;
import org.pcollections.PersistentQueue;
import org.pcollections.TreePersistentList;
public class PersistentQueueTest {

    AmortizedPersistentQueue<Integer> org = null;
    PersistentQueue<Integer> test=null;

    @Before
    public void setup(){
       org = AmortizedPersistentQueue.empty();
       test = ScalaQueueX.empty();

    }

    @Test
    public void empty(){
        assertThat(TreePersistentList.empty().toArray(),equalTo(ScalaQueueX.empty().toArray()));
    }
    @Test
    public void singleton(){
        assertThat(TreePersistentList.singleton(1).toArray(),equalTo(ScalaQueueX.singleton(1).toArray()));
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
    @Test
    public void plusAllScala(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(Arrays.asList(5,6,7)).toArray(),
                   equalTo(test.plusAll(ScalaQueueX.of(1,2,3)).plusAll(Arrays.asList(5,6,7)).toArray()));
    }

}
