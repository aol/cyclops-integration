package cyclops.data;


import cyclops.control.Maybe;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeSet<T> implements ImmutableSortedSet<T>{
    private final RedBlackTree.Tree<T,T> map;
    private final Comparator<? super T> comp;

    public static <T> TreeSet<T> empty(Comparator<? super T> comp){
        return new TreeSet<>( RedBlackTree.empty(comp),comp);
    }
    public static <T> TreeSet<T> fromStream(ReactiveSeq<T> stream,Comparator<? super T> comp){
        return stream.foldLeft(empty(comp),(m,t2)->m.plus(t2));
    }

    public ReactiveSeq<T> stream(){
        return map.stream().map(t->t.v1);
    }

    public static <T> TreeSet<T> of(Comparator<? super T> comp, T... values){
        RedBlackTree.Tree<T, T> tree = RedBlackTree.empty(comp);
        for(T value : values){
            tree = tree.plus(value,value);
        }
        return new TreeSet<>(tree,comp);
    }
    public static <T> TreeSet<T> fromSortedSet(SortedSet<T> set, Comparator<? super T> comp){
        Stream<Tuple2<T,T>> s = set.stream().map(e -> Tuple.tuple(e,e));
        return new TreeSet<T>(RedBlackTree.fromStream(set.comparator(),s),comp);
    }

    public boolean contains(T value){
        return map.get(value).isPresent();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public ImmutableSet<T> add(T value) {
        return new TreeSet<>(map.plus(value,value),comp);
    }

    @Override
    public TreeSet<T> remove(T value) {
        return new TreeSet<>(map.minus(value),comp);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public <R> TreeSet<R> map(Function<? super T, ? extends R> fn) {
        return null;
    }

    @Override
    public <R> TreeSet<R> flatMap(Function<? super T, ? extends ImmutableSet<? extends R>> fn) {
        return null;
    }

    @Override
    public <R> TreeSet<R> flatMapI(Function<? super T, ? extends Iterable<? extends R>> fn) {
        return null;
    }

    @Override
    public TreeSet<T> filter(Predicate<? super T> predicate) {
        return null;
    }

    public TreeSet<T> plus(T value){
        return new TreeSet<>(map.plus(value,value),comp);
    }
    public TreeSet<T> minus(T value){
        return new TreeSet<>(map.minus(value),comp);
    }



    @Override
    public Iterator<T> iterator() {
        return stream().iterator();
    }

    @Override
    public Comparator<? super T> comparator() {
        return comp;
    }

    @Override
    public ImmutableSortedSet<T> subSet(T fromElement, T toElement) {
        return null;
    }

    @Override
    public ImmutableSortedSet<T> headSet(T toElement) {
        return null;
    }

    @Override
    public ImmutableSortedSet<T> tailSet(T fromElement) {
        return null;
    }

    @Override
    public Maybe<T> first() {
        return null;
    }

    @Override
    public Maybe<T> last() {
        return null;
    }

    @Override
    public ImmutableList<T> drop(int num) {
        return null;
    }

    @Override
    public ImmutableList<T> take(int num) {
        return null;
    }
}
