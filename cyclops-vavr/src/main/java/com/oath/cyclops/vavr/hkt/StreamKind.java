package com.oath.cyclops.vavr.hkt;

import com.oath.cyclops.hkt.Higher;
import cyclops.companion.vavr.Streams;
import cyclops.monads.VavrWitness.stream;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.StreamT;
import cyclops.reactive.ReactiveSeq;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;


import io.vavr.collection.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * Simulates Higher Kinded Types for Vavr Stream's
 *
 * StreamKind is a Stream and a Higher Kinded Type (StreamKind.µ,T)
 *
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Stream
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamKind<T> implements Higher<stream, T>, Publisher<T>, Stream<T>{

    public Active<stream,T> allTypeclasses(){
        return Active.of(this, Streams.Instances.definitions());
    }

    public static <T> Higher<stream,T> widenK(final Stream<T> completableList) {

        return new StreamKind<>(
                completableList);
    }
    public <W2,R> Nested<stream,W2,R> mapM(Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        return Streams.mapM(boxed,fn,defs);
    }

    public <W extends WitnessType<W>> StreamT<W, T> liftM(W witness) {
        return StreamT.of(witness.adapter().unit(ReactiveSeq.fromStream(boxed.toJavaStream())));
    }
    public <R> StreamKind<R> fold(Function<? super Stream<? super T>,? extends Stream<R>> op){
        return widen(op.apply(boxed));
    }
    /**
     * Construct a HKT encoded completed Stream
     *
     * @param value To encode inside a HKT encoded Stream
     * @return Completed HKT encoded FStream
     */
    public static <T> StreamKind<T> just(T value) {

        return widen(Stream.of(value));
    }

    public static <T> StreamKind<T> just(T... values) {

        return widen(Stream.of(values));
    }

    public static <T> StreamKind<T> empty() {
        return widen(Stream.empty());
    }

    /**
     * Convert a Stream to a simulated HigherKindedType that captures Stream nature
     * and Stream element data type separately. Recover via @see StreamKind#narrow
     *
     * If the supplied Stream implements StreamKind it is returned already, otherwise it
     * is wrapped into a Stream implementation that does implement StreamKind
     *
     * @param stream Stream to widen to a StreamKind
     * @return StreamKind encoding HKT info about Streams
     */
    public static <T> StreamKind<T> widen(final Stream<T> stream) {

        return new StreamKind<>(stream);
    }

    /**
     * Widen a StreamKind nested inside another HKT encoded type
     *
     * @param flux HTK encoded type containing  a Stream to widen
     * @return HKT encoded type with a widened Stream
     */
    public static <C2, T> Higher<C2, Higher<stream, T>> widen2(Higher<C2, StreamKind<T>> flux) {
        // a functor could be used (if C2 is a functor / one exists for C2 type)
        // instead of casting
        // cast seems safer as Higher<stream,T> must be a StreamKind
        return (Higher) flux;
    }

    public static <T> StreamKind<T> widen(final Publisher<T> completableStream) {

        return new StreamKind<>(
                                Stream.ofAll((java.util.stream.Stream<T>)ReactiveSeq.fromPublisher(completableStream)));
    }

    /**
     * Convert the raw Higher Kinded Type for StreamKind types into the StreamKind type definition class
     *
     * @param future HKT encoded list into a StreamKind
     * @return StreamKind
     */
    public static <T> StreamKind<T> narrowK(final Higher<stream, T> future) {
        return (StreamKind<T>) future;
    }

    /**
     * Convert the HigherKindedType definition for a Stream into
     *
     * @param stream Type Constructor to convert back into narrowed type
     * @return Stream from Higher Kinded Type
     */
    public static <T> Stream<T> narrow(final Higher<stream, T> stream) {

        return ((StreamKind<T>) stream).narrow();

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




    public T head() {
        return boxed.head();
    }


    /**
     * @param o
     * @return
     * @see io.vavr.Value#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return boxed.equals(o);
    }



    /**
     * @return
     * @see io.vavr.Value#hashCode()
     */
    public int hashCode() {
        return boxed.hashCode();
    }


    /**
     * @return
     * @see io.vavr.Value#toString()
     */
    public String toString() {
        return boxed.toString();
    }




    /**
     * @return
     * @see io.vavr.collection.Stream#tail()
     */
    public Stream<T> tail() {
        return boxed.tail();
    }

    /**
     * @return
     * @see io.vavr.collection.Traversable#isEmpty()
     */
    public boolean isEmpty() {
        return boxed.isEmpty();
    }



}
