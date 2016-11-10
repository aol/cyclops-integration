package com.aol.cyclops.hkt.jdk;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

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

        

    }

}
