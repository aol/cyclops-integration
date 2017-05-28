package cyclops;

import com.aol.cyclops2.data.collections.extensions.CollectionX;
import com.aol.cyclops2.types.foldable.To;
import cyclops.collections.clojure.*;
import cyclops.collections.immutable.PersistentMapX;
import clojure.lang.*;
import java.util.Comparator;

import java.util.HashMap;
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
public interface ClojureConverters {



    public static <K,V> PersistentHashMap PersistentHashMap(PersistentMapX<K, V> vec){
        return vec.unwrapIfInstance(HashMap.class,
                ()-> ClojureHashMapX.copyFromMap(vec).unwrap());
    }
    public static <T extends Comparable<? extends T>> PersistentTreeSet PersistentTreeSet(CollectionX<T> vec){
        return vec.unwrapIfInstance(PersistentTreeSet.class,
                ()-> ClojureTreeSetX.copyFromCollection(vec,(Comparator<T>)Comparator.naturalOrder()).unwrap());
    }
    public static <T> PersistentHashSet PersistentHashSet(CollectionX<T> vec){
        return vec.unwrapIfInstance(PersistentHashSet.class,
                ()-> ClojureHashSetX.copyFromCollection(vec).unwrap());
    }
    public static <T> PersistentTreeSet PersistentTreeSet(CollectionX<T> vec, Comparator<T> comp){
        return vec.unwrapIfInstance(PersistentTreeSet.class,
                ()-> ClojureTreeSetX.copyFromCollection(vec,comp).unwrap());
    }


    public static <T> PersistentQueue PersistentQueue(CollectionX<T> vec){
        return vec.unwrapIfInstance(PersistentQueue.class,
                ()-> ClojureQueueX.copyFromCollection(vec).unwrap());
    }
    public static <T> PersistentList List(CollectionX<T> vec){
        return vec.unwrapIfInstance(PersistentList.class,
                ()-> ClojureListX.copyFromCollection(vec).unwrap());
    }
    public static <T> PersistentVector PersistentVector(CollectionX<T> vec){
        return vec.unwrapIfInstance(PersistentVector.class,
                ()-> ClojureVectorX.copyFromCollection(vec).unwrap());
    }


}
