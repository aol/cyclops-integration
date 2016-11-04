package com.aol.cyclops.hkt;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Queue's
 * 
 * QueueType is a Queue and a Higher Kinded Type (QueueType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Queue
 */

public interface QueueType<T> extends Higher<QueueType.µ, T>, Queue<T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * Convert a Queue to a simulated HigherKindedType that captures Queue nature
     * and Queue element data type separately. Recover via @see QueueType#narrow
     * 
     * If the supplied Queue implements QueueType it is returned already, otherwise it
     * is wrapped into a Queue implementation that does implement QueueType
     * 
     * @param queue Queue to widen to a QueueType
     * @return QueueType encoding HKT info about Queues
     */
    public static <T> QueueType<T> widen(final Queue<T> queue) {
        if (queue instanceof QueueType)
            return (QueueType<T>) queue;
        return new Box<>(queue);
    }

    /**
     * Convert the HigherKindedType definition for a Queue into
     * 
     * @param queue Type Constructor to convert back into narrowed type
     * @return QueueX from Higher Kinded Type
     */
    public static <T> Queue<T> narrow(final Higher<QueueType.µ, T> queue) {
        if (queue instanceof Queue)
            return (Queue)queue;
        //this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) queue;
        return type.narrow();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements QueueType<T> {

        private final Queue<T> boxed;

        /**
         * @return This back as a QueueX
         */
        public Queue<T> narrow() {
            return (Queue)(boxed);
        }

        public boolean add(T e) {
            return boxed.add(e);
        }

        public boolean offer(T e) {
            return boxed.offer(e);
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

        public T remove() {
            return boxed.remove();
        }

        public T poll() {
            return boxed.poll();
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

        public boolean remove(Object o) {
            return boxed.remove(o);
        }

        public boolean containsAll(Collection<?> c) {
            return boxed.containsAll(c);
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

        public boolean equals(Object o) {
            return boxed.equals(o);
        }

        public int hashCode() {
            return boxed.hashCode();
        }

       
    }

}
