package cyclops;

import com.aol.cyclops2.data.collections.extensions.CollectionX;
import com.aol.cyclops2.types.foldable.To;
import cyclops.collections.clojure.*;
import cyclops.collections.immutable.PersistentMapX;
import clojure.lang.*;
import java.util.Comparator;

import java.util.HashMap;
import java.util.TreeMap;
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


    public static <K,V> PersistentHashMap PersistentHashMap(PersistentMapX<K,V> vec){
        return vec.unwrapNested(PersistentHashMap.class,
                ()->{
                    ClojureHashMapX<K,V> map = ClojureHashMapX.copyFromMap(vec).unwrap();
                    return map.unwrap();
                } );
    }
    public static <K extends Comparable<K>,V> PersistentTreeMap PersistentTreeMap(PersistentMapX<K,V> vec){
        return vec.unwrapNested(PersistentTreeMap.class,
                ()->{
                    ClojureTreeMapX<K,V> map = ClojureTreeMapX.copyFromMap((Comparator<K>) Comparator.naturalOrder(),vec).unwrap();
                    return map.unwrap();
                } );
    }
    public static <K,V> PersistentTreeMap PersistentTreeMap(PersistentMapX<K,V> vec, Comparator<K> comp){
        return vec.unwrapNested(TreeMap.class,
                ()->{
                    ClojureTreeMapX<K,V> map = ClojureTreeMapX.copyFromMap(comp,vec).unwrap();
                    return map.unwrap();
                } );
    }
    public static <T extends Comparable<? extends T>> PersistentTreeSet PersistentTreeSet(CollectionX<T> vec){
        return vec.unwrapNested(PersistentTreeSet.class,
                ()->{
                    ClojureTreeSetX<T> set = ClojureTreeSetX.copyFromCollection(vec,(Comparator<T>)Comparator.naturalOrder()).unwrap();
                    return set.unwrap();
                });
    }
    public static <T> PersistentHashSet PersistentHashSet(CollectionX<T> vec){
        return vec.unwrapNested(PersistentHashSet.class,
                ()-> {
                    ClojureHashSetX<T> set = ClojureHashSetX.copyFromCollection(vec).unwrap();
                    return set.unwrap();
                });
    }
    public static <T> PersistentTreeSet PersistentTreeSet(CollectionX<T> vec, Comparator<T> comp) {
        return vec.unwrapNested(PersistentTreeSet.class,
                () -> {
                    ClojureTreeSetX<T> set = ClojureTreeSetX.copyFromCollection(vec, comp).unwrap();
                    return set.unwrap();
                });
    }
    public static <T> PersistentHashSet PersistentHashSet(CollectionX<T> vec, Comparator<T> comp) {
        return vec.unwrapNested(PersistentHashSet.class,
                () -> {
                    ClojureHashSetX<T> set = ClojureHashSetX.copyFromCollection(vec).unwrap();
                    return set.unwrap();
                });
    }
    
    public static <T> PersistentQueue PersistentQueue(CollectionX<T> vec){
        return vec.unwrapNested(PersistentQueue.class,
                ()-> {
                    ClojureQueueX<T> queue = ClojureQueueX.copyFromCollection(vec).unwrap();
                    return queue.unwrap();
                });
    }
    public static <T> PersistentList PersistentList(CollectionX<T> vec){
        return vec.unwrapNested(PersistentList.class,
                ()-> {
                    ClojureListX<T> vector =  ClojureListX.copyFromCollection(vec).unwrap();
                    return vector.unwrap();
                });
    }

    public static <T> PersistentVector PersistentVector(CollectionX<T> vec){
        return vec.unwrapNested(PersistentVector.class,
                ()-> {
                    ClojureVectorX<T> vector =  ClojureVectorX.copyFromCollection(vec).unwrap();
                    return vector.unwrap();
                });
    }
    

}
