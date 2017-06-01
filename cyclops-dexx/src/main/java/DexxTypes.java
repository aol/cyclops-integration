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
                                           .type(DexxTypes.list())
                                           .map(i->i*2)
                                           .to(DexxConverters::List);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Dexx List that implements PStack interface
     */
    public static <T> Reducer<PStack<T>> list() {
        return DexxListX.toPStack();
    }
    /**
     * Use to set the type of a VectorX to Dexx Vector
     * <pre>
     *     {@code
     *     Vector<Integer> list = VectorX.of(1,2,3)
                                         .type(DexxTypes.list())
                                         .map(i->i*2)
                                         .to(DexxConverters::Vector);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Dexx Vector that implements PVector interface
     */
    public static <T> Reducer<PVector<T>> vector() {
        return DexxVectorX.toPVector();
    }

    /**
     * Use to set the type of an OrderedSetX to Dexx TreeSet
     * <pre>
     *     {@code
     *     TreeSet<Integer> set =  OrderedSetX.of(1,2,3)
     *                                        .type(DexxTypes.treeSet(Comparator.naturalOrdering()))
     *                                        .map(i->i*2)
     *                                        .to(DexxConverters::TreeSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Dexx Set that implements POrderedSet interface
     */
    public static <T> Reducer<POrderedSet<T>> treeSet(Comparator<T> ordering) {
        return DexxTreeSetX.toPOrderedSet(ordering);
    }

    /**
     * Use to set the type of an PersistentSetX to Dexx TreeSet
     * <pre>
     *     {@code
     *     HashSet<Integer> set =  PersistentSetX.of(1,2,3)
     *                                           .type(DexxTypes.hashSet(Comparator.naturalOrdering()))
     *                                           .map(i->i*2)
     *                                           .to(DexxConverters::HashSet);
     *     }
     *
     * </pre>
     *
     * @param <T> Data type
     * @return Reducer to convert a sequence of data to a Dexx Set that implements PSet interface
     */
    public static <T> Reducer<PSet<T>> hashSet() {
        return DexxHashSetX.toPSet();
    }
}
