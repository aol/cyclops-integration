package cyclops.collections.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImmutableLinkedHashMap<K,V> {

    private final TrieMap<K, V> map;
    private final BankersQueue<Tuple2<K, V>> order;

    public Optional<V> get(K key){
        return map.get(key);
    }
    public ImmutableLinkedHashMap<K, V> put(K key, V value) {
        BankersQueue<Tuple2<K, V>> newOrder = get(key).map(v -> order.replace(Tuple.tuple(key, v), Tuple.tuple(key, value)))
                .orElseGet(() -> order.enqueue(Tuple.tuple(key, value)));
        return new ImmutableLinkedHashMap<>(map.plus(key,value),newOrder);

    }
}
