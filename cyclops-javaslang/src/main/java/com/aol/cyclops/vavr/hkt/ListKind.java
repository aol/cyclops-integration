package com.aol.cyclops.vavr.hkt;

import com.aol.cyclops2.hkt.Higher;
import cyclops.stream.ReactiveSeq;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;



import javaslang.collection.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Vavr List's
 * 
 * ListKind is a List and a Higher Kinded Type (ListKind.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the List
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListKind<T> implements Higher<ListKind.µ, T>, Publisher<T>, List<T> {

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
    public static <T> ListKind<T> just(T value) {

        return widen(List.of(value));
    }

    public static <T> ListKind<T> just(T... values) {

        return widen(List.of(values));
    }

    public static <T> ListKind<T> empty() {
        return widen(List.empty());
    }

    /**
     * Convert a List to a simulated HigherKindedType that captures List nature
     * and List element data type separately. Recover via @see ListKind#narrow
     * 
     * If the supplied List implements ListKind it is returned already, otherwise it
     * is wrapped into a List implementation that does implement ListKind
     * 
     * @param completableList List to widen to a ListKind
     * @return ListKind encoding HKT info about Lists
     */
    public static <T> ListKind<T> widen(final List<T> completableList) {

        return new ListKind<>(
                                completableList);
    }

    /**
     * Widen a ListKind nested inside another HKT encoded type
     * 
     * @param flux HTK encoded type containing  a List to widen
     * @return HKT encoded type with a widened List
     */
    public static <C2, T> Higher<C2, Higher<ListKind.µ, T>> widen2(Higher<C2, ListKind<T>> flux) {
        // a functor could be used (if C2 is a functor / one exists for C2 type)
        // instead of casting
        // cast seems safer as Higher<ListKind.µ,T> must be a ListKind
        return (Higher) flux;
    }

    public static <T> ListKind<T> widen(final Publisher<T> completableList) {

        return new ListKind<>(
                                List.ofAll(ReactiveSeq.fromPublisher(completableList)));
    }

    /**
     * Convert the raw Higher Kinded Type for ListKind types into the ListKind type definition class
     * 
     * @param future HKT encoded list into a ListKind
     * @return ListKind
     */
    public static <T> ListKind<T> narrowK(final Higher<ListKind.µ, T> future) {
        return (ListKind<T>) future;
    }

    /**
     * Convert the HigherKindedType definition for a List into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return List from Higher Kinded Type
     */
    public static <T> List<T> narrow(final Higher<ListKind.µ, T> list) {

        return ((ListKind<T>) list).narrow();

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
