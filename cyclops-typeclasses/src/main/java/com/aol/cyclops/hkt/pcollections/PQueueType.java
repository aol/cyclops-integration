package com.aol.cyclops.hkt.pcollections;

import java.util.Collection;
import java.util.Iterator;

import org.pcollections.PCollection;
import org.pcollections.PQueue;

import com.aol.cyclops.data.collections.extensions.persistent.PQueueX;
import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for PQueue's
 * 
 * PQueueType is a PQueue and a Higher Kinded Type (PQueueType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the PQueue
 */

public interface PQueueType<T> extends Higher<PQueueType.µ, T>, PQueue<T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    public static <T> PQueueType<T> of(final T... values) {
        return PQueueType.widen(PQueueX.of(values));
    }

    /**
     * Convert a PQueue to a simulated HigherKindedType that captures PQueue nature
     * and PQueue element data type separately. Recover via @see PQueueType#narrow
     * 
     * If the supplied PQueue implements PQueueType it is returned already, otherwise it
     * is wrapped into a PQueue implementation that does implement PQueueType
     * 
     * @param list PQueue to widen to a PQueueType
     * @return PQueueType encoding HKT info about PQueues
     */
    public static <T> PQueueType<T> widen(final PQueue<T> list) {
        if (list instanceof PQueueType)
            return (PQueueType<T>) list;
        return new Box<>(list);
    }
    /**
     * Widen a PQueueType nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a PQueue to widen
     * @return HKT encoded type with a widened PQueue
     */
    public static <C2,T> Higher<C2, Higher<PQueueType.µ,T>> widen2(Higher<C2, PQueueType<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<PQueueType.µ,T> must be a PQueueType
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for PQueue types into the PQueueType type definition class
     * 
     * @param list HKT encoded list into a PQueueType
     * @return PQueueType
     */
    public static <T> PQueueType<T> narrowK(final Higher<PQueueType.µ, T> list) {
       return (PQueueType<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a PQueue into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return PQueueX from Higher Kinded Type
     */
    public static <T> PQueueX<T> narrow(final Higher<PQueueType.µ, T> list) {
        if (list instanceof PQueue)
            return PQueueX.fromIterable((PQueue)list);
        //this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) list;
        return type.narrow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements PQueueType<T> {

        
        private final PQueue<T> boxed;
        

        /**
         * @return This back as a PQueueX
         */
        public PQueueX<T> narrow() {
            return PQueueX.fromCollection(boxed);
        }


        public PQueue<T> minus() {
            return boxed.minus();
        }


        public PQueue<T> plus(T e) {
            return boxed.plus(e);
        }


        public PQueue<T> plusAll(Collection<? extends T> list) {
            return boxed.plusAll(list);
        }


        public PCollection<T> minus(Object e) {
            return boxed.minus(e);
        }


        public PCollection<T> minusAll(Collection<?> list) {
            return boxed.minusAll(list);
        }


        public boolean offer(T o) {
            return boxed.offer(o);
        }


        public T poll() {
            return boxed.poll();
        }


        public T remove() {
            return boxed.remove();
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


        public int size() {
            return boxed.size();
        }


        public boolean isEmpty() {
            return boxed.isEmpty();
        }


        public boolean contains(Object o) {
            return boxed.contains(o);
        }


        public T element() {
            return boxed.element();
        }


        public Iterator<T> iterator() {
            return boxed.iterator();
        }


        public T peek() {
            return boxed.peek();
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


       
       
    }

}
