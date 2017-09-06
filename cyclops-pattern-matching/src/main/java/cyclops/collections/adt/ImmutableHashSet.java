package cyclops.collections.adt;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImmutableHashSet<T> {
    private final HAMT.Node<T,T> map;


    public static <T> ImmutableHashSet<T> of(T... values){
        HAMT.Node<T, T> tree = HAMT.empty();
        for(T value : values){
            tree = tree.plus(0,value.hashCode(),value,value);
        }
        return new ImmutableHashSet<>(tree);
    }


    public boolean contains(T value){
        return map.get(0,value.hashCode(),value).isPresent();
    }
    public ImmutableHashSet<T> plus(T value){
        return new ImmutableHashSet<>(map.plus(0,value.hashCode(),value,value));
    }
    public ImmutableHashSet<T> minus(T value){
        return new ImmutableHashSet<>(map.minus(0,value.hashCode(),value));
    }
}
