package com.aol.cyclops.hkt.jdk;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Set's
 * 
 * SetType is a Set and a Higher Kinded Type (SetType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Set
 */

public interface SetType<T> extends Higher<SetType.µ, T>, Set<T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * Convert a Set to a simulated HigherKindedType that captures Set nature
     * and Set element data type separately. Recover via @see SetType#narrow
     * 
     * If the supplied Set implements SetType it is returned already, otherwise it
     * is wrapped into a Set implementation that does implement SetType
     * 
     * @param Set Set to widen to a SetType
     * @return SetType encoding HKT info about Sets
     */
    public static <T> SetType<T> widen(final Set<T> set) {
        if (set instanceof SetType)
            return (SetType<T>) set;
        return new Box<>(
                         set);
    }

    /**
     * Convert the HigherKindedType definition for a Set into
     * 
     * @param Set Type Constructor to convert back into narrowed type
     * @return SetX from Higher Kinded Type
     */
    public static <T> Set<T> narrow(final Higher<SetType.µ, T> set) {
        if (set instanceof Set)
            return (Set) set;
        // this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) set;
        return type.narrow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements SetType<T> {

        private final Set<T> boxed;

        /**
         * @return This back as a SetX
         */
        public Set<T> narrow() {
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
        public Object[] toArray() {
            return boxed.toArray();
        }

        @Override
        public <T> T[] toArray(final T[] a) {
            return boxed.toArray(a);
        }

        @Override
        public boolean add(final T e) {
            return boxed.add(e);
        }

        @Override
        public boolean remove(final Object o) {
            return boxed.remove(o);
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
