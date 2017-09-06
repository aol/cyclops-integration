package cyclops.collections.adt;

import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeMap<K,V> {
    private final RedBlackTree.Tree<K,V> map;

    public ReactiveSeq<Tuple2<K,V>> stream(){
        return map.stream();
    }
    public static <K,V> TreeMap<K,V> fromMap(Comparator<K> comp, Map<K,V> map){
        Stream<Tuple2<K, V>> s = map.entrySet().stream().map(e -> Tuple.tuple(e.getKey(), e.getValue()));
        return new TreeMap<>(RedBlackTree.fromStream(comp,s));
    }

    public <KR,VR> TreeMap<KR,VR> bimap(Comparator<KR> comp, Function<? super K, ? extends KR> keyMapper, Function<? super V, ? extends VR> valueMapper){
        ReactiveSeq<? extends Tuple2<? extends KR, ? extends VR>> s = map.stream().map(t -> t.map((k, v) -> Tuple.tuple(keyMapper.apply(k), valueMapper.apply(v))));
        return new TreeMap<>(RedBlackTree.fromStream(comp,s));
    }
    public Optional<V> get(K key){
        return map.get(key);
    }
    public TreeMap<K,V> plus(K key, V value){
        return new TreeMap<>(map.plus(key,value));
    }
    public TreeMap<K,V> minus(K key){
        return new TreeMap<>(map.minus(key));
    }
}
