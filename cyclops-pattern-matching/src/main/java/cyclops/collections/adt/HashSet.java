package cyclops.collections.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HashSet<T> {
    private final HAMT.Node<T,T> map;


    public static <T> HashSet<T> of(T... values){
        HAMT.Node<T, T> tree = HAMT.empty();
        for(T value : values){
            tree = tree.plus(0,value.hashCode(),value,value);
        }
        return new HashSet<>(tree);
    }


    public boolean contains(T value){
        return map.get(0,value.hashCode(),value).isPresent();
    }
    public HashSet<T> plus(T value){
        return new HashSet<>(map.plus(0,value.hashCode(),value,value));
    }
    public HashSet<T> minus(T value){
        return new HashSet<>(map.minus(0,value.hashCode(),value));
    }
}
