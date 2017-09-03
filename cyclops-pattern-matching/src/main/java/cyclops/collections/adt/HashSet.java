package cyclops.collections.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashSet<T> {
    private final HAMT.Node<T,T> map;


    public static <T> HashSet<T> of(T... values){
        HAMT.Node<T, T> tree = HAMT.empty();
        for(T value : values){
            tree = tree.put(value.hashCode(),value,value);
        }
        return new HashSet<>(tree);
    }


    public boolean contains(T value){
        return map.get(value.hashCode(),value).isPresent();
    }
    public HashSet<T> plus(T value){
        return new HashSet<>(map.put(value.hashCode(),value,value));
    }
    public HashSet<T> minus(T value){
        return new HashSet<>(map.minus(value.hashCode(),value));
    }
}
