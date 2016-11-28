package com.aol.cyclops.clojure.collections;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jooq.lambda.tuple.Tuple2;
import org.pcollections.PMap;

import com.aol.cyclops.Reducer;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.persistent.PMapX;
import com.aol.cyclops.data.collections.extensions.standard.MapXs;
import com.aol.cyclops.reactor.collections.extensions.base.ExtensiblePMapX;
import com.aol.cyclops.types.mixins.TupleWrapper;

import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;
import clojure.lang.PersistentVector;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;
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
    public static <K,V> ClojureHashPMap<K,V> fromMap(PersistentHashMap map){
        return new ClojureHashPMap<>(map);
    }
    public static <K,V> ClojureHashPMap<K,V> fromJavaMap(Map<K,V> map){
        PersistentHashMap res = ( PersistentHashMap)PersistentHashMap.create(map);
        return fromMap(res);
    }
    public static <K,V> PMapX<K,V> empty(){
       return new ExtensiblePMapX<K,V>(fromMap(PersistentHashMap.EMPTY),null);
    }
    public static <K,V> PMap<K,V> singletonPMap(K key,V value){
        PersistentHashMap map = ( PersistentHashMap)PersistentHashMap.create(MapXs.of(key, value));
        return fromMap(map);
     }
    public static <K,V> PMapX<K,V> singleton(K key,V value){
        PersistentHashMap map = ( PersistentHashMap)PersistentHashMap.create(MapXs.of(key, value));
        return new ExtensiblePMapX<K,V>(fromMap(map),ClojureHashPMap.<K,V>toPMapX());
     }
    
    public static <K,V> PMapX<K,V> fromStream(ReactiveSeq<Tuple2<K,V>> stream){
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
      
        
        return withMap((PersistentHashMap)map.remove(key));
     
    }
   
    @Override
    public PMap<K, V> minusAll(Collection<?> keys) {
       IPersistentMap m = map;
       for(Object key : keys){
           m.
           m = (PersistentHashMap)m.remove(key);
       }
       return withMap(m);
        
    }
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return map.entrySet();
        
    }
    
   
   
}
