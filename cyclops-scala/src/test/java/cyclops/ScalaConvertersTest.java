package cyclops;

import cyclops.collections.immutable.*;
import cyclops.collections.mutable.ListX;
import cyclops.collections.mutable.QueueX;
import cyclops.collections.scala.ScalaHashMapX;
import cyclops.collections.scala.ScalaListX;
import cyclops.collections.scala.ScalaTreeMapX;
import cyclops.collections.scala.ScalaVectorX;
import cyclops.companion.MapXs;
import cyclops.companion.PersistentMapXs;
import org.junit.Test;
import scala.collection.immutable.*;

import java.util.Comparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.*;

/**
 * Created by johnmcclean on 25/05/2017.
 */
public class ScalaConvertersTest {

    @Test
    public void listNative(){
//materializeList makes use of ConsPStack
        List<Integer> list = LinkedListX.of(1,2,3)
                                        .type(ScalaListX.toPStack())
                                        .map(i->i*2)
                                        .to(ScalaConverters::List);

        assertThat(list.toString(), equalTo( "List(2, 4, 6)"));
    }
    @Test
    public void listAlien(){
//materializeList makes use of ConsPStack
        List<Integer> list = LinkedListX.of(1,2,3)
                .map(i->i*2)
                .to(ScalaConverters::List);

        assertThat(list.toString(), equalTo( "List(2, 4, 6)"));
    }
    @Test
    public void vectorNative() {

        Vector<Integer> list = VectorX.of(1, 2, 3)
                .type(ScalaTypes.vector())
                .map(i -> i * 2)
                .to(ScalaConverters::Vector);

        assertThat(list.toString(), equalTo("Vector(2, 4, 6)"));
    }
    @Test
    public void vectorAlien(){

        Vector<Integer> list = VectorX.of(1,2,3)
                                      .map(i->i*2)
                .to(ScalaConverters::Vector);

        assertThat(list.toString(), equalTo("Vector(2, 4, 6)"));
    }

    @Test
    public void queueNative() {

        Queue<Integer> list = PersistentQueueX.of(1, 2, 3)
                                              .type(ScalaTypes.queue())
                                              .map(i -> i * 2)
                                              .to(ScalaConverters::Queue);

        assertThat(list.toString(), equalTo("Queue(2, 4, 6)"));
    }
    @Test
    public void queueAlien(){

        Queue<Integer> list = PersistentQueueX.of(1,2,3)
                .map(i->i*2)
                .to(ScalaConverters::Queue);

        assertThat(list.toString(), equalTo("Queue(2, 4, 6)"));
    }
    @Test
    public void hashSetNative() {

        HashSet<Integer> list = PersistentSetX.of(1, 2, 3)
                .type(ScalaTypes.hashSet())
                .map(i -> i * 2)
                .to(ScalaConverters::HashSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void hashSetAlien(){

        HashSet<Integer> list = PersistentSetX.of(1,2,3)
                                              .map(i->i*2)
                                              .to(ScalaConverters::HashSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void treeSetNative() {

        TreeSet<Integer> list = OrderedSetX.of(1, 2, 3)
                                           .type(ScalaTypes.treeSet())
                                            .map(i -> i * 2)
                                           .to(ScalaConverters::TreeSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void treeSetAlien(){

        TreeSet<Integer> list = OrderedSetX.of(1, 2, 3)
                .map(i->i*2)
                .to(ScalaConverters::TreeSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void bitSetNative() {

        BitSet list = OrderedSetX.of(1, 2, 3)
                .type(ScalaTypes.bitset())
                .map(i -> i * 2)
                .to(ScalaConverters::BitSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void bitSetAlien(){

        BitSet list = OrderedSetX.of(1, 2, 3)
                .map(i->i*2)
                .to(ScalaConverters::BitSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }

    @Test
    public void hashMapNative() {

        HashMap<Integer,Integer> list = ScalaHashMapX.copyFromMap(MapXs.of(1,2,3,4))
                                                       .to(ScalaConverters::HashMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.contains(1),list.toString());
        assertTrue(list.contains(3),list.toString());

    }

    @Test
    public void hashMapAlien(){

        HashMap<Integer,Integer> list = PersistentMapXs.of(1, 2, 3,4)
                .to(ScalaConverters::HashMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.contains(1),list.toString());
        assertTrue(list.contains(3),list.toString());
    }
    @Test
    public void treeMapNative() {

        TreeMap<Integer,Integer> list = ScalaTreeMapX.copyFromMap(MapXs.of(1,2,3,4), Comparator.naturalOrder())
                                                     .to(ScalaConverters::TreeMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.contains(1),list.toString());
        assertTrue(list.contains(3),list.toString());

    }

    @Test
    public void treeMapAlien(){

        TreeMap<Integer,Integer> list = PersistentMapXs.of(1, 2, 3,4)
                                                        .to(ScalaConverters::TreeMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.contains(1),list.toString());
        assertTrue(list.contains(3),list.toString());
    }

}