package cyclops.collections.adt;


import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderedSet<T> {
    private final RedBlackTree.Tree<T,T> map;

    public ReactiveSeq<T> stream(){
        return map.stream().map(t->t.v1);
    }

    public static <T> OrderedSet<T> of(Comparator<T> comp, T... values){
        RedBlackTree.Tree<T, T> tree = RedBlackTree.empty(comp);
        for(T value : values){
            tree = tree.plus(value,value);
        }
        return new OrderedSet<>(tree);
    }
    public static <T> OrderedSet<T> fromSortedSet(SortedSet<T> set){
        Stream<Tuple2<T,T>> s = set.stream().map(e -> Tuple.tuple(e,e));
        return new OrderedSet<T>(RedBlackTree.fromStream(set.comparator(),s));
    }

    public boolean contains(T value){
        return map.get(value).isPresent();
    }
    public OrderedSet<T> plus(T value){
        return new OrderedSet<>(map.plus(value,value));
    }
    public OrderedSet<T> minus(T value){
        return new OrderedSet<>(map.minus(value));
    }
}
