package cyclops.collections.adt;


import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.factory.Unit;
import cyclops.typeclasses.Pure;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiMap<W,K,V> {
    private final HashMap<K,Higher<W,V>> multiMap;
    private final Appender<W,V> appender;
    private final Pure<W> pure;

    public MultiMap<W,K, V> put(K key, V value) {
        Higher<W,V> hkt = multiMap.get(key).map(v->appender.append(v,value)).orElseGet(()->pure.unit(value));
        return new MultiMap<>(multiMap.plus(key,hkt),appender,pure);
    }
    public Optional<Higher<W,V>> get(K key){
        return multiMap.get(key);
    }
    @FunctionalInterface
    public interface Appender<W,V>{
        public Higher<W,V> append(Higher<W,V> container,V value);
    }
}
