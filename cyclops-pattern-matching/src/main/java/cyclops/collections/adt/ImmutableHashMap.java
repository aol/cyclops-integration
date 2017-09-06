package cyclops.collections.adt;

import cyclops.stream.ReactiveSeq;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor
public class ImmutableHashMap<K,V> implements ImmutableMap<K,V>{
    HAMT.Node<K,V> map;

    public static <K,V> ImmutableHashMap<K,V> empty(){
        return new ImmutableHashMap<>(HAMT.empty());
    }
    public int size(){
        return map.size();
    }

    @Override
    public <K2, V2> DMap.Two<K, V, K2, V2> merge(ImmutableMap<K2, V2> one) {
        return null;
    }

    @Override
    public <K2, V2, K3, V3> DMap.Three<K, V, K2, V2, K3, V3> merge(DMap.Two<K2, V2, K3, V3> two) {
        return null;
    }

    public ReactiveSeq<Tuple2<K,V>> stream(){
        return map.stream();
    }

    @Override
    public <R> ImmutableMap<K, R> mapValues(Function<? super V, ? extends R> map) {
        return null;
    }

    @Override
    public <R> ImmutableMap<R, V> mapKeys(Function<? super K, ? extends R> map) {
        return null;
    }

    @Override
    public <R1, R2> ImmutableMap<R1, R2> bimap(BiFunction<? super K, ? super V, ? extends Tuple2<R1, R2>> map) {
        return null;
    }

    public ImmutableHashMap<K,V> put(K key, V value){
        return new ImmutableHashMap<K,V>(map.plus(0,key.hashCode(),key,value));
    }

    @Override
    public ImmutableMap<K, V> putAll(ImmutableMap<K, V> map) {
       return map.stream().foldLeft(this,(m,next)->m.put(next.v1,next.v2));
    }

    @Override
    public boolean contains(K key) {
        return map.get(0,key.hashCode(),key).isPresent();
    }

    public Optional<V> get(K key){
        return map.get(0,key.hashCode(),key);
    }

    @Override
    public V getOrElse(K key, V alt) {
        return get(key).orElse(alt);
    }

    public ImmutableHashMap<K,V> minus(K key){
        return new ImmutableHashMap<K,V>(map.minus(0,key.hashCode(),key));
    }
}
