package cyclops;

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
     * @return Reducer to convert a sequence of data to a Clojure List that implements PStack interface
     */
    public static <T> Reducer<PStack<T>> list() {
        return ClojureListX.toPStack();
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
     * @return Reducer to convert a sequence of data to a Clojure Vector that implements PVector interface
     */
    public static <T> Reducer<PVector<T>> vector() {
        return ClojureVectorX.toPVector();
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
     * @return Reducer to convert a sequence of data to a Clojure List that implements PQueue interface
     */
    public static <T> Reducer<PQueue<T>> queue() {
        return ClojureQueueX.toPQueue();
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
     * @return Reducer to convert a sequence of data to a Clojure Set that implements POrderedSet interface
     */
    public static <T> Reducer<POrderedSet<T>> treeSet(Comparator<T> ordering) {
        return ClojureTreeSetX.toPOrderedSet(ordering);
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
    public static <T> Reducer<PSet<T>> hashSet() {
        return ClojureHashSetX.toPSet();
    }
}
