package cyclops.collections.vavr;

import java.util.*;
import java.util.function.Supplier;

import com.oath.cyclops.data.collections.extensions.ExtensiblePMapX;
import com.oath.cyclops.types.Unwrapable;

import com.oath.cyclops.types.persistent.PersistentMap;
import cyclops.VavrConverters;
import cyclops.collections.immutable.PersistentMapX;
import cyclops.control.Eval;
import cyclops.control.Option;
import cyclops.function.Reducer;
import cyclops.reactive.ReactiveSeq;
import io.vavr.collection.HashMap;
import cyclops.data.tuple.Tuple2;



import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Wither;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VavrHashMapX<K,V>  implements PersistentMap<K,V>, Unwrapable{

  public static <K, V> Reducer<PersistentMapX<K, V>, Tuple2<K,V>> toPersistentMapX() {
    return Reducer.of(PersistentMapX.empty(), (final PersistentMapX<K, V> a) -> b -> a.putAll(b), (in) -> {
      Tuple2<K, V> w = in;
      return singleton((K) w._1(), (V) w._2());
    });


  }
    @Override
    public <R> R unwrap() {
        return (R)map;
    }

    @Wither
    HashMap<K,V> map;

    public static <K,V> VavrHashMapX<K,V> fromMap(HashMap<K,V> map){
        return new VavrHashMapX<>(map);
    }
    public static <K,V> VavrHashMapX<K,V> ofAll(HashMap<K,V> map){
        return new VavrHashMapX<>(map);
    }
    public static <K,V> VavrHashMapX<K,V> fromJavaMap(Map<K,V> map){
        HashMap<K,V> res = HashMap.ofAll(map);
        return fromMap(res);
    }
    public static <K,V> PersistentMapX<K,V> empty(){
       return new ExtensiblePMapX<K,V>(fromMap(HashMap.empty()), Eval.later(()->toPersistentMapX()));
    }
    public static <K,V> PersistentMap<K,V> singletonPMap(K key,V value){
        HashMap<K,V> map = HashMap.of(key, value);
        return fromMap(map);
     }
    public static <K,V> PersistentMapX<K,V> singleton(K key,V value){
        HashMap<K,V> map = HashMap.of(key, value);
        return new ExtensiblePMapX<K,V>(fromMap(map),Eval.later(()-> VavrHashMapX.<K,V>toPersistentMapX()));
     }

    public static <K,V> PersistentMapX<K,V> fromStream(ReactiveSeq<Tuple2<K,V>> stream){
        return stream.mapReduce(toPersistentMapX());
    }

  @Override
  public PersistentMap<K, V> put(K key, V value) {
    return withMap(map.put(key,value));
  }

  @Override
  public PersistentMap<K, V> put(cyclops.data.tuple.Tuple2<K, V> keyAndValue) {
    return put(keyAndValue._1(),keyAndValue._2());
  }
  @Override
  public PersistentMap<K, V> putAll(PersistentMap<? extends K, ? extends V> m2) {

        HashMap<K,V> m = map;
        for(Tuple2<? extends K, ? extends V> next : m2){
            m = m.put(next._1(), next._2());
        }
        return withMap(m);
    }
    @Override
    public PersistentMap<K, V> remove(K key) {


        return withMap(map.remove((K)key));

    }

    @Override
    public PersistentMap<K, V> removeAll(Iterable<? extends K> keys) {

      return withMap(map.removeAll((Iterable)keys));


    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return map.toJavaMap().entrySet();

    }
  @Override
  public Option<V> get(K key) {


    return VavrConverters.toCyclopsOption(map.get(key));

  }
  @Override
  public V getOrElse(K key, V alt) {

    return map.getOrElse(key,alt);
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
    return map.containsKey(key);
  }

  @Override
  public Iterator<Tuple2<K, V>> iterator() {
    return ReactiveSeq.fromIterable(map).map(VavrConverters::toCyclopsTuple2).iterator();
   }



}
