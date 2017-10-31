package cyclops;

import cyclops.collections.dexx.DexxHashSetX;
import cyclops.collections.dexx.DexxListX;
import cyclops.collections.dexx.DexxTreeSetX;
import cyclops.collections.dexx.DexxVectorX;
import cyclops.function.Reducer;
import org.pcollections.*;

import java.util.Comparator;


public class DexxTypes {

    /**
     * Use to set the type of a LinkedListX to Dexx List
     * <pre>
     *     {@code
     *     List<Integer> list = LinkedListX.of(1,2,3)
                                           .type(cyclops.DexxTypes.list())
                                           .map(i->i*2)
                                           .to(cyclops.DexxConverters::List);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Dexx List that implements PersistentList interface
     */
    public static <T> Reducer<PersistentList<T>> list() {
        return DexxListX.toPersistentList();
    }
    /**
     * Use to set the type of a VectorX to Dexx Vector
     * <pre>
     *     {@code
     *     Vector<Integer> list = VectorX.of(1,2,3)
                                         .type(cyclops.DexxTypes.list())
                                         .map(i->i*2)
                                         .to(cyclops.DexxConverters::Vector);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Dexx Vector that implements PersistentList interface
     */
    public static <T> Reducer<PersistentList<T>> vector() {
        return DexxVectorX.toPersistentList();
    }

    /**
     * Use to set the type of an OrderedSetX to Dexx TreeSet
     * <pre>
     *     {@code
     *     TreeSet<Integer> set =  OrderedSetX.of(1,2,3)
     *                                        .type(cyclops.DexxTypes.treeSet(Comparator.naturalOrdering()))
     *                                        .map(i->i*2)
     *                                        .to(cyclops.DexxConverters::TreeSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Dexx Set that implements PersistentSortedSet interface
     */
    public static <T> Reducer<PersistentSortedSet<T>> treeSet(Comparator<T> ordering) {
        return DexxTreeSetX.toPersistentSortedSet(ordering);
    }
    public static <T extends Comparable<T>> Reducer<PersistentSortedSet<T>> treeSet() {
        return DexxTypes.<T>treeSet(Comparator.naturalOrder());
    }

    /**
     * Use to set the type of an PersistentSetX to Dexx TreeSet
     * <pre>
     *     {@code
     *     HashSet<Integer> set =  PersistentSetX.of(1,2,3)
     *                                           .type(cyclops.DexxTypes.hashSet(Comparator.naturalOrdering()))
     *                                           .map(i->i*2)
     *                                           .to(cyclops.DexxConverters::HashSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Dexx Set that implements PSet interface
     */
    public static <T> Reducer<PersistentSet<T>> hashSet() {
        return DexxHashSetX.toPSet();
    }
}
