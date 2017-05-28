package cyclops;

import com.aol.cyclops2.data.collections.extensions.CollectionX;
import com.aol.cyclops2.types.foldable.To;
import cyclops.collections.immutable.PersistentMapX;
import cyclops.collections.vavr.*;
import javaslang.collection.*;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Class for holding conversion methods between types
 * Use in conjunction with {@link To#to(Function)} for fluent conversions
 *
 * <pre>
 *     {@code
 *      LinkedList<Integer> list1 = ListX.of(1,2,3)
 *                                      .to(Converters::LinkedList);
        ArrayList<Integer> list2 = ListX.of(1,2,3)
                                       .to(Converters::ArrayList);
 *     }
 *
 * </pre>
 */
public interface VavrConverters {



    public static <K,V> HashMap<K,V> HashMap(PersistentMapX<K, V> vec){
        return vec.unwrapIfInstance(HashMap.class,
                ()-> VavrHashMapX.copyFromMap(vec).unwrap());
    }
    public static <T extends Comparable<? extends T>> TreeSet<T> TreeSet(CollectionX<T> vec){
        return vec.unwrapIfInstance(TreeSet.class,
                ()-> VavrTreeSetX.copyFromCollection(vec,(Comparator<T>)Comparator.naturalOrder()).unwrap());
    }
    public static <T> HashSet<T> HashSet(CollectionX<T> vec){
        return vec.unwrapIfInstance(HashSet.class,
                ()-> VavrHashSetX.copyFromCollection(vec,(Comparator<T>)Comparator.naturalOrder()).unwrap());
    }
    public static <T> TreeSet<T> TreeSet(CollectionX<T> vec, Comparator<T> comp){
        return vec.unwrapIfInstance(TreeSet.class,
                ()-> VavrTreeSetX.copyFromCollection(vec,comp).unwrap());
    }
    public static <T> HashSet<T> HashSet(CollectionX<T> vec, Comparator<T> comp){
        return vec.unwrapIfInstance(HashSet.class,
                ()-> VavrHashSetX.copyFromCollection(vec,comp).unwrap());
    }

    public static <T> Queue<T> Queue(CollectionX<T> vec){
        return vec.unwrapIfInstance(Queue.class,
                ()-> VavrQueueX.copyFromCollection(vec).unwrap());
    }
    public static <T> List<T> List(CollectionX<T> vec){
        return vec.unwrapIfInstance(List.class,
                ()-> VavrListX.copyFromCollection(vec).unwrap());
    }
    public static <T> Vector<T> Vector(CollectionX<T> vec){
        return vec.unwrapIfInstance(Vector.class,
                ()-> VavrVectorX.copyFromCollection(vec).unwrap());
    }


}
