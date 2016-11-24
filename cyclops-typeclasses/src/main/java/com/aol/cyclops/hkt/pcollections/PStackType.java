package com.aol.cyclops.hkt.pcollections;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import org.pcollections.PStack;

import com.aol.cyclops.data.collections.extensions.persistent.PStackX;
import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for PStack's
 * 
 * PStackType is a PStack and a Higher Kinded Type (PStackType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the PStack
 */

public interface PStackType<T> extends Higher<PStackType.µ, T>, PStack<T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    public static <T> PStackType<T> of(final T... values) {
        return PStackType.widen(PStackX.of(values));
    }

    /**
     * Convert a PStack to a simulated HigherKindedType that captures PStack nature
     * and PStack element data type separately. Recover via @see PStackType#narrow
     * 
     * If the supplied PStack implements PStackType it is returned already, otherwise it
     * is wrapped into a PStack implementation that does implement PStackType
     * 
     * @param list PStack to widen to a PStackType
     * @return PStackType encoding HKT info about PStacks
     */
    public static <T> PStackType<T> widen(final PStack<T> list) {
        if (list instanceof PStackType)
            return (PStackType<T>) list;
        return new Box<>(list);
    }
    /**
     * Widen a PStackType nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a PStack to widen
     * @return HKT encoded type with a widened PStack
     */
    public static <C2,T> Higher<C2, Higher<PStackType.µ,T>> widen2(Higher<C2, PStackType<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<PStackType.µ,T> must be a PStackType
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for PStack types into the PStackType type definition class
     * 
     * @param list HKT encoded list into a PStackType
     * @return PStackType
     */
    public static <T> PStackType<T> narrowK(final Higher<PStackType.µ, T> list) {
       return (PStackType<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a PStack into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return PStackX from Higher Kinded Type
     */
    public static <T> PStackX<T> narrow(final Higher<PStackType.µ, T> list) {
        if (list instanceof PStack)
            return PStackX.fromIterable((PStack)list);
        //this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) list;
        return type.narrow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements PStackType<T> {

        
        private final PStack<T> boxed;
        

        /**
         * @return This back as a PStackX
         */
        public PStackX<T> narrow() {
            return PStackX.fromCollection(boxed);
        }
        public PStack<T> plus(T e) {
            return boxed.plus(e);
        }
        public PStack<T> plusAll(Collection<? extends T> list) {
            return boxed.plusAll(list);
        }
        public PStack<T> with(int i, T e) {
            return boxed.with(i, e);
        }
        public PStack<T> plus(int i, T e) {
            return boxed.plus(i, e);
        }
        public PStack<T> plusAll(int i, Collection<? extends T> list) {
            return boxed.plusAll(i, list);
        }
        public PStack<T> minus(Object e) {
            return boxed.minus(e);
        }
        public PStack<T> minusAll(Collection<?> list) {
            return boxed.minusAll(list);
        }
        public PStack<T> minus(int i) {
            return boxed.minus(i);
        }
        public PStack<T> subList(int start, int end) {
            return boxed.subList(start, end);
        }
        public boolean add(T o) {
            return boxed.add(o);
        }
        public PStack<T> subList(int start) {
            return boxed.subList(start);
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
