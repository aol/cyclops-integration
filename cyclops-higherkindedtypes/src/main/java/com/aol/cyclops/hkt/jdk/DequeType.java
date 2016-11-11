package com.aol.cyclops.hkt.jdk;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import com.aol.cyclops.hkt.alias.Higher;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Deque's
 * 
 * DequeType is a Deque and a Higher Kinded Type (DequeType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Deque
 */

public interface DequeType<T> extends Higher<DequeType.µ, T>, Deque<T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    public static <T> DequeType<T> of(final T... values) {
        LinkedList<T> list = new LinkedList<>();
        for(T val : values){
            list.add(val);
        }
        return DequeType.widen(list);
    }
    /**
     * Convert a Deque to a simulated HigherKindedType that captures Deque nature
     * and Deque element data type separately. Recover via @see DequeType#narrow
     * 
     * If the supplied Deque implements DequeType it is returned already, otherwise it
     * is wrapped into a Deque implementation that does implement DequeType
     * 
     * @param deque Deque to widen to a DequeType
     * @return DequeType encoding HKT info about Deques
     */
    public static <T> DequeType<T> widen(final Deque<T> deque) {
        if (deque instanceof DequeType)
            return (DequeType<T>) deque;
        return new Box<>(deque);
    }
    /**
     * Widen a DequeType nested inside another HKT encoded type
     * 
     * @param deque HTK encoded type containing  a Deque to widen
     * @return HKT encoded type with a widened Deque
     */
    public static <C2,T> Higher<C2, Higher<DequeType.µ,T>> widen2(Higher<C2, DequeType<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<DequeType.µ,T> must be a ListType
        return (Higher)list;
    }
    /**
     * Convert the HigherKindedType definition for a Deque into
     * 
     * @param deque Type Constructor to convert back into narrowed type
     * @return DequeX from Higher Kinded Type
     */
    public static <T> Deque<T> narrow(final Higher<DequeType.µ, T> deque) {
        if (deque instanceof Deque)
            return (Deque)deque;
        //this code should be unreachable due to HKT type checker
        final Box<T> type = (Box<T>) deque;
        return type.narrow();
    }
    /**
     * Convert the raw Higher Kinded Type for Deque types into the DequeType type definition class
     * 
     * @param deque HKT encoded list into a DequeType
     * @return DequeType
     */
    public static <T> DequeType<T> narrowK(final Higher<DequeType.µ, T> list) {
       return (DequeType<T>)list;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static final class Box<T> implements DequeType<T> {

        private final Deque<T> boxed;

        /**
         * @return This back as a DequeX
         */
        public Deque<T> narrow() {
            return (Deque)(boxed);
        }

       
        public void addFirst(T e) {
            boxed.addFirst(e);
        }

        public boolean isEmpty() {
            return boxed.isEmpty();
        }

        public void addLast(T e) {
            boxed.addLast(e);
        }

        public Object[] toArray() {
            return boxed.toArray();
        }

        public boolean offerFirst(T e) {
            return boxed.offerFirst(e);
        }

        public <T> T[] toArray(T[] a) {
            return boxed.toArray(a);
        }

        public boolean offerLast(T e) {
            return boxed.offerLast(e);
        }

        public T removeFirst() {
            return boxed.removeFirst();
        }

        public T removeLast() {
            return boxed.removeLast();
        }

        public T pollFirst() {
            return boxed.pollFirst();
        }

        public T pollLast() {
            return boxed.pollLast();
        }

        public T getFirst() {
            return boxed.getFirst();
        }

        public T getLast() {
            return boxed.getLast();
        }

        public T peekFirst() {
            return boxed.peekFirst();
        }

        public T peekLast() {
            return boxed.peekLast();
        }

        public boolean removeFirstOccurrence(Object o) {
            return boxed.removeFirstOccurrence(o);
        }

        public boolean removeLastOccurrence(Object o) {
            return boxed.removeLastOccurrence(o);
        }

        public boolean containsAll(Collection<?> c) {
            return boxed.containsAll(c);
        }

        public boolean add(T e) {
            return boxed.add(e);
        }

        public boolean addAll(Collection<? extends T> c) {
            return boxed.addAll(c);
        }

        public boolean offer(T e) {
            return boxed.offer(e);
        }

        public boolean removeAll(Collection<?> c) {
            return boxed.removeAll(c);
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

        public T peek() {
            return boxed.peek();
        }

        public void push(T e) {
            boxed.push(e);
        }

        public boolean retainAll(Collection<?> c) {
            return boxed.retainAll(c);
        }

        public T pop() {
            return boxed.pop();
        }

        public boolean remove(Object o) {
            return boxed.remove(o);
        }

        public void clear() {
            boxed.clear();
        }

        public boolean equals(Object o) {
            return boxed.equals(o);
        }

        public boolean contains(Object o) {
            return boxed.contains(o);
        }

        public int size() {
            return boxed.size();
        }

        public Iterator<T> iterator() {
            return boxed.iterator();
        }

        public Iterator<T> descendingIterator() {
            return boxed.descendingIterator();
        }

        public int hashCode() {
            return boxed.hashCode();
        }


        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "DequeType [" + boxed + "]";
        }

        

    }

}
