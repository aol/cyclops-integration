package cyclops;

import clojure.lang.*;
import cyclops.collections.clojure.ClojureHashMapX;
import cyclops.collections.clojure.ClojureListX;
import cyclops.collections.clojure.ClojureTreeMapX;
import cyclops.collections.immutable.*;

import cyclops.collections.mutable.ListX;
import cyclops.companion.MapXs;
import cyclops.companion.PersistentMapXs;
import cyclops.monads.Witness;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertTrue;

/**
 * Created by johnmcclean on 25/05/2017.
 */
public class ClojureConvertersTest {

    @Test
    public void listNative(){
//materializeList makes use of ConsPersistentList
        PersistentList list = LinkedListX.of(1,2,3)
                                        .type(ClojureListX.toPersistentList())
                                        .map(i->i*2)
                                        .to(ClojureConverters::PersistentList);

        assertThat(ListX.fromIterable(list), equalTo(ListX.of(2,4,6)));
    }
    @Test
    public void listAlien(){
//materializeList makes use of ConsPersistentList
        PersistentList list = LinkedListX.of(1,2,3)
                .map(i->i*2)
                .to(ClojureConverters::PersistentList);

        assertThat(ListX.fromIterable(list), equalTo(ListX.of(2,4,6)));
    }
    @Test
    public void vectorNative() {

        PersistentVector list = VectorX.of(1, 2, 3)
                .type(ClojureTypes.vector())
                .map(i -> i * 2)
                .to(ClojureConverters::PersistentVector);

        assertThat(ListX.fromIterable(list), equalTo(ListX.of(2,4,6)));
    }
    @Test
    public void vectorAlien(){

        PersistentVector list = VectorX.of(1,2,3)
                                      .map(i->i*2)
                .to(ClojureConverters::PersistentVector);

        assertThat(ListX.fromIterable(list), equalTo(ListX.of(2,4,6)));
    }

    @Test
    public void queueNative() {

        PersistentQueue list = PersistentQueueX.of(1, 2, 3)
                                              .type(ClojureTypes.queue())
                                              .map(i -> i * 2)
                                              .to(ClojureConverters::PersistentQueue);

        assertThat(ListX.fromIterable(list), equalTo(ListX.of(2,4,6)));
    }
    @Test
    public void queueAlien(){

        PersistentQueue list = PersistentQueueX.of(1,2,3)
                .map(i->i*2)
                .to(ClojureConverters::PersistentQueue);

        assertThat(ListX.fromIterable(list), equalTo(ListX.of(2,4,6)));
    }
    @Test
    public void hashSetNative() {

        PersistentHashSet list = PersistentSetX.of(1, 2, 3)
                .type(ClojureTypes.hashSet())
                .map(i -> i * 2)
                .to(ClojureConverters::PersistentHashSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void hashSetAlien(){

        PersistentHashSet list = PersistentSetX.of(1,2,3)
                                              .map(i->i*2)
                                              .to(ClojureConverters::PersistentHashSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void treeSetNative() {

        PersistentTreeSet list = OrderedSetX.of(1, 2, 3)
                                           .type(ClojureTypes.treeSet())
                                            .map(i -> i * 2)
                                           .to(ClojureConverters::PersistentTreeSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void treeSetAlien(){

        PersistentTreeSet list = OrderedSetX.of(1, 2, 3)
                .map(i->i*2)
                .to(ClojureConverters::PersistentTreeSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }


    @Test
    public void hashMapNative() {

        PersistentHashMap list = ClojureHashMapX.copyFromMap(MapXs.of(1,2,3,4))
                                                       .to(ClojureConverters::PersistentHashMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.containsKey(1),list.toString());
        assertTrue(list.containsKey(3),list.toString());

    }

    @Test
    public void hashMapAlien(){

        PersistentHashMap list = PersistentMapXs.of(1, 2, 3,4)
                .to(ClojureConverters::PersistentHashMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.containsKey(1),list.toString());
        assertTrue(list.containsKey(3),list.toString());
    }
    @Test
    public void treeMapNative() {

        PersistentTreeMap list = ClojureTreeMapX.copyFromMap(MapXs.of(1,2,3,4))
                                                     .to(ClojureConverters::PersistentTreeMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.containsKey(1),list.toString());
        assertTrue(list.containsKey(3),list.toString());

    }

    @Test
    public void treeMapAlien(){

        PersistentTreeMap list = PersistentMapXs.of(1, 2, 3,4)
                                                        .to(ClojureConverters::PersistentTreeMap);

        assertThat(list.size(), equalTo(2));
        assertTrue(list.containsKey(1),list.toString());
        assertTrue(list.containsKey(3),list.toString());
    }

}
