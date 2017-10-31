package cyclops;

import com.oath.cyclops.types.persistent.PersistentSet;
import cyclops.collections.clojure.*;
import cyclops.function.Reducer;
import org.pcollections.*;

import java.util.Comparator;


public class ClojureTypes {

    /**
     * Use to set the type of a LinkedListX to Clojure List
     * <pre>
     *     {@code
     *     PersistentList list = LinkedListX.of(1,2,3)
                                           .type(ClojureTypes.list())
                                           .map(i->i*2)
                                           .to(ClojureConverters::List);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Clojure List that implements PersistentList interface
     */
    public static <T> Reducer<PersistentList<T>> list() {
        return ClojureListX.toPersistentList();
    }
    /**
     * Use to set the type of a VectorX to Clojure Vector
     * <pre>
     *     {@code
     *     PersistentVector list = VectorX.of(1,2,3)
                                         .type(ClojureTypes.list())
                                         .map(i->i*2)
                                         .to(ClojureConverters::Vector);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Clojure Vector that implements PersistentList interface
     */
    public static <T> Reducer<PersistentList<T>> vector() {
        return ClojureVectorX.toPersistentList();
    }

    /**
     * Use to set the type of a QueueX to Clojure Queue
     * <pre>
     *     {@code
     *     PersistentQueue list =  QueueX.of(1,2,3)
     *                                  .type(ClojureTypes.queue())
     *                                  .map(i->i*2)
     *                                  .to(ClojureConverters::Queue);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Clojure List that implements PersistentQueue interface
     */
    public static <T> Reducer<PersistentQueue<T>> queue() {
        return ClojureQueueX.toPersistentQueue();
    }
    /**
     * Use to set the type of an OrderedSetX to Clojure TreeSet
     * <pre>
     *     {@code
     *     PersistentTreeSet set =  OrderedSetX.of(1,2,3)
     *                                        .type(ClojureTypes.treeSet(Comparator.naturalOrdering()))
     *                                        .map(i->i*2)
     *                                        .to(ClojureConverters::TreeSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Clojure Set that implements PersistentSortedSet interface
     */
    public static <T> Reducer<PersistentSortedSet<T>> treeSet(Comparator<T> ordering) {
        return ClojureTreeSetX.toPersistentSortedSet(ordering);
    }
    public static <T extends Comparable<T>> Reducer<PersistentSortedSet<T>> treeSet() {
        return ClojureTreeSetX.<T>toPersistentSortedSet(Comparator.naturalOrder());
    }

    /**
     * Use to set the type of an PersistentSetX to Clojure TreeSet
     * <pre>
     *     {@code
     *     PersistentHashSet<Integer> set =  PersistentSetX.of(1,2,3)
     *                                           .type(ClojureTypes.hashSet(Comparator.naturalOrdering()))
     *                                           .map(i->i*2)
     *                                           .to(ClojureConverters::HashSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Clojure Set that implements PSet interface
     */
    public static <T> Reducer<PersistentSet<T>> hashSet() {
        return ClojureHashSetX.toPSet();
    }
}
