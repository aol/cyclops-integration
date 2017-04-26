package com.aol.cyclops.javaslang.collections;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aol.cyclops2.data.collections.extensions.ExtensiblePMapX;
import com.aol.cyclops2.types.mixins.TupleWrapper;
import cyclops.collections.immutable.PMapX;
import cyclops.control.Eval;
import cyclops.function.Reducer;
import cyclops.stream.ReactiveSeq;
import org.jooq.lambda.tuple.Tuple2;
import org.pcollections.PMap;

import javaslang.collection.HashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JavaSlangHashPMap<K,V> extends AbstractMap<K,V> implements PMap<K,V>{
    
    @Wither
    HashMap<K,V> map;
    public static <K, V> Reducer<PMapX<K, V>> toPMapX() {
        return Reducer.<PMapX<K, V>> of(empty(), (final PMapX<K, V> a) -> b -> a.plusAll(b), (in) -> {
            final List w = ((TupleWrapper) () -> in).values();
            return singleton((K) w.get(0), (V) w.get(1));
        });
    }
    public static <K,V> JavaSlangHashPMap<K,V> fromMap(HashMap<K,V> map){
        return new JavaSlangHashPMap<>(map);
    }
    public static <K,V> JavaSlangHashPMap<K,V> fromJavaMap(Map<K,V> map){
        HashMap<K,V> res = HashMap.ofAll(map);
        return fromMap(res);
    }
    public static <K,V> PMapX<K,V> empty(){
       return new ExtensiblePMapX<K,V>(fromMap(HashMap.empty()), Eval.later(()->toPMapX()));
    }
    public static <K,V> PMap<K,V> singletonPMap(K key,V value){
        HashMap<K,V> map = HashMap.of(key, value);
        return fromMap(map);
     }
    public static <K,V> PMapX<K,V> singleton(K key,V value){
        HashMap<K,V> map = HashMap.of(key, value);
        return new ExtensiblePMapX<K,V>(fromMap(map),Eval.later(()->JavaSlangHashPMap.<K,V>toPMapX()));
     }
    
    public static <K,V> PMapX<K,V> fromStream(ReactiveSeq<Tuple2<K,V>> stream){
        return stream.mapReduce(toPMapX());
    }
    
    @Override
    public PMap<K, V> plus(K key, V value) {
        return withMap(map.put(key, value));
    }
    @Override
    public PMap<K, V> plusAll(java.util.Map<? extends K, ? extends V> m2) {
        
        HashMap<K,V> m = map;
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
