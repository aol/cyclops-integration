package cyclops;

import com.github.andrewoma.dexx.collection.HashSet;
import com.github.andrewoma.dexx.collection.List;
import com.github.andrewoma.dexx.collection.TreeSet;
import com.github.andrewoma.dexx.collection.Vector;
import cyclops.collections.dexx.DexxListX;
import cyclops.collections.immutable.*;


import cyclops.companion.MapXs;
import cyclops.companion.PersistentMapXs;
import org.junit.Test;

import java.util.Comparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertTrue;

/**
 * Created by johnmcclean on 25/05/2017.
 */
public class DexxConvertersTest {

    @Test
    public void listNative(){
//materializeList makes use of ConsPersistentList
        List<Integer> list = LinkedListX.of(1,2,3)
                                        .type(DexxListX.toPersistentList())
                                        .map(i->i*2)
                                        .to(DexxConverters::List);

        assertThat(list.toString(), equalTo( "Cons(2, 4, 6)"));
    }
    @Test
    public void listAlien(){
//materializeList makes use of ConsPersistentList
        List<Integer> list = LinkedListX.of(1,2,3)
                .map(i->i*2)
                .to(DexxConverters::List);

        assertThat(list.toString(), equalTo( "Cons(2, 4, 6)"));
    }
    @Test
    public void vectorNative() {

        Vector<Integer> list = VectorX.of(1, 2, 3)
                .type(DexxTypes.vector())
                .map(i -> i * 2)
                .to(DexxConverters::Vector);

        assertThat(list.toString(), equalTo("Vector(2, 4, 6)"));
    }
    @Test
    public void vectorAlien(){

        Vector<Integer> list = VectorX.of(1,2,3)
                                      .map(i->i*2)
                .to(DexxConverters::Vector);

        assertThat(list.toString(), equalTo("Vector(2, 4, 6)"));
    }


    @Test
    public void hashSetNative() {

        HashSet<Integer> list = PersistentSetX.of(1, 2, 3)
                .type(DexxTypes.hashSet())
                .map(i -> i * 2)
                .to(DexxConverters::HashSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void hashSetAlien(){

        HashSet<Integer> list = PersistentSetX.of(1,2,3)
                                              .map(i->i*2)
                                              .to(DexxConverters::HashSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void treeSetNative() {

        TreeSet<Integer> list = OrderedSetX.of(1, 2, 3)
                                           .type(DexxTypes.treeSet())
                                            .map(i -> i * 2)
                                           .to(DexxConverters::TreeSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }
    @Test
    public void treeSetAlien(){

        TreeSet<Integer> list = OrderedSetX.of(1, 2, 3)
                .map(i->i*2)
                .to(DexxConverters::TreeSet);

        assertThat(list.size(), equalTo(3));
        assertTrue(list.contains(2),list.toString());
        assertTrue(list.contains(4),list.toString());
        assertTrue(list.contains(6),list.toString());
    }


}
