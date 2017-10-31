package cyclops;

import com.oath.cyclops.data.collections.extensions.CollectionX;
import com.oath.cyclops.data.collections.extensions.lazy.immutable.LazyLinkedListX;
import com.oath.cyclops.types.foldable.To;
import cyclops.collections.immutable.PersistentMapX;
import cyclops.collections.scala.*;
import cyclops.companion.Reducers;
import cyclops.control.Option;
import org.pcollections.*;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.immutable.*;


import java.util.Comparator;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
public interface ScalaConverters {

  public static <T> java.util.Iterator<T> iterator(Iterator<T> it){
    return new java.util.Iterator<T>() {
      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public T next() {
        return it.next();
      }
    }
  }

  public static <T> Option<T> toCyclopsOption(scala.Option<T> o){
    return o.fold(()->Option.none(),b->Option.some(b));
  }

    public static <K,V> HashMap<K,V> HashMap(PersistentMapX<K,V> vec){
        return vec.unwrapNested(HashMap.class,
                ()->{
                   ScalaHashMapX<K,V> map = ScalaHashMapX.copyFromMap(vec).unwrap();
                   return map.unwrap();
                } );
    }
    public static <K extends Comparable<K>,V> TreeMap<K,V> TreeMap(PersistentMapX<K,V> vec){
        return vec.unwrapNested(TreeMap.class,
                ()->{
                    ScalaTreeMapX<K,V> map = ScalaTreeMapX.copyFromMap(vec,(Comparator<K>) Comparator.naturalOrder()).unwrap();
                    return map.unwrap();
                } );
    }
    public static <K,V> TreeMap<K,V> TreeMap(PersistentMapX<K,V> vec, Comparator<K> comp){
        return vec.unwrapNested(TreeMap.class,
                ()->{
                    ScalaTreeMapX<K,V> map = ScalaTreeMapX.copyFromMap(vec,comp).unwrap();
                    return map.unwrap();
                } );
    }
    public static <T extends Comparable<? extends T>> TreeSet<T> TreeSet(CollectionX<T> vec){
        return vec.unwrapNested(TreeSet.class,
                ()->{
                     ScalaTreeSetX<T> set = ScalaTreeSetX.copyFromCollection(vec,(Comparator<T>)Comparator.naturalOrder()).unwrap();
                     return set.unwrap();
                });
    }
    public static <T> HashSet<T> HashSet(CollectionX<T> vec){
        return vec.unwrapNested(HashSet.class,
                ()-> {
                    ScalaHashSetX<T> set = ScalaHashSetX.copyFromCollection(vec, (Comparator<T>) Comparator.naturalOrder()).unwrap();
                    return set.unwrap();
                });
    }
    public static <T> TreeSet<T> TreeSet(CollectionX<T> vec, Comparator<T> comp) {
        return vec.unwrapNested(TreeSet.class,
                () -> {
                    ScalaTreeSetX<T> set = ScalaTreeSetX.copyFromCollection(vec, comp).unwrap();
                    return set.unwrap();
                });
    }
    public static <T> HashSet<T> HashSet(CollectionX<T> vec, Comparator<T> comp) {
        return vec.unwrapNested(HashSet.class,
                () -> {
                    ScalaHashSetX<T> set = ScalaHashSetX.copyFromCollection(vec, comp).unwrap();
                    return set.unwrap();
                });
    }
    public static  BitSet BitSet(CollectionX<Integer> vec){
        return vec.unwrapNested(BitSet.class,
                ()->{
                   ScalaBitSetX set = ScalaBitSetX.copyFromCollection(vec).unwrap();
                    return set.unwrap();
                } );
    }
    public static <T> Queue<T> Queue(CollectionX<T> vec){
        return vec.unwrapNested(Queue.class,
                ()-> {
                    ScalaQueueX<T> queue = ScalaQueueX.copyFromCollection(vec).unwrap();
                    return queue.unwrap();
                });
    }
    public static <T> List<T> List(CollectionX<T> vec){
        return vec.unwrapNested(List.class,
                ()-> {
                    ScalaListX<T> vector =  ScalaListX.copyFromCollection(vec).unwrap();
                    return vector.unwrap();
                });
    }

    public static <T> Vector<T> Vector(CollectionX<T> vec){
        return vec.unwrapNested(Vector.class,
                ()-> {
                    ScalaVectorX<T> vector =  ScalaVectorX.copyFromCollection(vec).unwrap();
                    return vector.unwrap();
                });
    }


  public static <K, V> cyclops.data.tuple.Tuple2<K,V> toCyclopsTuple2(Tuple2<K, V> t) {
      return cyclops.data.tuple.Tuple.tuple(t._1(),t._2());
  }
}
