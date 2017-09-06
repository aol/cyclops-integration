package cyclops.collections.adt;


import com.aol.cyclops2.hkt.Higher;
import cyclops.typeclasses.Pure;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.function.Function;

/*
 Higher kinded multimap
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiMap<W,K,V> {
    private final ImmutableHashMap<K,Higher<W,V>> multiMap;
    private final Appender<W,V> appender;
    private final Pure<W> pure;

    public static <W,K,V> MultiMap<W,K,V> empty(Appender<W,V> appender, Pure<W> pure){
        return new MultiMap<>(ImmutableHashMap.empty(),appender,pure);
    }

    public MultiMap<W,K, V> put(K key, V value) {
        Higher<W,V> hkt = multiMap.get(key).map(v->appender.append(v,value)).orElseGet(()->pure.unit(value));
        return new MultiMap<>(multiMap.put(key,hkt),appender,pure);
    }
    public <R> Optional<R> get(K key,Function<? super Higher<W,V>,? extends R> decoder){
        return multiMap.get(key).map(decoder);
    }
    public Optional<Higher<W,V>> get(K key){
        return multiMap.get(key);
    }
    @FunctionalInterface
    public interface Appender<W,V>{
        public Higher<W,V> append(Higher<W,V> container,V value);
    }
}
