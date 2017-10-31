package cyclops.collections.clojure;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.collections.immutable.LinkedListX;
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
       test = ClojureListX.empty();

    }

    @Test
    public void empty(){
        assertThat(ConsPersistentList.empty(),equalTo(ClojureListX.empty()));
    }
    @Test
    public void singleton(){
        assertThat(ConsPersistentList.singleton(1),equalTo(ClojureListX.singleton(1)));
    }
    @Test
    public void testWith(){
       System.out.println( ClojureListX.of(1,2,3,4,5,6));
       System.out.println( ClojureListX.of(1,2,3,4,5,6).with(2, 500));
    }
    @Test
    public void npe(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minus((Object)1),equalTo(test.plusAll(Arrays.asList(1,2,3)).minus((Object)1)));

    }
    @Test
    public void npePlus(){
        LinkedListX<Integer> list = ClojureListX.empty();
        for (Integer next : Arrays.asList(1,2,3)) {
            list = list.plus(list.size(), next);
        }
        assertThat(LinkedListX.of(1,2,3),equalTo(list));

    }
    @Test
    public void npePlusAll(){
        LinkedListX<Integer> list = ClojureListX.empty();
        for (Integer next : Arrays.asList(1,2,3)) {
            list = list.plusAll(list.size(), Arrays.asList(next));
        }

    }
    @Test
    public void npeMinusAll(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minusAll(Arrays.asList(1)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minusAll(Arrays.asList(1))));

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


        assertThat(org.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).plusAll(1,Arrays.asList(5,6,7))));

    }

    @Test
    public void minusAll(){
        assertThat(org.plusAll(Arrays.asList(1,2,3)).minusAll(Arrays.asList(2,3)),
                   equalTo(test.plusAll(Arrays.asList(1,2,3)).minusAll(Arrays.asList(2,3))));
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
