package cyclops.collections.adt;

import org.junit.Test;

import static java.lang.Integer.bitCount;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.testng.Assert.*;


public class ImmutableHashMapTest {

    @Test
    public void testEmpty(){
        assertThat(ImmutableHashMap.empty().size(),equalTo(0));
    }
    @Test
    public void test(){
        ImmutableHashMap<Integer,Integer> map = ImmutableHashMap.empty();

        assertThat(map.put(10,10).size(),equalTo(1));

    }


    @Test
    public void add3Entries(){
        ImmutableHashMap<Integer,Integer> map = ImmutableHashMap.empty();
        for(int i=0;i<3;i++){
            map = map.put(i,i*2);
        }
        assertThat(map.size(),equalTo(3));
    }
    @Test
    public void add5Entries(){
        ImmutableHashMap<Integer,Integer> map = ImmutableHashMap.empty();
        for(int i=0;i<5;i++){
            map = map.put(i,i*2);
        }
        assertThat(map.size(),equalTo(5));
    }
    @Test
    public void add10Entries(){
        ImmutableHashMap<Integer,Integer> map = ImmutableHashMap.empty();
        for(int i=0;i<10;i++){
            map = map.put(i,i*2);
        }
        assertThat(map.size(),equalTo(10));
    }
    @Test
    public void add34Entries(){
        ImmutableHashMap<Integer,Integer> map = ImmutableHashMap.empty();
        for(int i=0;i<34;i++){
            map = map.put(i,i*2);
        }
        assertThat(map.size(),equalTo(34));
    }
    @Test
    public void add500Entries(){
        ImmutableHashMap<Integer,Integer> map = ImmutableHashMap.empty();
        for(int i=0;i<500;i++){
            map = map.put(i,i*2);
        }
        assertThat(map.size(),equalTo(500));
    }

}