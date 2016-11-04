package com.aol.cyclops.hkt.jdk;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for SortedSet's
 * 
 * SortedSetType is a SortedSet and a Higher Kinded Type (SortedSetType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the SortedSet
 */

public interface SortedSetType<T> extends Higher<SortedSetType.µ, T>, SortedSet<T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * Convert a SortedSet to a simulated HigherKindedType that captures SortedSet nature
     * and SortedSet element data type separately. Recover via @see SortedSetType#narrow
     * 
     * If the supplied SortedSet implements SortedSetType it is returned already, otherwise it
     * is wrapped into a SortedSet implementation that does implement SortedSetType
     * 
     * @param sortedSet SortedSet to widen to a SortedSetType
     * @return SortedSetType encoding HKT info about SortedSets
     */
    public static <T> SortedSetType<T> widen(final SortedSet<T> sortedSet) {
        if (sortedSet instanceof SortedSetType)
            return (SortedSetType<T>) sortedSet;
        return new Box<>(
                         sortedSet);
    }

    /**
     * Convert the HigherKindedType definition for a SortedSet into
     * 
     * @param sortedSet Type Constructor to convert back into narrowed type
     * @return SortedSetX from Higher Kinded Type
     */
    public static <T> SortedSet<T> narrow(final Higher<SortedSetType.µ, T> sortedSet) {
        if (sortedSet instanceof SortedSet)
            return (SortedSet) sortedSet;
        // this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) sortedSet;
        return type.narrow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements SortedSetType<T> {

        private final SortedSet<T> boxed;

        /**
         * @return This back as a SortedSetX
         */
        public SortedSet<T> narrow() {
            return boxed;
        }

        @Override
        public int size() {
            return boxed.size();
        }

        @Override
        public boolean isEmpty() {
            return boxed.isEmpty();
        }

        @Override
        public boolean contains(final Object o) {
            return boxed.contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            return boxed.iterator();
        }

        @Override
        public Comparator<? super T> comparator() {
            return boxed.comparator();
        }

        @Override
        public Object[] toArray() {
            return boxed.toArray();
        }

        @Override
        public SortedSet<T> subSet(final T fromElement, final T toElement) {
            return boxed.subSet(fromElement, toElement);
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return boxed.toArray(a);
        }

        @Override
        public SortedSet<T> headSet(final T toElement) {
            return boxed.headSet(toElement);
        }

        @Override
        public boolean add(final T e) {
            return boxed.add(e);
        }

        @Override
        public SortedSet<T> tailSet(final T fromElement) {
            return boxed.tailSet(fromElement);
        }

        @Override
        public boolean remove(final Object o) {
            return boxed.remove(o);
        }

        @Override
        public T first() {
            return boxed.first();
        }

        @Override
        public T last() {
            return boxed.last();
        }

        @Override
        public boolean containsAll(final Collection<?> c) {
            return boxed.containsAll(c);
        }

        @Override
        public boolean addAll(final Collection<? extends T> c) {
            return boxed.addAll(c);
        }

        @Override
        public boolean retainAll(final Collection<?> c) {
            return boxed.retainAll(c);
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            return boxed.removeAll(c);
        }

        @Override
        public void clear() {
            boxed.clear();
        }

        @Override
        public boolean equals(final Object o) {
            return boxed.equals(o);
        }

        @Override
        public int hashCode() {
            return boxed.hashCode();
        }

    }

}
