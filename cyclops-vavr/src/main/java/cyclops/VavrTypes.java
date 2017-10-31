package cyclops;

import cyclops.collections.vavr.*;
import cyclops.function.Reducer;
import org.pcollections.*;

import java.util.Comparator;


public class VavrTypes {

    /**
     * Use to set the type of a LinkedListX to Vavr List
     * <pre>
     *     {@code
     *     List<Integer> list = LinkedListX.of(1,2,3)
                                           .type(VavrTypes.list())
                                           .map(i->i*2)
                                           .to(VavrConverters::List);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Vavr List that implements PersistentList interface
     */
    public static <T> Reducer<PersistentList<T>> list() {
        return VavrListX.toPersistentList();
    }
    /**
     * Use to set the type of a VectorX to Vavr Vector
     * <pre>
     *     {@code
     *     Vector<Integer> list = VectorX.of(1,2,3)
                                         .type(VavrTypes.list())
                                         .map(i->i*2)
                                         .to(VavrConverters::Vector);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Vavr Vector that implements PersistentList interface
     */
    public static <T> Reducer<PersistentList<T>> vector() {
        return VavrVectorX.toPersistentList();
    }

    /**
     * Use to set the type of a QueueX to Vavr Queue
     * <pre>
     *     {@code
     *     Queue<Integer> list =  QueueX.of(1,2,3)
     *                                  .type(VavrTypes.queue())
     *                                  .map(i->i*2)
     *                                  .to(VavrConverters::Queue);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Vavr List that implements PersistentQueue interface
     */
    public static <T> Reducer<PersistentQueue<T>> queue() {
        return VavrQueueX.toPersistentQueue();
    }
    /**
     * Use to set the type of an OrderedSetX to Vavr TreeSet
     * <pre>
     *     {@code
     *     TreeSet<Integer> set =  OrderedSetX.of(1,2,3)
     *                                        .type(VavrTypes.treeSet(Comparator.naturalOrdering()))
     *                                        .map(i->i*2)
     *                                        .to(VavrConverters::TreeSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Vavr Set that implements PersistentSortedSet interface
     */
    public static <T> Reducer<PersistentSortedSet<T>> treeSet(Comparator<T> ordering) {
        return VavrTreeSetX.toPersistentSortedSet(ordering);
    }
    public static <T extends Comparable<T>> Reducer<PersistentSortedSet<T>> treeSet() {
        return VavrTreeSetX.toPersistentSortedSet(Comparator.naturalOrder());
    }

    /**
     * Use to set the type of an PersistentSetX to Vavr TreeSet
     * <pre>
     *     {@code
     *     HashSet<Integer> set =  PersistentSetX.of(1,2,3)
     *                                           .type(VavrTypes.hashSet(Comparator.naturalOrdering()))
     *                                           .map(i->i*2)
     *                                           .to(VavrConverters::HashSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Vavr Set that implements PSet interface
     */
    public static <T> Reducer<PersistentSet<T>> hashSet() {
        return VavrHashSetX.toPSet();
    }

    /**
     * Use to set the type of an OrderedSetX to Scala TreeSet
     * <pre>
     *     {@code
     *     BitSet set =  OrderedSetX.of(1,2,3)
     *                                        .type(VavrTypes.bitSet())
     *                                        .map(i->i*2)
     *                                        .to(VavrConverters::BitSet);
     *     }
     *
     * </pre>
     *
     * @return Reducer to convert a sequence of data to a Scala Set that implements PersistentSortedSet interface
     */
    public static Reducer<PersistentSortedSet<Integer>> bitset() {
        return VavrBitSetX.toPersistentSortedSet();
    }
}
