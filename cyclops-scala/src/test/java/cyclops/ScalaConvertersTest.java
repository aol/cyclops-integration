package cyclops;

import cyclops.collections.immutable.*;
import cyclops.collections.mutable.ListX;
import cyclops.collections.mutable.QueueX;
import cyclops.collections.scala.ScalaListX;
import cyclops.collections.scala.ScalaVectorX;
import org.junit.Test;
import scala.collection.immutable.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.*;

/**
 * Created by johnmcclean on 25/05/2017.
 */
public class ScalaConvertersTest {

    @Test
    public void list(){
//materializeList makes use of ConsPStack
        List<Integer> list = LinkedListX.of(1,2,3)
                                        .type(ScalaListX.toPStack())
                                        .map(i->i*2).materialize()
                                        .to(ScalaConverters::List2);

        System.out.println(list);
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
}