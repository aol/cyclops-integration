package com.aol.cyclops.javaslang.hkt;

import com.aol.cyclops2.hkt.Higher;
import cyclops.stream.ReactiveSeq;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;



import javaslang.collection.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Javaslang List's
 * 
 * ListType is a List and a Higher Kinded Type (ListType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the List
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListType<T> implements Higher<ListType.µ, T>, Publisher<T>, List<T> {

    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * Construct a HKT encoded completed List
     * 
     * @param value To encode inside a HKT encoded List
     * @return Completed HKT encoded FList
     */
    public static <T> ListType<T> just(T value) {

        return widen(List.of(value));
    }

    public static <T> ListType<T> just(T... values) {

        return widen(List.of(values));
    }

    public static <T> ListType<T> empty() {
        return widen(List.empty());
    }

    /**
     * Convert a List to a simulated HigherKindedType that captures List nature
     * and List element data type separately. Recover via @see ListType#narrow
     * 
     * If the supplied List implements ListType it is returned already, otherwise it
     * is wrapped into a List implementation that does implement ListType
     * 
     * @param List List to widen to a ListType
     * @return ListType encoding HKT info about Lists
     */
    public static <T> ListType<T> widen(final List<T> completableList) {

        return new ListType<>(
                                completableList);
    }

    /**
     * Widen a ListType nested inside another HKT encoded type
     * 
     * @param flux HTK encoded type containing  a List to widen
     * @return HKT encoded type with a widened List
     */
    public static <C2, T> Higher<C2, Higher<ListType.µ, T>> widen2(Higher<C2, ListType<T>> flux) {
        // a functor could be used (if C2 is a functor / one exists for C2 type)
        // instead of casting
        // cast seems safer as Higher<ListType.µ,T> must be a ListType
        return (Higher) flux;
    }

    public static <T> ListType<T> widen(final Publisher<T> completableList) {

        return new ListType<>(
                                List.ofAll(ReactiveSeq.fromPublisher(completableList)));
    }

    /**
     * Convert the raw Higher Kinded Type for ListType types into the ListType type definition class
     * 
     * @param future HKT encoded list into a ListType
     * @return ListType
     */
    public static <T> ListType<T> narrowK(final Higher<ListType.µ, T> future) {
        return (ListType<T>) future;
    }

    /**
     * Convert the HigherKindedType definition for a List into
     * 
     * @param List Type Constructor to convert back into narrowed type
     * @return List from Higher Kinded Type
     */
    public static <T> List<T> narrow(final Higher<ListType.µ, T> completableList) {

        return ((ListType<T>) completableList).narrow();

    }

    private final List<T> boxed;

    public ReactiveSeq<T> toReactiveSeq(){
        return ReactiveSeq.fromIterable(boxed);
    }
    /**
     * @return wrapped List
     */
    public List<T> narrow() {
        return boxed;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        ReactiveSeq.fromIterable(boxed)
                   .subscribe(s);

    }

   

    /**
     * @return
     * @see javaslang.collection.Traversable#head()
     */
    public T head() {
        return boxed.head();
    }
    

    /**
     * @param o
     * @return
     * @see javaslang.Value#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return boxed.equals(o);
    }

    

    /**
     * @return
     * @see javaslang.Value#hashCode()
     */
    public int hashCode() {
        return boxed.hashCode();
    }

   
    /**
     * @return
     * @see javaslang.Value#toString()
     */
    public String toString() {
        return boxed.toString();
    }

   
    

    /**
     * @return
     * @see javaslang.collection.List#tail()
     */
    public List<T> tail() {
        return boxed.tail();
    }

    /**
     * @return
     * @see javaslang.collection.Traversable#isEmpty()
     */
    public boolean isEmpty() {
        return boxed.isEmpty();
    }

    /**
     * @return
     * @see javaslang.collection.List#length()
     */
    public int length() {
        return boxed.length();
    }

    

}
