package cyclops;

import cyclops.collections.scala.*;
import cyclops.function.Reducer;
import org.pcollections.*;

import java.util.Comparator;


public class ScalaTypes {

    /**
     * Use to set the type of a LinkedListX to Scala List
     * <pre>
     *     {@code
     *     List<Integer> list = LinkedListX.of(1,2,3)
                                           .type(ScalaTypes.list())
                                           .map(i->i*2)
                                           .to(ScalaConverters::List);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Scala List that implements PersistentList interface
     */
    public static <T> Reducer<PersistentList<T>> list() {
        return ScalaListX.toPersistentList();
    }
    /**
     * Use to set the type of a VectorX to Scala Vector
     * <pre>
     *     {@code
     *     Vector<Integer> list = VectorX.of(1,2,3)
                                         .type(ScalaTypes.list())
                                         .map(i->i*2)
                                         .to(ScalaConverters::Vector);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Scala Vector that implements PersistentList interface
     */
    public static <T> Reducer<PersistentList<T>> vector() {
        return ScalaVectorX.toPersistentList();
    }

    /**
     * Use to set the type of a QueueX to Scala Queue
     * <pre>
     *     {@code
     *     Queue<Integer> list =  QueueX.of(1,2,3)
     *                                  .type(ScalaTypes.queue())
     *                                  .map(i->i*2)
     *                                  .to(ScalaConverters::Queue);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Scala List that implements PersistentQueue interface
     */
    public static <T> Reducer<PersistentQueue<T>> queue() {
        return ScalaQueueX.toPersistentQueue();
    }
    /**
     * Use to set the type of an OrderedSetX to Scala TreeSet
     * <pre>
     *     {@code
     *     TreeSet<Integer> set =  OrderedSetX.of(1,2,3)
     *                                        .type(ScalaTypes.treeSet(Comparator.naturalOrder()))
     *                                        .map(i->i*2)
     *                                        .to(ScalaConverters::TreeSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Scala Set that implements PersistentSortedSet interface
     */
    public static <T> Reducer<PersistentSortedSet<T>> treeSet(Comparator<T> ordering) {
        return ScalaTreeSetX.toPersistentSortedSet(ordering);
    }
    /**
     * Use to set the type of an OrderedSetX to Scala TreeSet
     * <pre>
     *     {@code
     *     TreeSet<Integer> set =  OrderedSetX.of(1,2,3)
     *                                        .type(ScalaTypes.treeSet())
     *                                        .map(i->i*2)
     *                                        .to(ScalaConverters::TreeSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Scala Set that implements PersistentSortedSet interface
     */
    public static <T extends Comparable<T>> Reducer<PersistentSortedSet<T>> treeSet() {
        return ScalaTreeSetX.<T>toPersistentSortedSet(Comparator.<T>naturalOrder());
    }
    /**
     * Use to set the type of an OrderedSetX to Scala TreeSet
     * <pre>
     *     {@code
     *     BitSet<Integer> set =  OrderedSetX.of(1,2,3)
     *                                        .type(ScalaTypes.bitSet())
     *                                        .map(i->i*2)
     *                                        .to(ScalaConverters::BitSet);
     *     }
     *
     * </pre>
     *
     * @return Reducer to convert a sequence of data to a Scala Set that implements PersistentSortedSet interface
     */
    public static Reducer<PersistentSortedSet<Integer>> bitset() {
        return ScalaBitSetX.toPersistentSortedSet();
    }

    /**
     * Use to set the type of an PersistentSetX to Scala TreeSet
     * <pre>
     *     {@code
     *     HashSet<Integer> set =  PersistentSetX.of(1,2,3)
     *                                           .type(ScalaTypes.hashSet(Comparator.naturalOrdering()))
     *                                           .map(i->i*2)
     *                                           .to(ScalaConverters::HashSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Scala Set that implements PSet interface
     */
    public static <T> Reducer<PersistentSet<T>,T> hashSet() {
        return ScalaHashSetX.toPSet();
    }
}
