package com.aol.cyclops.scala.collections;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Set;


import com.aol.cyclops2.data.collections.extensions.ExtensiblePMapX;
import com.aol.cyclops2.types.mixins.TupleWrapper;
import cyclops.collections.immutable.PMapX;
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
import scala.collection.immutable.HashMap$;
import scala.collection.immutable.MapLike;
import scala.collection.immutable.TreeMap;
import scala.collection.mutable.Builder;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScalaHashPMap<K,V> extends AbstractMap<K,V> implements PMap<K,V>, HasScalaCollection {
    
    @Wither
    HashMap<K,V> map;
    public static <K, V> Reducer<PMapX<K, V>> toPMapX() {
        return Reducer.<PMapX<K, V>> of(empty(), (final PMapX<K, V> a) -> b -> a.plusAll(b), (in) -> {
            final List w = ((TupleWrapper) () -> in).values();
            return singleton((K) w.get(0), (V) w.get(1));
        });
    }
    public static <K,V> ScalaHashPMap<K,V> fromMap(HashMap<K,V> map){
        return new ScalaHashPMap<>(map);
    }
    public static <K,V> PMapX<K,V> empty(){
       return new ExtensiblePMapX<K,V>(fromMap(HashMap$.MODULE$.empty()),Eval.later(()->toPMapX()));
    }
    public static <K,V> PMap<K,V> singletonPMap(K key,V value){
        Builder<Tuple2<K, V>, HashMap> builder = HashMap$.MODULE$.newBuilder();
        HashMap<K,V> map = builder.$plus$eq(Tuple2.apply(key,value)).result();
        return fromMap(map);
     }
    public static <K,V> PMapX<K,V> singleton(K key,V value){
        Builder<Tuple2<K, V>, HashMap> builder = HashMap$.MODULE$.newBuilder();
        HashMap<K,V> map = builder.$plus$eq(Tuple2.apply(key,value)).result();
        return new ExtensiblePMapX<K,V>(fromMap(map), Eval.later(()->ScalaHashPMap.<K,V>toPMapX()));
     }
    
    public static <K,V> PMapX<K,V> fromStream(ReactiveSeq<Tuple2<K,V>> stream){
        return stream.mapReduce(toPMapX());
    }
    
    @Override
    public PMap<K, V> plus(K key, V value) {
        return withMap(map.$plus(Tuple2.apply(key,value)));
    }
    @Override
    public PMap<K, V> plusAll(java.util.Map<? extends K, ? extends V> m) {
         HashMap<K,V> use = map;
         
         if(m instanceof ScalaHashPMap){
             HashMap<K,V> add = ((ScalaHashPMap)m).map;
             use = (HashMap<K, V>) use.$plus$plus(map, this.canBuildFrom());
         }
         if(m instanceof ScalaTreePMap){
             TreeMap<K,V> add = ((ScalaTreePMap)m).map;
             use = (HashMap<K, V>) use.$plus$plus(map, this.canBuildFrom());
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
      
        HashMap m = map;
        return withMap((HashMap)m.$minus(key));
     
    }
   
    @Override
    public PMap<K, V> minusAll(Collection<?> keys) {
        GenTraversableOnce gen =  HasScalaCollection.traversable(keys);
        MapLike m = map;
        return withMap((HashMap)m.$minus$minus(gen));
        
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
       return HashMap.canBuildFrom();
    }
    /* (non-Javadoc)
     * @see java.util.AbstractMap#get(java.lang.Object)
     */
    @Override
    public V get(Object key) {
        return map.apply((K)key);
    }
   
   
   
}
