package cyclops.collections.clojure;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import cyclops.collections.immutable.PersistentQueueX;
import org.junit.Before;
import org.junit.Test;
import org.pcollections.AmortizedPQueue;
import org.pcollections.PQueue;


public class PQueueTest {

    AmortizedPQueue<Integer> org = null;
    PQueue<Integer> test=null;
    
    @Before
    public void setup(){
       org = AmortizedPQueue.empty();
       test = ClojureQueueX.empty();
     
    }
    
    @Test
    public void empty(){
        assertThat(AmortizedPQueue.empty().toArray(),equalTo(ClojureQueueX.empty().toArray()));
    }
    @Test
    public void singleton(){
        assertThat(PersistentQueueX.singleton(1).toArray(),equalTo(ClojureQueueX.singleton(1).toArray()));
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
                   equalTo(test.plusAll(ClojureQueueX.of(1,2,3)).plusAll(Arrays.asList(5,6,7)).toArray()));
    }
   
}
