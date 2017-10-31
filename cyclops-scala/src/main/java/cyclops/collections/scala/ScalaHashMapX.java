package cyclops.collections.scala;

import java.util.*;
import java.util.function.Supplier;


import com.aol.cyclops.scala.collections.Converters;
import com.aol.cyclops.scala.collections.HasScalaCollection;
import com.oath.cyclops.data.collections.extensions.ExtensiblePMapX;
import com.oath.cyclops.types.Unwrapable;

import com.oath.cyclops.types.persistent.PersistentMap;
import cyclops.ScalaConverters;
import cyclops.collections.immutable.PersistentMapX;
import cyclops.control.Eval;
import cyclops.control.Option;
import cyclops.function.Reducer;
import cyclops.reactive.ReactiveSeq;




import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;
import scala.Function0;
import scala.Tuple2;
import scala.collection.GenTraversableOnce;
import scala.collection.JavaConverters;
import scala.collection.generic.CanBuildFrom;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.HashMap$;
import scala.collection.immutable.MapLike;
import scala.collection.immutable.TreeMap;
import scala.collection.mutable.Builder;

import static cyclops.ScalaConverters.toCyclopsTuple2;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ScalaHashMapX<K,V> implements PersistentMap<K,V>, HasScalaCollection, Unwrapable {

    @Wither
    HashMap<K,V> map;

  public static <K, V> Reducer<PersistentMapX<K, V>, cyclops.data.tuple.Tuple2<K,V>> toPersistentMapX() {
    return Reducer.of(PersistentMapX.empty(), (final PersistentMapX<K, V> a) -> b -> a.putAll(b), (in) -> {
      cyclops.data.tuple.Tuple2<K, V> w = in;
      return singleton((K) w._1(), (V) w._2());
    });


  }
    @Override
    public <R> R unwrap() {
        return (R)map;
    }

   public static <K,V> PersistentMapX<K,V> copyFromMap(Map<K,V> map){
      PersistentMapX<K, V> res = ScalaHashMapX.<K, V>empty();
       for(Map.Entry<K,V> next : map.entrySet()){
         res = res.put(next.getKey(),next.getValue());
       }
        return res;
    }

    public static <K,V> ScalaHashMapX<K,V> fromMap(HashMap<K,V> map){
        return new ScalaHashMapX<>(map);
    }
    public static <K,V> PersistentMapX<K,V> empty(){
      ScalaHashMapX<K, V> x = fromMap(HashMap$.MODULE$.empty());
       return new ExtensiblePMapX<K,V>(x,Eval.later(()->toPersistentMapX()));
    }
    public static <K,V> PersistentMap<K,V> singletonPMap(K key,V value){
        Builder b = HashMap$.MODULE$.newBuilder();
        Builder<Tuple2<K, V>, HashMap> builder = b;
        HashMap<K,V> map = builder.$plus$eq(Tuple2.apply(key,value)).result();
        return fromMap(map);
     }
    public static <K,V> PersistentMapX<K,V> singleton(K key,V value){
         Builder b = HashMap$.MODULE$.newBuilder();
        Builder<Tuple2<K, V>, HashMap<K,V>> builder = b;
        HashMap<K,V> map = builder.$plus$eq(Tuple2.apply(key,value)).result();
        return new ExtensiblePMapX<K,V>(fromMap(map), Eval.later(()-> ScalaHashMapX.<K,V>toPersistentMapX()));
     }

    public static <K,V> PersistentMapX<K,V> fromStream(ReactiveSeq<cyclops.data.tuple.Tuple2<K,V>> stream){
        return stream.mapReduce(toPersistentMapX());
    }

    @Override
    public PersistentMap<K, V> put(K key, V value) {
        return withMap(map.$plus(Tuple2.apply(key,value)));
    }
    @Override
    public PersistentMap<K, V> put(cyclops.data.tuple.Tuple2<K, V> keyAndValue) {
      return put(keyAndValue._1(),keyAndValue._2());
    }
    @Override
    public PersistentMap<K, V> putAll(PersistentMap<? extends K, ? extends V> m) {
         HashMap<K,V> use = map;

         if(m instanceof ScalaHashMapX){
             HashMap<K,V> add = ((ScalaHashMapX)m).map;
             use = (HashMap<K, V>) use.$plus$plus(map, this.canBuildFrom());
         }
         if(m instanceof ScalaTreeMapX){
             TreeMap<K,V> add = ((ScalaTreeMapX)m).map;
             use = (HashMap<K, V>) use.$plus$plus(map, this.canBuildFrom());
         }
         else{
             for(cyclops.data.tuple.Tuple2<? extends K, ? extends V> next : m){
                 use = use.$plus(Tuple2.apply(next._1(),next._2()));
             }
         }
        return withMap(use);
    }


    @Override
    public PersistentMap<K, V> remove(K key) {

        HashMap m = map;
        return withMap((HashMap)m.$minus(key));

    }

    @Override
    public PersistentMap<K, V> removeAll(Iterable<? extends K> keys) {
        GenTraversableOnce gen =  HasScalaCollection.traversable(keys);
        MapLike m = map;
        return withMap((HashMap)m.$minus$minus(gen));

    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return ReactiveSeq.fromIterable(JavaConverters.asJavaIterable(map))
                        .map(t->(java.util.Map.Entry<K,V>)new AbstractMap.SimpleEntry<K,V>(t._1,t._2))
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


  @Override
  public Option<V> get(K key) {
  scala.collection.immutable.Map<K,V> m = this.map;

    return ScalaConverters.toCyclopsOption(m.get(key));

  }
  @Override
  public V getOrElse(K key, V alt) {
    return get(key).orElse(alt);
  }

  @Override
  public V getOrElseGet(K key, Supplier<? extends V> alt) {
    return get(key).orElseGet(alt);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean containsKey(K key) {
    return map.contains(key);
  }

  @Override
  public Iterator<cyclops.data.tuple.Tuple2<K, V>> iterator() {

    Iterator<Tuple2<K, V>> it = ScalaConverters.iterator(map.iterator());
    ReactiveSeq<cyclops.data.tuple.Tuple2<K,V>> x = ReactiveSeq.fromIterator(it)
                                                               .map(t -> ScalaConverters.<K, V>toCyclopsTuple2(t));
    return x.iterator();
  }

}
