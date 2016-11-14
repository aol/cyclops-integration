package com.aol.cyclops.hkt.pcollections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.pcollections.PVector;

import com.aol.cyclops.data.collections.extensions.persistent.PVectorX;
import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for PVector's
 * 
 * PVectorType is a PVector and a Higher Kinded Type (PVectorType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the PVector
 */

public interface PVectorType<T> extends Higher<PVectorType.µ, T>, PVector<T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    public static <T> PVectorType<T> of(final T... values) {
        return PVectorType.widen(PVectorX.of(values));
    }

    /**
     * Convert a PVector to a simulated HigherKindedType that captures PVector nature
     * and PVector element data type separately. Recover via @see PVectorType#narrow
     * 
     * If the supplied PVector implements PVectorType it is returned already, otherwise it
     * is wrapped into a PVector implementation that does implement PVectorType
     * 
     * @param list PVector to widen to a PVectorType
     * @return PVectorType encoding HKT info about PVectors
     */
    public static <T> PVectorType<T> widen(final PVector<T> list) {
        if (list instanceof PVectorType)
            return (PVectorType<T>) list;
        return new Box<>(list);
    }
    /**
     * Widen a PVectorType nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a PVector to widen
     * @return HKT encoded type with a widened PVector
     */
    public static <C2,T> Higher<C2, Higher<PVectorType.µ,T>> widen2(Higher<C2, PVectorType<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<PVectorType.µ,T> must be a PVectorType
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for PVector types into the PVectorType type definition class
     * 
     * @param list HKT encoded list into a PVectorType
     * @return PVectorType
     */
    public static <T> PVectorType<T> narrowK(final Higher<PVectorType.µ, T> list) {
       return (PVectorType<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a PVector into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return PVectorX from Higher Kinded Type
     */
    public static <T> PVectorX<T> narrow(final Higher<PVectorType.µ, T> list) {
        if (list instanceof PVector)
            return PVectorX.fromIterable((PVector)list);
        //this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) list;
        return type.narrow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements PVectorType<T> {

        
        private final PVector<T> boxed;
        

        /**
         * @return This back as a PVectorX
         */
        public PVectorX<T> narrow() {
            return PVectorX.fromCollection(boxed);
        }


        public PVector<T> plus(T e) {
            return boxed.plus(e);
        }


        public PVector<T> plusAll(Collection<? extends T> list) {
            return boxed.plusAll(list);
        }


        public PVector<T> with(int i, T e) {
            return boxed.with(i, e);
        }


        public PVector<T> plus(int i, T e) {
            return boxed.plus(i, e);
        }


        public PVector<T> plusAll(int i, Collection<? extends T> list) {
            return boxed.plusAll(i, list);
        }


        public PVector<T> minus(Object e) {
            return boxed.minus(e);
        }


        public PVector<T> minusAll(Collection<?> list) {
            return boxed.minusAll(list);
        }


        public PVector<T> minus(int i) {
            return boxed.minus(i);
        }


        public PVector<T> subList(int start, int end) {
            return boxed.subList(start, end);
        }


        public boolean add(T o) {
            return boxed.add(o);
        }




        public boolean remove(Object o) {
            return boxed.remove(o);
        }


        public boolean addAll(Collection<? extends T> c) {
            return boxed.addAll(c);
        }


        public boolean removeAll(Collection<?> c) {
            return boxed.removeAll(c);
        }


        public boolean retainAll(Collection<?> c) {
            return boxed.retainAll(c);
        }


        public void clear() {
            boxed.clear();
        }


        public boolean addAll(int index, Collection<? extends T> c) {
            return boxed.addAll(index, c);
        }


        public T set(int index, T element) {
            return boxed.set(index, element);
        }


        public void add(int index, T element) {
            boxed.add(index, element);
        }


        public T remove(int index) {
            return boxed.remove(index);
        }


        public int size() {
            return boxed.size();
        }


        public boolean isEmpty() {
            return boxed.isEmpty();
        }


        public boolean contains(Object o) {
            return boxed.contains(o);
        }


        public Iterator<T> iterator() {
            return boxed.iterator();
        }


        public Object[] toArray() {
            return boxed.toArray();
        }


        public <T> T[] toArray(T[] a) {
            return boxed.toArray(a);
        }


        public boolean containsAll(Collection<?> c) {
            return boxed.containsAll(c);
        }


    

        public boolean equals(Object o) {
            return boxed.equals(o);
        }


        public int hashCode() {
            return boxed.hashCode();
        }


       

        public T get(int index) {
            return boxed.get(index);
        }




        public int indexOf(Object o) {
            return boxed.indexOf(o);
        }


        public int lastIndexOf(Object o) {
            return boxed.lastIndexOf(o);
        }


        public ListIterator<T> listIterator() {
            return boxed.listIterator();
        }


        public ListIterator<T> listIterator(int index) {
            return boxed.listIterator(index);
        }
       
       
    }

}
