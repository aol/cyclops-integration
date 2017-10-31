package com.aol.cyclops.functionaljava.hkt;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;


import com.oath.cyclops.hkt.Higher;
import cyclops.companion.functionaljava.Options;
import cyclops.companion.functionaljava.Streams;
import cyclops.control.Maybe;
import cyclops.monads.FJWitness;
import cyclops.monads.FJWitness.stream;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.MaybeT;
import cyclops.monads.transformers.StreamT;
import cyclops.reactive.ReactiveSeq;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import fj.Equal;
import fj.F;
import fj.F0;
import fj.F2;
import fj.Ord;
import fj.P1;
import fj.P2;
import fj.Unit;
import fj.control.parallel.Strategy;
import fj.data.Array;
import fj.data.Either;
import fj.data.IO;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.function.Effect1;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Stream's
 *
 * StreamKind is a Stream and a Higher Kinded Type (StreamKind.µ,T)
 *
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Stream
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class StreamKind<T> implements Higher<stream, T> {


    public Active<stream,T> allTypeclasses(){
        return Active.of(this, Streams.Instances.definitions());
    }

    public <W2,R> Nested<stream,W2,R> mapM(Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        return Streams.mapM(boxed,fn,defs);
    }

    public <W extends WitnessType<W>> StreamT<W, T> liftM(W witness) {
        return StreamT.of(witness.adapter().unit(ReactiveSeq.fromIterable(boxed)));
    }

    public static <T> Higher<stream,T> widenK(final Stream<T> completableList) {

        return new StreamKind<>(
                completableList);
    }
    public static <T> StreamKind<T> stream(final T... values) {

        return widen(Stream.stream(values));
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
     * @param stream HTK encoded type containing  a Stream to widen
     * @return HKT encoded type with a widened Stream
     */
    public static <C2,T> Higher<C2, Higher<stream,T>> widen2(Higher<C2, StreamKind<T>> stream){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<stream,T> must be a StreamKind
        return (Higher)stream;
    }
    /**
     * Convert the raw Higher Kinded Type for Stream types into the StreamKind type definition class
     *
     * @param stream HKT encoded stream into a StreamKind
     * @return StreamKind
     */
    public static <T> StreamKind<T> narrowK(final Higher<stream, T> stream) {
       return (StreamKind<T>)stream;
    }
    /**
     * Convert the HigherKindedType definition for a Stream into
     *
     * @param stream Type Constructor to convert back into narrowed type
     * @return StreamX from Higher Kinded Type
     */
    public static <T> Stream<T> narrow(final Higher<stream, T> stream) {
        return ((StreamKind)stream).narrow();

    }

    public <R> StreamKind<R> fold(Function<? super Stream<?  super T>,? extends Stream<R>> op){
        return widen(op.apply(boxed));
    }

    private final Stream<T> boxed;

    /**
     * @return This back as a StreamX
     */
    public Stream<T> narrow() {
        return (Stream) (boxed);
    }

    /**
     * @return
     * @see fj.data.Stream#iterator()
     */
    public final Iterator<T> iterator() {
        return boxed.iterator();
    }
    /**
     * @return
     * @see fj.data.Stream#head()
     */
    public T head() {
        return boxed.head();
    }
    /**
     * @return
     * @see fj.data.Stream#tail()
     */
    public P1<Stream<T>> tail() {
        return boxed.tail();
    }
    /**
     * @return
     * @see fj.data.Stream#isEmpty()
     */
    public final boolean isEmpty() {
        return boxed.isEmpty();
    }

    /**
     * @return
     * @see fj.data.Stream#isNotEmpty()
     */
    public final boolean isNotEmpty() {
        return boxed.isNotEmpty();
    }
    /**
     * @param nil
     * @param cons
     * @return
     * @deprecated
     * @see fj.data.Stream#stream(java.lang.Object, fj.F)
     */
    public final <B> B stream(B nil, F<T, F<P1<Stream<T>>, B>> cons) {
        return boxed.stream(nil, cons);
    }
    /**
     * @param nil
     * @param cons
     * @return
     * @see fj.data.Stream#uncons(java.lang.Object, fj.F)
     */
    public final <B> B uncons(B nil, F<T, F<P1<Stream<T>>, B>> cons) {
        return boxed.uncons(nil, cons);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.Stream#foldRight(fj.F, java.lang.Object)
     */
    public final <B> B foldRight(F<T, F<P1<B>, B>> f, B b) {
        return boxed.foldRight(f, b);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.Stream#foldRight(fj.F2, java.lang.Object)
     */
    public final <B> B foldRight(F2<T, P1<B>, B> f, B b) {
        return boxed.foldRight(f, b);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.Stream#foldRight1(fj.F, java.lang.Object)
     */
    public final <B> B foldRight1(F<T, F<B, B>> f, B b) {
        return boxed.foldRight1(f, b);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.Stream#foldRight1(fj.F2, java.lang.Object)
     */
    public final <B> B foldRight1(F2<T, B, B> f, B b) {
        return boxed.foldRight1(f, b);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.Stream#foldLeft(fj.F, java.lang.Object)
     */
    public final <B> B foldLeft(F<B, F<T, B>> f, B b) {
        return boxed.foldLeft(f, b);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.Stream#foldLeft(fj.F2, java.lang.Object)
     */
    public final <B> B foldLeft(F2<B, T, B> f, B b) {
        return boxed.foldLeft(f, b);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#foldLeft1(fj.F2)
     */
    public final T foldLeft1(F2<T, T, T> f) {
        return boxed.foldLeft1(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#foldLeft1(fj.F)
     */
    public final T foldLeft1(F<T, F<T, T>> f) {
        return boxed.foldLeft1(f);
    }
    /**
     * @param a
     * @return
     * @see fj.data.Stream#orHead(fj.F0)
     */
    public final T orHead(F0<T> a) {
        return boxed.orHead(a);
    }
    /**
     * @param as
     * @return
     * @see fj.data.Stream#orTail(fj.F0)
     */
    public final P1<Stream<T>> orTail(F0<Stream<T>> as) {
        return boxed.orTail(as);
    }
    /**
     * @param a
     * @return
     * @see fj.data.Stream#intersperse(java.lang.Object)
     */
    public final Stream<T> intersperse(T a) {
        return boxed.intersperse(a);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#map(fj.F)
     */
    public final <B> Stream<B> map(F<T, B> f) {
        return boxed.map(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#foreach(fj.F)
     */
    public final Unit foreach(F<T, Unit> f) {
        return boxed.foreach(f);
    }
    /**
     * @param f
     * @see fj.data.Stream#foreachDoEffect(fj.function.Effect1)
     */
    public final void foreachDoEffect(Effect1<T> f) {
        boxed.foreachDoEffect(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#filter(fj.F)
     */
    public final Stream<T> filter(F<T, Boolean> f) {
        return boxed.filter(f);
    }
    /**
     * @param as
     * @return
     * @see fj.data.Stream#append(fj.data.Stream)
     */
    public final Stream<T> append(Stream<T> as) {
        return boxed.append(as);
    }
    /**
     * @param as
     * @return
     * @see fj.data.Stream#append(fj.F0)
     */
    public final Stream<T> append(F0<Stream<T>> as) {
        return boxed.append(as);
    }
    /**
     * @param eq
     * @param xs
     * @return
     * @see fj.data.Stream#minus(fj.Equal, fj.data.Stream)
     */
    public final Stream<T> minus(Equal<T> eq, Stream<T> xs) {
        return boxed.minus(eq, xs);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#removeAll(fj.F)
     */
    public final Stream<T> removeAll(F<T, Boolean> f) {
        return boxed.removeAll(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#mapM(fj.F)
     */
    public final <B, C> F<B, Stream<C>> mapM(F<T, F<B, C>> f) {
        return boxed.mapM(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#bind(fj.F)
     */
    public final <B> Stream<B> bind(F<T, Stream<B>> f) {
        return boxed.bind(f);
    }
    /**
     * @param sb
     * @param f
     * @return
     * @see fj.data.Stream#bind(fj.data.Stream, fj.F)
     */
    public final <B, C> Stream<C> bind(Stream<B> sb, F<T, F<B, C>> f) {
        return boxed.bind(sb, f);
    }
    /**
     * @param sb
     * @param f
     * @return
     * @see fj.data.Stream#bind(fj.data.Stream, fj.F2)
     */
    public final <B, C> Stream<C> bind(Stream<B> sb, F2<T, B, C> f) {
        return boxed.bind(sb, f);
    }
    /**
     * @param sb
     * @param sc
     * @param f
     * @return
     * @see fj.data.Stream#bind(fj.data.Stream, fj.data.Stream, fj.F)
     */
    public final <B, C, D> Stream<D> bind(Stream<B> sb, Stream<C> sc, F<T, F<B, F<C, D>>> f) {
        return boxed.bind(sb, sc, f);
    }
    /**
     * @param sb
     * @param sc
     * @param sd
     * @param f
     * @return
     * @see fj.data.Stream#bind(fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.F)
     */
    public final <B, C, D, E> Stream<E> bind(Stream<B> sb, Stream<C> sc, Stream<D> sd, F<T, F<B, F<C, F<D, E>>>> f) {
        return boxed.bind(sb, sc, sd, f);
    }
    /**
     * @param sb
     * @param sc
     * @param sd
     * @param se
     * @param f
     * @return
     * @see fj.data.Stream#bind(fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.F)
     */
    public final <B, C, D, E, F$> Stream<F$> bind(Stream<B> sb, Stream<C> sc, Stream<D> sd, Stream<E> se,
            F<T, F<B, F<C, F<D, F<E, F$>>>>> f) {
        return boxed.bind(sb, sc, sd, se, f);
    }
    /**
     * @param sb
     * @param sc
     * @param sd
     * @param se
     * @param sf
     * @param f
     * @return
     * @see fj.data.Stream#bind(fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.F)
     */
    public final <B, C, D, E, F$, G> Stream<G> bind(Stream<B> sb, Stream<C> sc, Stream<D> sd, Stream<E> se,
            Stream<F$> sf, F<T, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
        return boxed.bind(sb, sc, sd, se, sf, f);
    }
    /**
     * @param sb
     * @param sc
     * @param sd
     * @param se
     * @param sf
     * @param sg
     * @param f
     * @return
     * @see fj.data.Stream#bind(fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.F)
     */
    public final <B, C, D, E, F$, G, H> Stream<H> bind(Stream<B> sb, Stream<C> sc, Stream<D> sd, Stream<E> se,
            Stream<F$> sf, Stream<G> sg, F<T, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
        return boxed.bind(sb, sc, sd, se, sf, sg, f);
    }
    /**
     * @param sb
     * @param sc
     * @param sd
     * @param se
     * @param sf
     * @param sg
     * @param sh
     * @param f
     * @return
     * @see fj.data.Stream#bind(fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.data.Stream, fj.F)
     */
    public final <B, C, D, E, F$, G, H, I> Stream<I> bind(Stream<B> sb, Stream<C> sc, Stream<D> sd, Stream<E> se,
            Stream<F$> sf, Stream<G> sg, Stream<H> sh, F<T, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
        return boxed.bind(sb, sc, sd, se, sf, sg, sh, f);
    }
    /**
     * @param bs
     * @return
     * @see fj.data.Stream#sequence(fj.data.Stream)
     */
    public final <B> Stream<B> sequence(Stream<B> bs) {
        return boxed.sequence(bs);
    }
    /**
     * @param sf
     * @return
     * @see fj.data.Stream#apply(fj.data.Stream)
     */
    public final <B> Stream<B> apply(Stream<F<T, B>> sf) {
        return boxed.apply(sf);
    }
    /**
     * @param as
     * @return
     * @see fj.data.Stream#interleave(fj.data.Stream)
     */
    public final Stream<T> interleave(Stream<T> as) {
        return boxed.interleave(as);
    }
    /**
     * @param o
     * @return
     * @see fj.data.Stream#sort(fj.Ord)
     */
    public final Stream<T> sort(Ord<T> o) {
        return boxed.sort(o);
    }
    /**
     * @param o
     * @param s
     * @return
     * @see fj.data.Stream#sort(fj.Ord, fj.control.parallel.Strategy)
     */
    public final Stream<T> sort(Ord<T> o, Strategy<Unit> s) {
        return boxed.sort(o, s);
    }
    /**
     * @return
     * @see fj.data.Stream#toCollection()
     */
    public final Collection<T> toCollection() {
        return boxed.toCollection();
    }
    /**
     * @param fs
     * @return
     * @see fj.data.Stream#zapp(fj.data.Stream)
     */
    public final <B> Stream<B> zapp(Stream<F<T, B>> fs) {
        return boxed.zapp(fs);
    }
    /**
     * @param bs
     * @param f
     * @return
     * @see fj.data.Stream#zipWith(fj.data.Stream, fj.F)
     */
    public final <B, C> Stream<C> zipWith(Stream<B> bs, F<T, F<B, C>> f) {
        return boxed.zipWith(bs, f);
    }
    /**
     * @param bs
     * @param f
     * @return
     * @see fj.data.Stream#zipWith(fj.data.Stream, fj.F2)
     */
    public final <B, C> Stream<C> zipWith(Stream<B> bs, F2<T, B, C> f) {
        return boxed.zipWith(bs, f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#zipWith(fj.F)
     */
    public final <B, C> F<Stream<B>, Stream<C>> zipWith(F<T, F<B, C>> f) {
        return boxed.zipWith(f);
    }
    /**
     * @param bs
     * @return
     * @see fj.data.Stream#zip(fj.data.Stream)
     */
    public final <B> Stream<P2<T, B>> zip(Stream<B> bs) {
        return boxed.zip(bs);
    }
    /**
     * @return
     * @see fj.data.Stream#zipIndex()
     */
    public final Stream<P2<T, Integer>> zipIndex() {
        return boxed.zipIndex();
    }
    /**
     * @param x
     * @return
     * @see fj.data.Stream#toEither(fj.F0)
     */
    public final <X> Either<X, T> toEither(F0<X> x) {
        return boxed.toEither(x);
    }
    /**
     * @return
     * @see fj.data.Stream#toOption()
     */
    public final Option<T> toOption() {
        return boxed.toOption();
    }
    /**
     * @return
     * @see fj.data.Stream#toJavaArray()
     */
    public final T[] toJavaArray() {
        return boxed.toJavaArray();
    }
    /**
     * @return
     * @see fj.data.Stream#toList()
     */
    public final List<T> toList() {
        return boxed.toList();
    }
    /**
     * @return
     * @see fj.data.Stream#toJavaList()
     */
    public final java.util.List<T> toJavaList() {
        return boxed.toJavaList();
    }
    /**
     * @return
     * @see fj.data.Stream#toArray()
     */
    public final Array<T> toArray() {
        return boxed.toArray();
    }
    /**
     * @param c
     * @return
     * @see fj.data.Stream#toArray(java.lang.Class)
     */
    public final Array<T> toArray(Class<T[]> c) {
        return boxed.toArray(c);
    }
    /**
     * @param c
     * @return
     * @see fj.data.Stream#array(java.lang.Class)
     */
    public final T[] array(Class<T[]> c) {
        return boxed.array(c);
    }
    /**
     * @param a
     * @return
     * @see fj.data.Stream#cons(java.lang.Object)
     */
    public final Stream<T> cons(T a) {
        return boxed.cons(a);
    }
    /**
     * @param a
     * @return
     * @see fj.data.Stream#snoc(java.lang.Object)
     */
    public final Stream<T> snoc(T a) {
        return boxed.snoc(a);
    }
    /**
     * @param a
     * @return
     * @see fj.data.Stream#snoc(fj.F0)
     */
    public final Stream<T> snoc(F0<T> a) {
        return boxed.snoc(a);
    }
    /**
     * @param n
     * @return
     * @see fj.data.Stream#take(int)
     */
    public final Stream<T> take(int n) {
        return boxed.take(n);
    }
    /**
     * @param i
     * @return
     * @see fj.data.Stream#drop(int)
     */
    public final Stream<T> drop(int i) {
        return boxed.drop(i);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#takeWhile(fj.F)
     */
    public final Stream<T> takeWhile(F<T, Boolean> f) {
        return boxed.takeWhile(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#traverseIO(fj.F)
     */
    public final <B> IO<Stream<B>> traverseIO(F<T, IO<B>> f) {
        return boxed.traverseIO(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#traverseOption(fj.F)
     */
    public final <B> Option<Stream<B>> traverseOption(F<T, Option<B>> f) {
        return boxed.traverseOption(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#dropWhile(fj.F)
     */
    public final Stream<T> dropWhile(F<T, Boolean> f) {
        return boxed.dropWhile(f);
    }
    /**
     * @param p
     * @return
     * @see fj.data.Stream#span(fj.F)
     */
    public final P2<Stream<T>, Stream<T>> span(F<T, Boolean> p) {
        return boxed.span(p);
    }
    /**
     * @param p
     * @param a
     * @return
     * @see fj.data.Stream#replace(fj.F, java.lang.Object)
     */
    public final Stream<T> replace(F<T, Boolean> p, T a) {
        return boxed.replace(p, a);
    }
    /**
     * @param p
     * @return
     * @see fj.data.Stream#split(fj.F)
     */
    public final P2<Stream<T>, Stream<T>> split(F<T, Boolean> p) {
        return boxed.split(p);
    }
    /**
     * @return
     * @see fj.data.Stream#reverse()
     */
    public final Stream<T> reverse() {
        return boxed.reverse();
    }
    /**
     * @return
     * @see fj.data.Stream#last()
     */
    public final T last() {
        return boxed.last();
    }
    /**
     * @return
     * @see fj.data.Stream#length()
     */
    public final int length() {
        return boxed.length();
    }
    /**
     * @param i
     * @return
     * @see fj.data.Stream#index(int)
     */
    public final T index(int i) {
        return boxed.index(i);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#forall(fj.F)
     */
    public final boolean forall(F<T, Boolean> f) {
        return boxed.forall(f);
    }
    /**
     * @param other
     * @return
     * @see fj.data.Stream#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        return boxed.equals(other);
    }
    /**
     * @return
     * @see fj.data.Stream#hashCode()
     */
    public int hashCode() {
        return boxed.hashCode();
    }
    /**
     * @return
     * @see fj.data.Stream#toString()
     */
    public String toString() {
        return boxed.toString();
    }
    /**
     * @return
     * @see fj.data.Stream#toStringLazy()
     */
    public String toStringLazy() {
        return boxed.toStringLazy();
    }
    /**
     * @return
     * @see fj.data.Stream#toStringEager()
     */
    public String toStringEager() {
        return boxed.toStringEager();
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#exists(fj.F)
     */
    public final boolean exists(F<T, Boolean> f) {
        return boxed.exists(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.Stream#find(fj.F)
     */
    public final Option<T> find(F<T, Boolean> f) {
        return boxed.find(f);
    }
    /**
     * @param k
     * @return
     * @see fj.data.Stream#cobind(fj.F)
     */
    public final <B> Stream<B> cobind(F<Stream<T>, B> k) {
        return boxed.cobind(k);
    }
    /**
     * @return
     * @see fj.data.Stream#tails()
     */
    public final Stream<Stream<T>> tails() {
        return boxed.tails();
    }
    /**
     * @return
     * @see fj.data.Stream#inits()
     */
    public final Stream<Stream<T>> inits() {
        return boxed.inits();
    }
    /**
     * @return
     * @see fj.data.Stream#substreams()
     */
    public final Stream<Stream<T>> substreams() {
        return boxed.substreams();
    }
    /**
     * @param p
     * @return
     * @see fj.data.Stream#indexOf(fj.F)
     */
    public final Option<Integer> indexOf(F<T, Boolean> p) {
        return boxed.indexOf(p);
    }
    /**
     * @param fs
     * @return
     * @see fj.data.Stream#sequenceW(fj.data.Stream)
     */
    public final <B> Stream<B> sequenceW(Stream<F<Stream<T>, B>> fs) {
        return boxed.sequenceW(fs);
    }
    /**
     * @return
     * @see fj.data.Stream#toFunction()
     */
    public final F<Integer, T> toFunction() {
        return boxed.toFunction();
    }


}
