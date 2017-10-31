package cyclops.collections.clojure;

import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentVector;
import com.oath.cyclops.data.collections.extensions.ExtensiblePMapX;
import com.oath.cyclops.types.Unwrapable;
import com.oath.cyclops.types.persistent.PersistentMap;
import cyclops.collections.immutable.PersistentMapX;
import cyclops.companion.MapXs;
import cyclops.companion.Reducers;
import cyclops.control.Eval;
import cyclops.control.Option;
import cyclops.data.HashMap;
import cyclops.data.tuple.Tuple2;
import cyclops.function.Reducer;
import cyclops.reactive.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Wither;
import cyclops.data.tuple.Tuple2;


import java.util.*;
import java.util.function.Supplier;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClojureArrayMapX<K,V> implements PersistentMap<K,V> , Unwrapable {

    @Wither
    PersistentArrayMap map;

  public static <K, V> Reducer<PersistentMapX<K, V>, Tuple2<K,V>> toPersistentMapX() {
    return Reducer.of(PersistentMapX.empty(), (final PersistentMapX<K, V> a) -> b -> a.putAll(b), (in) -> {
      Tuple2<K, V> w = in;
      return singleton((K) w._1(), (V) w._2());
    });


  }


    public static <K,V> ClojureArrayMapX<K,V> fromMap(@NonNull PersistentArrayMap map){
        return new ClojureArrayMapX<>(map);
    }
    public static <K,V> ClojureArrayMapX<K,V> fromJavaMap(@NonNull Map<K,V> map){
        PersistentArrayMap res = ( PersistentArrayMap)PersistentArrayMap.create(map);
        return fromMap(res);
    }
    public static <K,V> ClojureArrayMapX<K,V> emptyPMap(){
        return fromMap(PersistentArrayMap.EMPTY);
     }
    public static <K,V> PersistentMapX<K,V> empty(){
       return new ExtensiblePMapX<K,V>(new ClojureArrayMapX<>(PersistentArrayMap.EMPTY),Eval.later(()->toPersistentMapX()));
    }
    public static <K,V> PersistentMap<K,V> singletonPMap(K key,V value){
        PersistentArrayMap map = ( PersistentArrayMap)PersistentArrayMap.create(MapXs.of(key, value));
        return fromMap(map);
     }
    public static <K,V> PersistentMapX<K,V> singleton(K key,V value){
        PersistentArrayMap map = ( PersistentArrayMap)PersistentArrayMap.create(MapXs.of(key, value));
        return new ExtensiblePMapX<K,V>(fromMap(map), Eval.later(()-> toPersistentMapX()));
     }

    public static <K,V> PersistentMapX<K,V> fromStream(@NonNull ReactiveSeq<Tuple2<K,V>> stream){
        return stream.mapReduce(toPersistentMapX());
    }

    @Override
    public PersistentMap<K, V> put(K key, V value) {
        return withMap((PersistentArrayMap)map.cons(PersistentVector.create(key,value)));
    }

  @Override
  public PersistentMap<K, V> put(Tuple2<K, V> keyAndValue) {
    return put(keyAndValue._1(),keyAndValue._2());
  }

  @Override
    public PersistentMap<K, V> putAll(PersistentMap<? extends K, ? extends V> m2) {
        PersistentArrayMap m = map;
        for(Object next : m2){
            m = (PersistentArrayMap)m.cons(next);
        }
        return withMap(m);
    }
    @Override
    public PersistentMap<K, V> remove(K key) {


        return withMap((PersistentArrayMap)map.without(key));

    }

    @Override
    public PersistentMap<K, V> removeAll(Iterable<? extends K> keys) {

       PersistentArrayMap m = map;
       for(Object key : keys){

           m = (PersistentArrayMap)m.without(key);
       }
       return withMap(m);

    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return map.entrySet();

    }

    @Override
    public Option<V> get(K key) {
       return Option.ofNullable((V)map.valAt(key));

    }

  @Override
  public V getOrElse(K key, V alt) {
    return (V)map.valAt(key,alt);
  }

  @Override
  public V getOrElseGet(K key, Supplier<? extends V> alt) {
    return (V)map.valAt(key,alt.get());
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean containsKey(K key) {
    return map.containsKey(key);
  }

  @Override //@TODO fix
  public Iterator<Tuple2<K, V>> iterator() {
    return (Iterator)map.iterator();
  }
}
