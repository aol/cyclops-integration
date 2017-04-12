package com.aol.cyclops.javaslang.hkt;

import com.aol.cyclops2.hkt.Higher;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;


import javaslang.collection.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Javaslang Stream's
 * 
 * StreamType is a Stream and a Higher Kinded Type (StreamType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Stream
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamType<T> implements Higher<StreamType.µ, T>, Publisher<T>, Stream<T> {

    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * Construct a HKT encoded completed Stream
     * 
     * @param value To encode inside a HKT encoded Stream
     * @return Completed HKT encoded FStream
     */
    public static <T> StreamType<T> just(T value) {

        return widen(Stream.of(value));
    }

    public static <T> StreamType<T> just(T... values) {

        return widen(Stream.of(values));
    }

    public static <T> StreamType<T> empty() {
        return widen(Stream.empty());
    }

    /**
     * Convert a Stream to a simulated HigherKindedType that captures Stream nature
     * and Stream element data type separately. Recover via @see StreamType#narrow
     * 
     * If the supplied Stream implements StreamType it is returned already, otherwise it
     * is wrapped into a Stream implementation that does implement StreamType
     * 
     * @param Stream Stream to widen to a StreamType
     * @return StreamType encoding HKT info about Streams
     */
    public static <T> StreamType<T> widen(final Stream<T> completableStream) {

        return new StreamType<>(
                                completableStream);
    }

    /**
     * Widen a StreamType nested inside another HKT encoded type
     * 
     * @param flux HTK encoded type containing  a Stream to widen
     * @return HKT encoded type with a widened Stream
     */
    public static <C2, T> Higher<C2, Higher<StreamType.µ, T>> widen2(Higher<C2, StreamType<T>> flux) {
        // a functor could be used (if C2 is a functor / one exists for C2 type)
        // instead of casting
        // cast seems safer as Higher<StreamType.µ,T> must be a StreamType
        return (Higher) flux;
    }

    public static <T> StreamType<T> widen(final Publisher<T> completableStream) {

        return new StreamType<>(
                                Stream.ofAll(ReactiveSeq.fromPublisher(completableStream)));
    }

    /**
     * Convert the raw Higher Kinded Type for StreamType types into the StreamType type definition class
     * 
     * @param future HKT encoded list into a StreamType
     * @return StreamType
     */
    public static <T> StreamType<T> narrowK(final Higher<StreamType.µ, T> future) {
        return (StreamType<T>) future;
    }

    /**
     * Convert the HigherKindedType definition for a Stream into
     * 
     * @param Stream Type Constructor to convert back into narrowed type
     * @return Stream from Higher Kinded Type
     */
    public static <T> Stream<T> narrow(final Higher<StreamType.µ, T> completableStream) {

        return ((StreamType<T>) completableStream).narrow();

    }

    private final Stream<T> boxed;

    public ReactiveSeq<T> toReactiveSeq(){
        return ReactiveSeq.fromIterable(boxed);
    }
    /**
     * @return wrapped Stream
     */
    public Stream<T> narrow() {
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
     * @see javaslang.collection.Stream#tail()
     */
    public Stream<T> tail() {
        return boxed.tail();
    }

    /**
     * @return
     * @see javaslang.collection.Traversable#isEmpty()
     */
    public boolean isEmpty() {
        return boxed.isEmpty();
    }

    

}
