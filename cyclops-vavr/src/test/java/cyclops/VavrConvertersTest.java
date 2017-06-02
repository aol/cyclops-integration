package cyclops;

import cyclops.collections.immutable.*;

import cyclops.collections.vavr.*;
import cyclops.companion.MapXs;
import cyclops.companion.PersistentMapXs;
import io.vavr.collection.*;
import org.junit.Test;

import java.util.Comparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertTrue;

/**
 * Created by johnmcclean on 25/05/2017.
 */
public class VavrConvertersTest {

    @Test
    public void listNative(){
//materializeList makes use of ConsPStack
        List<Integer> list = LinkedListX.of(1,2,3)
                                        .type(VavrListX.toPStack())
                                        .map(i->i*2)
                                        .to(VavrConverters::List);

        assertThat(list.toString(), equalTo( "List(2, 4, 6)"));
    }
    @Test
    public void listAlien(){
//materializeList makes use of ConsPStack
        List<Integer> list = LinkedListX.of(1,2,3)
                .map(i->i*2)
                .to(VavrConverters::List);

        assertThat(list.toString(), equalTo( "List(2, 4, 6)"));
    }
    @Test
    public void vectorNative() {

        Vector<Integer> list = VectorX.of(1, 2, 3)
                .type(VavrTypes.vector())
                .map(i -> i * 2)
                .to(VavrConverters::Vector);

        assertThat(list.toString(), equalTo("Vector(2, 4, 6)"));
    }
    @Test
    public void vectorAlien(){

        Vector<Integer> list = VectorX.of(1,2,3)
                                      .map(i->i*2)
                .to(VavrConverters::Vector);

        assertThat(list.toString(), equalTo("Vector(2, 4, 6)"));
    }

    @Test
    public void queueNative() {

        Queue<Integer> list = PersistentQueueX.of(1, 2, 3)
                                              .type(VavrTypes.queue())
                                              .map(i -> i * 2)
                                              .to(VavrConverters::Queue);

        assertThat(list.toString(), equalTo("Queue(6, 4, 2)"));
    }
    @Test
    public void queueAlien(){

        Queue<Integer> list = PersistentQueueX.of(1,2,3)
                .map(i->i*2)
                .to(VavrConverters::Queue);

        assertThat(list.toString(), equalTo("Queue(2, 4, 6)"));
    }
    @Test
    public void hashSetNative() {

        HashSet<Integer> list = PersistentSetX.of(1, 2, 3)
                .type(VavrTypes.hashSet())
                .map(i -> i * 2)
                .to(VavrConverters::HashSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void hashSetAlien(){

        HashSet<Integer> list = PersistentSetX.of(1,2,3)
                                              .map(i->i*2)
                                              .to(VavrConverters::HashSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void treeSetNative() {

        TreeSet<Integer> list = OrderedSetX.of(1, 2, 3)
                                           .type(VavrTypes.treeSet())
                                            .map(i -> i * 2)
                                           .to(VavrConverters::TreeSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void treeSetAlien(){

        TreeSet<Integer> list = OrderedSetX.of(1, 2, 3)
                .map(i->i*2)
                .to(VavrConverters::TreeSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void bitSetNative() {

        BitSet list = OrderedSetX.of(1, 2, 3)
                .type(VavrTypes.bitset())
                .map(i -> i * 2)
                .to(VavrConverters::BitSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void bitSetAlien(){

        BitSet list = OrderedSetX.of(1, 2, 3)
                .map(i->i*2)
                .to(VavrConverters::BitSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }

    @Test
    public void hashMapNative() {

        HashMap<Integer,Integer> list = VavrHashMapX.copyFromMap(MapXs.of(1,2,3,4))
                                                       .to(VavrConverters::HashMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.containsKey(1),list.toString());
        assertTrue(list.containsKey(3),list.toString());

    }

    @Test
    public void hashMapAlien(){

        HashMap<Integer,Integer> list = PersistentMapXs.of(1, 2, 3,4)
                .to(VavrConverters::HashMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.containsKey(1),list.toString());
        assertTrue(list.containsKey(3),list.toString());
    }
    @Test
    public void treeMapNative() {

        TreeMap<Integer,Integer> list = VavrTreeMapX.copyFromMap(MapXs.of(1,2,3,4), Comparator.naturalOrder())
                                                     .to(VavrConverters::TreeMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.containsKey(1),list.toString());
        assertTrue(list.containsKey(3),list.toString());

    }

    @Test
    public void treeMapAlien(){

        TreeMap<Integer,Integer> list = PersistentMapXs.of(1, 2, 3,4)
                                                        .to(VavrConverters::TreeMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.containsKey(1),list.toString());
        assertTrue(list.containsKey(3),list.toString());
    }

}