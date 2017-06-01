package cyclops.collections.scala;

import java.util.*;


import com.aol.cyclops.scala.collections.Converters;
import com.aol.cyclops.scala.collections.HasScalaCollection;
import com.aol.cyclops2.data.collections.extensions.ExtensiblePMapX;
import com.aol.cyclops2.types.Unwrapable;
import com.aol.cyclops2.types.mixins.TupleWrapper;
import cyclops.collections.immutable.PersistentMapX;
import cyclops.control.Eval;
import cyclops.function.Reducer;
import cyclops.stream.ReactiveSeq;
import org.pcollections.PMap;



import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.JavaConverters;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.MapLike;
import scala.collection.immutable.TreeMap;
import scala.collection.immutable.TreeMap$;
import scala.collection.mutable.Builder;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScalaTreeMapX<K,V> extends AbstractMap<K,V> implements PMap<K,V>, HasScalaCollection, Unwrapable {
    
    @Wither
    TreeMap<K,V> map;

    @Override
    public <R> R unwrap() {
        return (R)map;
    }

    public static <K, V> Reducer<PersistentMapX<K, V>> toPersistentMapX(Comparator<? super K> c) {
        return Reducer.<PersistentMapX<K, V>> of(empty(c), (final PersistentMapX<K, V> a) -> b -> a.plusAll(b), (in) -> {
            final List w = ((TupleWrapper) () -> in).values();
            return singleton(c,(K) w.get(0), (V) w.get(1));
        });
    }
    public static <K,V> PersistentMapX<K,V> copyFromMap(Map<K,V> map,Comparator<? super K> c){
        return ScalaTreeMapX.<K,V>empty(c)
                .plusAll(map);
    }
    public static <K,V> ScalaTreeMapX<K,V> fromMap(TreeMap<K,V> map){
        return new ScalaTreeMapX<>(map);
    }
    public static <K extends Comparable<? super K>,V> PersistentMapX<K,V> empty(){
        Comparator<K> comp = Comparator.naturalOrder();
       return new ExtensiblePMapX<K,V>(fromMap(TreeMap$.MODULE$.empty(Converters.ordering(comp))),
               Eval.later (()-> ScalaTreeMapX.<K,V>toPersistentMapX(Comparator.naturalOrder())));
    }
    public static <K,V> PersistentMapX<K,V> empty(Comparator<? super K> c){
        Comparator<K> comp = (Comparator<K>)c;
       return new ExtensiblePMapX<K,V>(fromMap(TreeMap$.MODULE$.empty(Converters.ordering(comp))),
               Eval.later (()-> ScalaTreeMapX.<K,V>toPersistentMapX(comp)));
    }
    public static <K,V> PMap<K,V> singletonPMap(Comparator<? super K> c,K key,V value){
        Comparator<K> comp = (Comparator<K>)c;
        Builder<Tuple2<K, V>, TreeMap> builder = TreeMap$.MODULE$.newBuilder(Converters.ordering(comp));
        TreeMap<K,V> map = builder.$plus$eq(Tuple2.apply(key,value)).result();
        return fromMap(map);
     }
    public static <K,V> PersistentMapX<K,V> singleton(Comparator<? super K> c,K key,V value){
        Comparator<K> comp = (Comparator<K>)c;
        Builder<Tuple2<K, V>, TreeMap> builder = TreeMap$.MODULE$.newBuilder(Converters.ordering(comp));
        TreeMap<K,V> map = builder.$plus$eq(Tuple2.apply(key,value)).result();
        return new ExtensiblePMapX<K,V>(fromMap(map),Eval.later (()-> ScalaTreeMapX.<K,V>toPersistentMapX(c)));
     }
    
    public static <K,V> PersistentMapX<K,V> fromStream(Comparator<? super K> c,ReactiveSeq<Tuple2<K,V>> stream){
        return stream.mapReduce(toPersistentMapX(c));
    }
    
    @Override
    public PMap<K, V> plus(K key, V value) {
        return withMap(map.$plus(Tuple2.apply(key,value)));
    }
    @Override
    public PMap<K, V> plusAll(java.util.Map<? extends K, ? extends V> m) {
         TreeMap<K,V> use = map;
         
         if(m instanceof ScalaTreeMapX){
             TreeMap<K,V> add = ((ScalaTreeMapX)m).map;
             use = (TreeMap<K, V>) use.$plus$plus(map);
         }
         if(m instanceof ScalaHashMapX){
             HashMap<K,V> add = ((ScalaHashMapX)m).map;
             use = (TreeMap<K, V>) use.$plus$plus(map);
         }
         else{
             for(java.util.Map.Entry<? extends K, ? extends V> next : m.entrySet()){
                 use = use.$plus(Tuple2.apply(next.getKey(),next.getValue()));
             }
         }
        return withMap(use);
    }
    @Override
    public PMap<K, V> minus(Object key) {
      
        TreeMap m = map;
        return withMap((TreeMap)m.$minus(key));
     
    }
   
    @Override
    public PMap<K, V> minusAll(Collection<?> keys) {
        GenTraversableOnce gen =  HasScalaCollection.traversable(keys);
        MapLike m = map;
        return withMap((TreeMap)m.$minus$minus(gen));
        
    }
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return ReactiveSeq.fromIterable(JavaConverters.asJavaIterable(map))
                        .map(t->(java.util.Map.Entry<K,V>)new SimpleEntry<K,V>(t._1,t._2))
                        .toSet();
    }
    @Override
    public GenTraversableOnce traversable() {
        return map;
    }
    @Override
    public CanBuildFrom canBuildFrom() {
       return TreeMap.canBuildFrom(map.ordering());
    }
    /* (non-Javadoc)
     * @see java.util.AbstractMap#get(java.lang.Object)
     */
    @Override
    public V get(Object key) {
        
        return map.apply((K)key);
    }
   
}
