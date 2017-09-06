package cyclops.collections.adt;


import cyclops.stream.ReactiveSeq;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ImmutableMap<K,V>{

    ImmutableMap<K,V> put(K key, V value);
    ImmutableMap<K,V> putAll(ImmutableMap<K,V> map);

    boolean contains(K key);
    Optional<V> get(K key);
    V getOrElse(K key,V alt);

    int size();

    <K2,V2> DMap.Two<K,V,K2,V2> merge(ImmutableMap<K2,V2> one);
    <K2,V2,K3,V3> DMap.Three<K,V,K2,V2,K3,V3> merge(DMap.Two<K2,V2,K3,V3> two);

    ReactiveSeq<Tuple2<K,V>> stream();

    <R> ImmutableMap<K,R> mapValues(Function<? super V,? extends R> map);
    <R> ImmutableMap<R,V> mapKeys(Function<? super K,? extends R> map);
    <R1,R2> ImmutableMap<R1,R2> bimap(BiFunction<? super K,? super V,? extends Tuple2<R1,R2>> map);

}
