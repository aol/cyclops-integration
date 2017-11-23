package com.aol.cyclops.vavr.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.collections.immutable.PersistentMapX;
import cyclops.companion.MapXs;
import org.junit.Before;
import org.junit.Test;



public class JavaSlangTreePMapTest {
    PersistentMapX<Integer,String> org;
    PersistentMapX<Integer,String> test;

    @Before
    public void setup(){
       org = PersistentMapX.fromMap(MapXs.of(1, "hello"));
       test = VavrTreeMapX.singleton(1, "hello");

    }
    @Test
    public void same(){
        assertThat(org.toList(),equalTo(test.toList()));
    }
    @Test
    public void plus(){
        assertThat(org.plus(2, "world").toList(),equalTo(test.plus(2,"world").toList()));
    }
    @Test
    public void minus(){
        assertThat(org.minus(1),equalTo(test.minus(1)));
    }
    @Test
    public void minusAll(){
        assertThat(org.plus(2, "world").minusAll(Arrays.asList(1,2)),
                   equalTo(test.plus(2,"world").minusAll(Arrays.asList(1,2))));
    }

    @Test
    public void map(){
        assertThat(test.map(s->s+" world").toListX(t->t._2()).get(0),equalTo("hello world"));
    }
}
