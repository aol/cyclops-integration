package cyclops.collections.adt;

import cyclops.stream.ReactiveSeq;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@AllArgsConstructor
public class ImmutableHashMap<K,V> implements ImmutableMap<K,V>{
    HAMT.Node<K,V> map;

    public static <K,V> ImmutableHashMap<K,V> empty(){
        return new ImmutableHashMap<>(HAMT.empty());
    }

    public static <K,V> ImmutableHashMap<K,V> fromStream(ReactiveSeq<Tuple2<K,V>> stream){
        return stream.foldLeft(empty(),(m,t2)->m.put(t2.v1,t2.v2));
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
        return fromStream(stream().map(t->t.map2(map)));
    }

    @Override
    public <R> ImmutableMap<R, V> mapKeys(Function<? super K, ? extends R> map) {
        return fromStream(stream().map(t->t.map1(map)));

    }

    @Override
    public <R1, R2> ImmutableMap<R1, R2> bimap(BiFunction<? super K, ? super V, ? extends Tuple2<R1, R2>> map) {
        return fromStream(stream().map(t->t.map(map)));
    }

    @Override
    public <K2, V2> ImmutableMap<K2, V2> flatMap(BiFunction<? super K, ? super V, ? extends ImmutableMap<K2, V2>> mapper) {
        return fromStream(stream().flatMapI(t->t.map(mapper)));
    }

    @Override
    public <K2, V2> ImmutableMap<K2, V2> flatMapI(BiFunction<? super K, ? super V, ? extends Iterable<Tuple2<K2, V2>>> mapper) {
        return fromStream(stream().flatMapI(t->t.map(mapper)));
    }

    @Override
    public ImmutableMap<K, V> filter(Predicate<? super Tuple2<K, V>> predicate) {
        return fromStream(stream().filter(predicate));
    }

    @Override
    public ImmutableMap<K, V> filterKeys(Predicate<? super K> predicate) {
        return fromStream(stream().filter(t->predicate.test(t.v1)));
    }

    @Override
    public ImmutableMap<K, V> filterValues(Predicate<? super V> predicate) {
        return fromStream(stream().filter(t->predicate.test(t.v2)));
    }

    public ImmutableHashMap<K,V> put(K key, V value){
        return new ImmutableHashMap<K,V>(map.plus(0,key.hashCode(),key,value));
    }

    @Override
    public ImmutableMap<K, V> put(Tuple2<K, V> keyAndValue) {
        return put(keyAndValue.v1,keyAndValue.v2);
    }

    @Override
    public ImmutableMap<K, V> putAll(ImmutableMap<K, V> map) {
       return map.stream().foldLeft(this,(m,next)->m.put(next.v1,next.v2));
    }

    @Override
    public ImmutableMap<K, V> remove(K key) {
        return new ImmutableHashMap<>(map.minus(0,key.hashCode(),key));
    }


    @Override
    public ImmutableMap<K, V> removeAll(K... keys) {
        HAMT.Node<K,V> cur = map;
        for(K key : keys){
            cur = map.minus(0,key.hashCode(),key);
        }
        return new ImmutableHashMap<>(cur);
    }



    @Override
    public boolean containsKey(K key) {
        return map.get(0,key.hashCode(),key).isPresent();
    }



    @Override
    public boolean contains(Tuple2<K, V> t) {
        return get(t.v1).filter(v-> Objects.equals(v,t.v2)).isPresent();
    }

    public Optional<V> get(K key){
        return map.get(0,key.hashCode(),key);
    }

    @Override
    public V getOrElse(K key, V alt) {
        return map.getOrElse(0,key.hashCode(),key,alt);
    }

    @Override
    public V getOrElseGet(K key, Supplier<V> alt) {
        return map.getOrElseGet(0,key.hashCode(),key,alt);
    }

    public ImmutableHashMap<K,V> minus(K key){
        return new ImmutableHashMap<K,V>(map.minus(0,key.hashCode(),key));
    }

    @Override
    public Iterator<Tuple2<K, V>> iterator() {
        return stream().iterator();
    }
}
