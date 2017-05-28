package cyclops.collections.vavr;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aol.cyclops2.data.collections.extensions.ExtensiblePMapX;
import com.aol.cyclops2.types.mixins.TupleWrapper;
import cyclops.collections.immutable.PersistentMapX;
import cyclops.control.Eval;
import cyclops.function.Reducer;
import cyclops.stream.ReactiveSeq;
import org.jooq.lambda.tuple.Tuple2;
import org.pcollections.PMap;

import javaslang.collection.TreeMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Wither;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VavrTreeMapX<K,V> extends AbstractMap<K,V> implements PMap<K,V>{
    
    @Wither
    TreeMap<K,V> map;
    public static <K extends Comparable<? super K>, V> Reducer<PersistentMapX<K, V>> toPersistentMapX() {
        return Reducer.<PersistentMapX<K, V>> of(empty(), (final PersistentMapX<K, V> a) -> b -> a.plusAll(b), (in) -> {
            final List w = ((TupleWrapper) () -> in).values();
            return singleton((K) w.get(0), (V) w.get(1));
        });
    }
    public static <K,V> VavrTreeMapX<K,V> fromMap(@NonNull TreeMap<K,V> map){
        return new VavrTreeMapX<>(map);
    }
    public static <K,V> VavrTreeMapX<K,V> ofAll(@NonNull TreeMap<K,V> map){
        return new VavrTreeMapX<>(map);
    }
    public static <K extends Comparable<? super K>,V> VavrTreeMapX<K,V> fromJavaMap(Map<? extends K,? extends V> map){
        TreeMap<K,V> res = TreeMap.ofAll((Map)map);
        return fromMap(res);
    }
    public static <K extends Comparable<? super K>,V> PersistentMapX<K,V> empty(){
       return new ExtensiblePMapX<K,V>(fromMap(TreeMap.<K,V>empty()), Eval.later(()->toPersistentMapX()));
    }
    public static <K extends Comparable<? super K>,V> PMap<K,V> singletonPMap(K key,V value){
        TreeMap<K,V> map = TreeMap.of(key, value);
        return fromMap(map);
     }
    public static <K extends Comparable<? super K>,V> PersistentMapX<K,V> singleton(K key,V value){
        TreeMap<K,V> map = TreeMap.of(key, value);
        return new ExtensiblePMapX<K,V>(fromMap(map),Eval.later(()-> VavrTreeMapX.<K,V>toPersistentMapX()));
     }
    
    public static <K extends Comparable<? super K>,V> PersistentMapX<K,V> fromStream(@NonNull ReactiveSeq<Tuple2<K,V>> stream){
        return stream.mapReduce(toPersistentMapX());
    }
    
    @Override
    public PMap<K, V> plus(K key, V value) {
        return withMap(map.put(key, value));
    }
    @Override
    public PMap<K, V> plusAll(java.util.Map<? extends K, ? extends V> m2) {
        
        TreeMap<K,V> m = map;
        for(Map.Entry<? extends K, ? extends V> next : m2.entrySet()){
            m = m.put(next.getKey(), next.getValue());
        }
        return withMap(m);
    }
    @Override
    public PMap<K, V> minus(Object key) {
      
        
        return withMap(map.remove((K)key));
     
    }
   
    @Override
    public PMap<K, V> minusAll(Collection<?> keys) {
      
      return withMap(map.removeAll((Iterable)keys));
      
        
    }
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return map.toJavaMap().entrySet();
        
    }
    /* (non-Javadoc)
     * @see java.util.AbstractMap#get(java.lang.Object)
     */
    @Override
    public V get(Object key) {
       return (V)map.get((K)key);
    }
   
    
   
   
}
