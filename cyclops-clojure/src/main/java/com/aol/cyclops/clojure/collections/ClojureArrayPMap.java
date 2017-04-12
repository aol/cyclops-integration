package com.aol.cyclops.clojure.collections;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aol.cyclops2.types.mixins.TupleWrapper;
import cyclops.collections.MapXs;
import cyclops.collections.immutable.PMapX;
import cyclops.control.Eval;
import cyclops.function.Reducer;
import cyclops.stream.ReactiveSeq;
import org.jooq.lambda.tuple.Tuple2;
import org.pcollections.PMap;



import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentVector;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Wither;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClojureArrayPMap<K,V> extends AbstractMap<K,V> implements PMap<K,V>{
    
    @Wither
    PersistentArrayMap map;
    public static <K, V> Reducer<PMapX<K, V>> toPMapX() {
        return Reducer.<PMapX<K, V>> of(empty(), (final PMapX<K, V> a) -> b -> a.plusAll(b), (in) -> {
            final List w = ((TupleWrapper) () -> in).values();
            return singleton((K) w.get(0), (V) w.get(1));
        });
    }
    public static <K,V> ClojureArrayPMap<K,V> fromMap(@NonNull PersistentArrayMap map){
        return new ClojureArrayPMap<>(map);
    }
    public static <K,V> ClojureArrayPMap<K,V> fromJavaMap(@NonNull Map<K,V> map){
        PersistentArrayMap res = ( PersistentArrayMap)PersistentArrayMap.create(map);
        return fromMap(res);
    }
    public static <K,V> ClojureArrayPMap<K,V> emptyPMap(){
        return fromMap(PersistentArrayMap.EMPTY);
     }
    public static <K,V> PMapX<K,V> empty(){
       return new ExtensiblePMapX<K,V>(new ClojureArrayPMap<>(PersistentArrayMap.EMPTY),Eval.later(()->toPMapX()));
    }
    public static <K,V> PMap<K,V> singletonPMap(K key,V value){
        PersistentArrayMap map = ( PersistentArrayMap)PersistentArrayMap.create(MapXs.of(key, value));
        return fromMap(map);
     }
    public static <K,V> PMapX<K,V> singleton(K key,V value){
        PersistentArrayMap map = ( PersistentArrayMap)PersistentArrayMap.create(MapXs.of(key, value));
        return new ExtensiblePMapX<K,V>(fromMap(map), Eval.later(()->ClojureArrayPMap.<K,V>toPMapX()));
     }
    
    public static <K,V> PMapX<K,V> fromStream(@NonNull ReactiveSeq<Tuple2<K,V>> stream){
        return stream.mapReduce(toPMapX());
    }
    
    @Override
    public PMap<K, V> plus(K key, V value) {
        return withMap((PersistentArrayMap)map.cons(PersistentVector.create(key,value)));
    }
    @Override
    public PMap<K, V> plusAll(java.util.Map<? extends K, ? extends V> m2) {
        PersistentArrayMap m = map;
        for(Object next : m2.entrySet()){
            m = (PersistentArrayMap)m.cons(next);
        }
        return withMap(m);
    }
    @Override
    public PMap<K, V> minus(Object key) {
      
        
        return withMap((PersistentArrayMap)map.without(key));
     
    }
   
    @Override
    public PMap<K, V> minusAll(Collection<?> keys) {
      
       PersistentArrayMap m = map;
       for(Object key : keys){
          
           m = (PersistentArrayMap)m.without(key);
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
