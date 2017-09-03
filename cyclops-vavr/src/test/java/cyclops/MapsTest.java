package cyclops;


import io.vavr.collection.HashMap;
import org.junit.Test;

public class MapsTest {
    @Test
    public void add10000(){
        //12424
        long start = System.currentTimeMillis();
        HashMap<Integer,Integer> v = HashMap.empty();
        for(int i=0;i<100_000_00;i++){
            v =v.put(i,i);
        }
        System.out.println(System.currentTimeMillis()-start);
        System.out.println(v.size());
    }
}
