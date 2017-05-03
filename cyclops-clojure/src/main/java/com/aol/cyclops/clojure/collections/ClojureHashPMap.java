package com.aol.cyclops.clojure.collections;

import clojure.lang.PersistentHashMap;
import clojure.lang.PersistentVector;
import com.aol.cyclops2.data.collections.extensions.ExtensiblePMapX;
import com.aol.cyclops2.types.mixins.TupleWrapper;
import cyclops.collections.MapXs;
import cyclops.collections.immutable.PMapX;
import cyclops.control.Eval;
import cyclops.function.Reducer;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Wither;
import org.jooq.lambda.tuple.Tuple2;
import org.pcollections.PMap;

import java.util.*;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClojureHashPMap<K,V> extends AbstractMap<K,V> implements PMap<K,V>{
    
    @Wither
    PersistentHashMap map;
    public static <K, V> Reducer<PMapX<K, V>> toPMapX() {
        return Reducer.<PMapX<K, V>> of(empty(), (final PMapX<K, V> a) -> b -> a.plusAll(b), (in) -> {
            final List w = ((TupleWrapper) () -> in).values();
            return singleton((K) w.get(0), (V) w.get(1));
        });
    }
    public static <K,V> ClojureHashPMap<K,V> fromMap(@NonNull PersistentHashMap map){
        return new ClojureHashPMap<>(map);
    }
    public static <K,V> ClojureHashPMap<K,V> fromJavaMap(@NonNull Map<K,V> map){
        PersistentHashMap res = ( PersistentHashMap)PersistentHashMap.create(map);
        return fromMap(res);
    }
    public static <K,V> PMapX<K,V> empty(){
       return new ExtensiblePMapX<K,V>(fromMap(PersistentHashMap.EMPTY), Eval.later(()->toPMapX()));
    }
    public static <K,V> PMap<K,V> singletonPMap(K key,V value){
        PersistentHashMap map = ( PersistentHashMap)PersistentHashMap.create(MapXs.of(key, value));
        return fromMap(map);
     }
    public static <K,V> PMapX<K,V> singleton(K key,V value){
        PersistentHashMap map = ( PersistentHashMap)PersistentHashMap.create(MapXs.of(key, value));
        return new ExtensiblePMapX<K,V>(fromMap(map),Eval.later(()->ClojureHashPMap.<K,V>toPMapX()));
     }
    
    public static <K,V> PMapX<K,V> fromStream(@NonNull ReactiveSeq<Tuple2<K,V>> stream){
        return stream.mapReduce(toPMapX());
    }
    
    @Override
    public PMap<K, V> plus(K key, V value) {
        return withMap((PersistentHashMap)map.cons(PersistentVector.create(key,value)));
    }
    @Override
    public PMap<K, V> plusAll(java.util.Map<? extends K, ? extends V> m2) {
        PersistentHashMap m = map;
        for(Object next : m2.entrySet()){
            m = (PersistentHashMap)m.cons(next);
        }
        return withMap(m);
    }
    @Override
    public PMap<K, V> minus(Object key) {
      
        
        return withMap((PersistentHashMap)map.without(key));
     
    }
   
    @Override
    public PMap<K, V> minusAll(Collection<?> keys) {
      
       PersistentHashMap m = map;
       for(Object key : keys){
          
           m = (PersistentHashMap)m.without(key);
       }
       return withMap(m);
        
    }
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return map.entrySet();
        
    }
    /* (non-Javadoc)
     * @see java.util.AbstractMap#get(java.lang.Object)
     */
    @Override
    public V get(Object key) {
       return (V)map.valAt(key);
    }
   
    
   
   
}
