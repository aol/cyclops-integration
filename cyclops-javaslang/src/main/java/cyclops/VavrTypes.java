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
     * @return Reducer to convert a sequence of data to a Vavr List that implements PStack interface
     */
    public static <T> Reducer<PStack<T>> list() {
        return VavrListX.toPStack();
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
     * @return Reducer to convert a sequence of data to a Vavr Vector that implements PVector interface
     */
    public static <T> Reducer<PVector<T>> vector() {
        return VavrVectorX.toPVector();
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
     * @return Reducer to convert a sequence of data to a Vavr List that implements PQueue interface
     */
    public static <T> Reducer<PQueue<T>> queue() {
        return VavrQueueX.toPQueue();
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
     * @return Reducer to convert a sequence of data to a Vavr Set that implements POrderedSet interface
     */
    public static <T> Reducer<POrderedSet<T>> treeSet(Comparator<T> ordering) {
        return VavrTreeSetX.toPOrderedSet(ordering);
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
    public static <T> Reducer<PSet<T>> hashSet() {
        return VavrHashSetX.toPSet();
    }
}
