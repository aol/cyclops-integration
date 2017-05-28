import com.aol.cyclops2.data.collections.extensions.CollectionX;
import com.aol.cyclops2.types.foldable.To;
import com.github.andrewoma.dexx.collection.HashSet;
import com.github.andrewoma.dexx.collection.List;
import com.github.andrewoma.dexx.collection.TreeSet;
import com.github.andrewoma.dexx.collection.Vector;
import cyclops.collections.dexx.DexxHashSetX;
import cyclops.collections.dexx.DexxListX;
import cyclops.collections.dexx.DexxTreeSetX;
import cyclops.collections.dexx.DexxVectorX;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Class for holding conversion methods between types
 * Use in conjunction with {@link To#to(Function)} for fluent conversions
 *
 * <pre>
 *     {@code
 *      LinkedList<Integer> list1 = ListX.of(1,2,3)
 *                                      .to(Converters::LinkedList);
        ArrayList<Integer> list2 = ListX.of(1,2,3)
                                       .to(Converters::ArrayList);
 *     }
 *
 * </pre>
 */
public interface DexxConverters {




    public static <T extends Comparable<? extends T>> TreeSet<T> TreeSet(CollectionX<T> vec){
        return vec.unwrapIfInstance(TreeSet.class,
                ()-> DexxTreeSetX.copyFromCollection(vec,(Comparator<T>)Comparator.naturalOrder()).unwrap());
    }
    public static <T> HashSet<T> HashSet(CollectionX<T> vec){
        return vec.unwrapIfInstance(HashSet.class,
                ()-> DexxHashSetX.copyFromCollection(vec).unwrap());
    }
    public static <T> TreeSet<T> TreeSet(CollectionX<T> vec, Comparator<T> comp){
        return vec.unwrapIfInstance(TreeSet.class,
                ()-> DexxTreeSetX.copyFromCollection(vec,comp).unwrap());
    }


    public static <T> List<T> List(CollectionX<T> vec){
        return vec.unwrapIfInstance(List.class,
                ()-> DexxListX.copyFromCollection(vec).unwrap());
    }
    public static <T> Vector<T> Vector(CollectionX<T> vec){
        return vec.unwrapIfInstance(Vector.class,
                ()-> DexxVectorX.copyFromCollection(vec).unwrap());
    }


}
