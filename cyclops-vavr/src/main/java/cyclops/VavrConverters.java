package cyclops;

import com.oath.cyclops.data.collections.extensions.CollectionX;
import com.oath.cyclops.types.foldable.To;
import cyclops.collections.immutable.PersistentMapX;
import cyclops.collections.vavr.*;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;

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

  public static <T> cyclops.control.Option<T> toCyclopsOption(Option<T> opt){
    return opt.map(s->cyclops.control.Option.some(s)).getOrElse(cyclops.control.Option.none());
  }
  public static <K, V> cyclops.data.tuple.Tuple2<K,V> toCyclopsTuple2(Tuple2<K, V> t) {
    return cyclops.data.tuple.Tuple.tuple(t._1(),t._2());
  }

    public static <K,V> HashMap<K,V> HashMap(PersistentMapX<K,V> vec){
        return vec.unwrapNested(HashMap.class,
                ()->{
                    VavrHashMapX<K,V> map = VavrHashMapX.copyFromMap(vec).unwrap();
                    return map.unwrap();
                } );
    }
    public static <K extends Comparable<K>,V> TreeMap<K,V> TreeMap(PersistentMapX<K,V> vec){
        return vec.unwrapNested(TreeMap.class,
                ()->{
                    VavrTreeMapX<K,V> map = VavrTreeMapX.copyFromMap(vec,(Comparator<K>) Comparator.naturalOrder()).unwrap();
                    return map.unwrap();
                } );
    }
    public static <K,V> TreeMap<K,V> TreeMap(PersistentMapX<K,V> vec, Comparator<K> comp){
        return vec.unwrapNested(TreeMap.class,
                ()->{
                    VavrTreeMapX<K,V> map = VavrTreeMapX.copyFromMap(vec,comp).unwrap();
                    return map.unwrap();
                } );
    }
    public static <T extends Comparable<? extends T>> TreeSet<T> TreeSet(CollectionX<T> vec){
        return vec.unwrapNested(TreeSet.class,
                ()->{
                    VavrTreeSetX<T> set = VavrTreeSetX.copyFromCollection(vec,(Comparator<T>)Comparator.naturalOrder()).unwrap();
                    return set.unwrap();
                });
    }
    public static <T> HashSet<T> HashSet(CollectionX<T> vec){
        return vec.unwrapNested(HashSet.class,
                ()-> {
                    VavrHashSetX<T> set = VavrHashSetX.copyFromCollection(vec).unwrap();
                    return set.unwrap();
                });
    }
    public static <T> TreeSet<T> TreeSet(CollectionX<T> vec, Comparator<T> comp) {
        return vec.unwrapNested(TreeSet.class,
                () -> {
                    VavrTreeSetX<T> set = VavrTreeSetX.copyFromCollection(vec, comp).unwrap();
                    return set.unwrap();
                });
    }

    public static  BitSet BitSet(CollectionX<Integer> vec){
        return vec.unwrapNested(BitSet.class,
                ()->{
                    VavrBitSetX set = VavrBitSetX.copyFromCollection(vec).unwrap();
                    return set.unwrap();
                } );
    }
    public static <T> Queue<T> Queue(CollectionX<T> vec){
        return vec.unwrapNested(Queue.class,
                ()-> {
                    VavrQueueX<T> queue = VavrQueueX.copyFromCollection(vec).unwrap();
                    return queue.unwrap();
                });
    }
    public static <T> List<T> List(CollectionX<T> vec){
        return vec.unwrapNested(List.class,
                ()-> {
                    VavrListX<T> vector =  VavrListX.copyFromCollection(vec).unwrap();
                    return vector.unwrap();
                });
    }



    public static <T> Vector<T> Vector(CollectionX<T> vec){
        return vec.unwrapNested(Vector.class,
                ()-> {
                    VavrVectorX<T> vector =  VavrVectorX.copyFromCollection(vec).unwrap();
                    return vector.unwrap();
                });
    }

}
