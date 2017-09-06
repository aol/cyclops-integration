package cyclops.collections.adt;


import com.aol.cyclops2.types.foldable.Evaluation;
import cyclops.collections.immutable.OrderedSetX;
import cyclops.collections.immutable.PersistentSetX;
import cyclops.control.Maybe;

import java.util.Comparator;
import java.util.SortedSet;

public interface ImmutableSortedSet<T> extends ImmutableSet<T> {

    default OrderedSetX<T> orderedSetX(){
        return stream().to().orderedSetX(Evaluation.LAZY);
    }
    ImmutableSortedSet<? super T> comparator();
    ImmutableSortedSet<T> subSet(T fromElement, T toElement);
    ImmutableSortedSet<T> headSet(T toElement);
    ImmutableSortedSet<T> tailSet(T fromElement);
    Maybe<T> first();
    Maybe<T> last();

    ImmutableList<T> drop(int num);
    ImmutableList<T> take(int num);
}
