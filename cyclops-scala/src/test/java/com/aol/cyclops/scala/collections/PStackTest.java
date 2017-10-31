package com.aol.cyclops.scala.collections;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.collections.scala.ScalaListX;
import org.junit.Before;
import org.junit.Test;
import org.pcollections.ConsPersistentList;
import org.pcollections.PersistentList;
public class PersistentListTest {

    ConsPersistentList<Integer> org = null;
    PersistentList<Integer> test=null;

    @Before
    public void setup(){
       org = ConsPersistentList.empty();
       test = ScalaListX.empty();

    }

    @Test
    public void empty(){
        assertThat(ConsPersistentList.empty(),equalTo(ScalaListX.empty()));
    }
    @Test
    public void singleton(){
        assertThat(ConsPersistentList.singleton(1),equalTo(ScalaListX.singleton(1)));
    }
    @Test
    public void testWith(){
       System.out.println( ScalaListX.of(1,2,3,4,5,6));
       System.out.println( ScalaListX.of(1,2,3,4,5,6).with(2, 500));
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
    public void minus2(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus(2),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minus(2)));
    }
    @Test
    public void minus3(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus(3),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minus(3)));
    }
    @Test
    public void plusAll(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plusAll(Arrays.asList(5,6,7))));
    }
    @Test
    public void plusAllScala(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(ScalaListX.of(1,2,3)).plusAll(Arrays.asList(5,6,7))));
    }
    @Test
    public void plusIndex0(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plus(0,5),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plus(0,5)));
    }
    @Test
    public void plusIndex1(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plus(1,5),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plus(1,5)));
    }
    @Test
    public void plusIndex2(){
        System.out.println(org.plusAll(Arrays.asList(1,2,3)).plus(2,5));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plus(2,5),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plus(2,5)));
    }
    @Test
    public void plusIndex3(){
        System.out.println(org.plusAll(Arrays.asList(1,2,3)).plus(3,5));
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plus(3,5),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plus(3,5)));
    }
    @Test
    public void plusAllIndex0(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7))));
    }
    @Test
    public void plusAllIndex1(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7))));
    }
    @Test
    public void plusAllIndex2(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(2,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plusAll(2,Arrays.asList(5,6,7))));
    }
    @Test
    public void plusAllIndex3(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(3,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plusAll(3,Arrays.asList(5,6,7))));
    }
    @Test
    public void plusAllIndexScala0(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(ScalaListX.of(1,2,3)).plusAll(1,Arrays.asList(5,6,7))));
    }
    @Test
    public void plusAllIndexScala1(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(ScalaListX.of(1,2,3)).plusAll(1,Arrays.asList(5,6,7))));
    }
    @Test
    public void plusAllIndexScala2(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(2,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(ScalaListX.of(1,2,3)).plusAll(2,Arrays.asList(5,6,7))));
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
