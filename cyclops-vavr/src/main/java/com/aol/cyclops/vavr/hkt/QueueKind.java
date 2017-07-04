package com.aol.cyclops.vavr.hkt;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;


import com.aol.cyclops2.hkt.Higher;
import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.queue;
import io.vavr.collection.LinearSeq;
import io.vavr.collection.Queue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Simulates Higher Kinded Types for Queue's
 * 
 * QueueKind is a Queue and a Higher Kinded Type (queue,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Queue
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class QueueKind<T> implements Higher<queue, T> {


    public static <T> QueueKind<T> of(T element) {
        return  widen(Queue.of(element));
    }

   
    @SafeVarargs
    public static <T> QueueKind<T> of(T... elements) {
        return widen(Queue.of(elements));
    }
    /**
     * Convert a Queue to a simulated HigherKindedType that captures Queue nature
     * and Queue element data type separately. Recover via @see QueueKind#narrow
     * 
     * If the supplied Queue implements QueueKind it is returned already, otherwise it
     * is wrapped into a Queue implementation that does implement QueueKind
     * 
     * @param list Queue to widen to a QueueKind
     * @return QueueKind encoding HKT info about Queues
     */
    public static <T> QueueKind<T> widen(final Queue<T> list) {
        
        return new QueueKind<>(list);
    }
    /**
     * Widen a QueueKind nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a Queue to widen
     * @return HKT encoded type with a widened Queue
     */
    public static <C2,T> Higher<C2, Higher<queue,T>> widen2(Higher<C2, QueueKind<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<queue,T> must be a QueueKind
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for Queue types into the QueueKind type definition class
     * 
     * @param list HKT encoded list into a QueueKind
     * @return QueueKind
     */
    public static <T> QueueKind<T> narrowK(final Higher<queue, T> list) {
       return (QueueKind<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a Queue into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return QueueX from Higher Kinded Type
     */
    public static <T> Queue<T> narrow(final Higher<queue, T> list) {
        return ((QueueKind)list).narrow();
       
    }

    @Delegate
    private final Queue<T> boxed;

    /**
     * @return This back as a QueueX
     */
    public Queue<T> narrow() {
        return (Queue) (boxed);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return boxed.hashCode();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return boxed.equals(obj);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "QueueKind [" + boxed + "]";
    }

      
}
