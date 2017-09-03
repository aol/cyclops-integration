package cyclops.collections.adt;

import org.junit.Test;
import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.testng.Assert.*;

public class HashMapTest {

    @Test
    public void plusSize(){
        assertThat(HashMap.empty().plus("hello","world").size(),equalTo(1));
    }
    @Test
    public void add10000(){
        //17579
        long start = System.currentTimeMillis();
        HashMap<Integer,Integer> v = HashMap.empty();
        for(int i=0;i<100_000_00;i++){
            v =v.plus(i,i);
        }
        System.out.println(System.currentTimeMillis()-start);
        System.out.println(v.size());
    }
    @Test
    public void read100_000_00(){
        //4557

        HashMap<Integer,Integer> v = HashMap.empty();
        for(int i=0;i<100_000_00;i++){
            v =v.plus(i,i);
        }
        ArrayList<Integer> al = new ArrayList(v.size());
        long start = System.currentTimeMillis();
        for(int i=0;i<100_000_00;i++){
            al.add(v.get(i).get());
        }

        System.out.println(System.currentTimeMillis()-start);
        System.out.println(v.size());
    }
    @Test
    public void read100_000_00PC(){
        //1905

        PMap<Integer,Integer> v = HashTreePMap.empty();
        for(int i=0;i<100_000_00;i++){
            v =v.plus(i,i);
        }
        ArrayList<Integer> al = new ArrayList(v.size());
        long start = System.currentTimeMillis();
        for(int i=0;i<100_000_00;i++){
            al.add(v.get(i));
        }

        System.out.println(System.currentTimeMillis()-start);
        System.out.println(v.size());
    }

    @Test
    public void add10000PCol(){
        //27055
        long start = System.currentTimeMillis();
        PMap<Integer,Integer> v = HashTreePMap.empty();
        for(int i=0;i<100_000_00;i++){
            v =v.plus(i,i);
        }
        System.out.println(System.currentTimeMillis()-start);
        System.out.println(v.size());
    }

}