package cyclops.collections.adt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MapBag<T> {

    private final HashMap<T,Integer> map;
    private final int size;

    public static <T> MapBag<T> empty() {
        return new MapBag<>(HashMap.empty(), 0);
    }



    public int size() {
        return size;
    }

    public boolean contains(final T e) {
        return map.get(e).isPresent();
    }

    public MapBag<T> plus(final T value) {
        return new MapBag<>(map.plus(value, map.get(value).orElse(0)+1), size+1);
    }


    public MapBag<T> minus(final T value) {
        int n = map.get(value).orElse(0);
        if(n==0)
            return this;
        if(n==1)
            return new MapBag<>(map.minus(value), size-1);

        return new MapBag<>(map.plus(value, n-1), size-1);
    }




}
