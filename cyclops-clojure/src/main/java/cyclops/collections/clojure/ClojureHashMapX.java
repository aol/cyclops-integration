package cyclops.collections.clojure;

import clojure.lang.PersistentArrayMap;
import clojure.lang.PersistentHashMap;
import clojure.lang.PersistentVector;
import com.oath.cyclops.data.collections.extensions.ExtensiblePMapX;
import com.oath.cyclops.types.Unwrapable;
import com.oath.cyclops.types.persistent.PersistentMap;
import cyclops.collections.immutable.PersistentMapX;
import cyclops.companion.MapXs;
import cyclops.companion.Reducers;
import cyclops.control.Eval;
import cyclops.control.Option;
import cyclops.function.Reducer;
import cyclops.reactive.ReactiveSeq;
import lombok.NonNull;
import lombok.experimental.Wither;
import cyclops.data.tuple.Tuple2;

import java.util.*;
import java.util.function.Supplier;

public class ClojureHashMapX<K,V> implements PersistentMap<K,V> , Unwrapable{

    @Wither
    private final PersistentHashMap map;

    private ClojureHashMapX(PersistentHashMap map) {
        this.map = map;
    }

  public static <K, V> Reducer<PersistentMapX<K, V>, Tuple2<K,V>> toPersistentMapX() {
    return Reducer.of(empty(), (final PersistentMapX<K, V> a) -> b -> a.putAll(b), (in) -> {
      Tuple2<K, V> w = in;
      return singleton((K) w._1(), (V) w._2());
    });


  }

    @Override
    public <R> R unwrap() {
        return (R)map;
    }


    public static <K,V> ClojureHashMapX<K,V> fromMap(@NonNull PersistentHashMap map){
        return new ClojureHashMapX<K,V>(map);
    }
    public static <K,V> ClojureHashMapX<K,V> fromJavaMap(@NonNull Map<K,V> map){
        PersistentHashMap res = ( PersistentHashMap)PersistentHashMap.create(map);
        return fromMap(res);
    }
    public static <K,V> PersistentMapX<K,V> empty(){
       return new ExtensiblePMapX<K,V>(fromMap(PersistentHashMap.EMPTY), Eval.later(()-> toPersistentMapX()));
    }
    public static <K,V> PersistentMap<K,V> singletonPMap(K key,V value){
        PersistentHashMap map = ( PersistentHashMap)PersistentHashMap.create(MapXs.of(key, value));
        return fromMap(map);
     }
    public static <K,V> PersistentMapX<K,V> singleton(K key,V value){
        PersistentHashMap map = ( PersistentHashMap)PersistentHashMap.create(MapXs.of(key, value));
        return new ExtensiblePMapX<K,V>(fromMap(map),Eval.later(()-> toPersistentMapX()));
     }

    public static <K,V> PersistentMapX<K,V> fromStream(@NonNull ReactiveSeq<Tuple2<K,V>> stream){
        return stream.mapReduce(toPersistentMapX());
    }


    public PersistentMap<K, V> put(K key, V value) {
        return withMap((PersistentHashMap)map.cons(PersistentVector.create(key,value)));
    }
    @Override
    public PersistentMap<K, V> put(Tuple2<K, V> keyAndValue) {
      return put(keyAndValue._1(),keyAndValue._2());
    }
    public PersistentMap<K, V> putAll(PersistentMap<? extends K, ? extends V> m2) {
        PersistentHashMap m = map;
        for(Object next : m2){
            m = (PersistentHashMap)m.cons(next);
        }
        return withMap(m);
    }
    @Override
    public PersistentMap<K, V> remove(K key) {


        return withMap((PersistentHashMap)map.without(key));

    }

  @Override
  public PersistentMap<K, V> removeAll(Iterable<? extends K> keys) {

    PersistentHashMap  m = map;
    for(Object key : keys){

      m = (PersistentHashMap )m.without(key);
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

    public static <K,V> PersistentMapX<K,V> copyFromMap(Map<K,V> map){
      PersistentMapX<K, V> res = ClojureHashMapX.<K, V>empty();
       for(Map.Entry<K,V> next : map.entrySet()){
         res = res.put(next.getKey(),next.getValue());
       }
        return res;
    }
}
