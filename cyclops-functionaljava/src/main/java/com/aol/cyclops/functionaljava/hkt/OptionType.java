package com.aol.cyclops.functionaljava.hkt;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.functionaljava.FromCyclopsReact;
import com.aol.cyclops.hkt.alias.Higher;

import fj.F;
import fj.F0;
import fj.F2;
import fj.Ord;
import fj.P1;
import fj.P2;
import fj.P3;
import fj.P4;
import fj.P5;
import fj.P6;
import fj.P7;
import fj.P8;
import fj.Unit;
import fj.data.Array;
import fj.data.Either;
import fj.data.IO;
import fj.data.List;
import fj.data.Option;
import fj.data.Seq;
import fj.data.Set;
import fj.data.Stream;
import fj.data.Validation;
import fj.function.Effect1;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Option's
 * 
 * OptionType is a Option and a Higher Kinded Type (OptionType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Option
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class OptionType<T> implements Higher<OptionType.µ, T>, Iterable<T> {
    private final Option<T> boxed;  
   
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    /**
     * @return An HKT encoded empty Option
     */
    public static <T> OptionType<T> empty() {
        return widen(Option.none());
    }
    
    /**
     * @param value Value to embed in an Option
     * @return An HKT encoded Option
     */
    public static <T> OptionType<T> of(T value) {
        return widen(Option.some(value));
    }
    /**
     * Convert a Option to a simulated HigherKindedType that captures Option nature
     * and Option element data type separately. Recover via @see OptionType#narrow
     * 
     * If the supplied Option implements OptionType it is returned already, otherwise it
     * is wrapped into a Option implementation that does implement OptionType
     * 
     * @param Option Option to widen to a OptionType
     * @return OptionType encoding HKT info about Options
     */
    public static <T> OptionType<T> widen(final Option<T> Option) {
        
        return new OptionType<T>(Option);
    }
    public static <T> OptionType<T> widen(final Maybe<T> option) {
        
        return new OptionType<T>(FromCyclopsReact.option(option));
    }
    /**
     * Convert the raw Higher Kinded Type for OptionType types into the OptionType type definition class
     * 
     * @param future HKT encoded list into a OptionType
     * @return OptionType
     */
    public static <T> OptionType<T> narrowK(final Higher<OptionType.µ, T> future) {
       return (OptionType<T>)future;
    }
    /**
     * Convert the HigherKindedType definition for a Option into
     * 
     * @param Option Type Constructor to convert back into narrowed type
     * @return Option from Higher Kinded Type
     */
    public static <T> Option<T> narrow(final Higher<OptionType.µ, T> Option) {
        //has to be an OptionType as only OptionType can implement Higher<OptionType.µ, T>
         return ((OptionType<T>)Option).boxed;
        
    }
    
    public <R> R visit(Function<? super T, ? extends R> some, Supplier<? extends R> none){
        Option<T> opt = narrow();
        return opt.isNone() ? none.get() : some.apply(opt.some());
       
    }
    public boolean isSome(){
        return boxed.isSome();
    }
    public Option<T> narrow(){
       
        return boxed;
    }

    /**
     * @param action
     * @see java.lang.Iterable#forEach(java.util.function.Consumer)
     */
    public void forEach(Consumer<? super T> action) {
        boxed.forEach(action);
    }

    /**
     * @return
     * @see fj.data.Option#toString()
     */
    public String toString() {
        return boxed.toString();
    }

    /**
     * @return
     * @see fj.data.Option#iterator()
     */
    public final Iterator<T> iterator() {
        return boxed.iterator();
    }

    /**
     * @return
     * @see fj.data.Option#some()
     */
    public T some() {
        return boxed.some();
    }

    /**
     * @return
     * @see java.lang.Iterable#spliterator()
     */
    public Spliterator<T> spliterator() {
        return boxed.spliterator();
    }

    /**
     * @return
     * @see fj.data.Option#isNone()
     */
    public final boolean isNone() {
        return boxed.isNone();
    }

    /**
     * @param b
     * @param f
     * @return
     * @see fj.data.Option#option(java.lang.Object, fj.F)
     */
    public final <B> B option(B b, F<T, B> f) {
        return boxed.option(b, f);
    }

    /**
     * @param b
     * @param f
     * @return
     * @see fj.data.Option#option(fj.F0, fj.F)
     */
    public final <B> B option(F0<B> b, F<T, B> f) {
        return boxed.option(b, f);
    }

    /**
     * @return
     * @see fj.data.Option#length()
     */
    public final int length() {
        return boxed.length();
    }

    /**
     * @param a
     * @return
     * @see fj.data.Option#orSome(fj.F0)
     */
    public final T orSome(F0<T> a) {
        return boxed.orSome(a);
    }

    /**
     * @param a
     * @return
     * @see fj.data.Option#orSome(java.lang.Object)
     */
    public final T orSome(T a) {
        return boxed.orSome(a);
    }

    /**
     * @param message
     * @return
     * @see fj.data.Option#valueE(fj.F0)
     */
    public final T valueE(F0<String> message) {
        return boxed.valueE(message);
    }

    /**
     * @param message
     * @return
     * @see fj.data.Option#valueE(java.lang.String)
     */
    public final T valueE(String message) {
        return boxed.valueE(message);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#map(fj.F)
     */
    public final <B> Option<B> map(F<T, B> f) {
        return boxed.map(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#foreach(fj.F)
     */
    public final Unit foreach(F<T, Unit> f) {
        return boxed.foreach(f);
    }

    /**
     * @param f
     * @see fj.data.Option#foreachDoEffect(fj.function.Effect1)
     */
    public final void foreachDoEffect(Effect1<T> f) {
        boxed.foreachDoEffect(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#filter(fj.F)
     */
    public final Option<T> filter(F<T, Boolean> f) {
        return boxed.filter(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#bind(fj.F)
     */
    public final <B> Option<B> bind(F<T, Option<B>> f) {
        return boxed.bind(f);
    }

    /**
     * @param ob
     * @param f
     * @return
     * @see fj.data.Option#bind(fj.data.Option, fj.F)
     */
    public final <B, C> Option<C> bind(Option<B> ob, F<T, F<B, C>> f) {
        return boxed.bind(ob, f);
    }

    /**
     * @param ob
     * @param oc
     * @param f
     * @return
     * @see fj.data.Option#bind(fj.data.Option, fj.data.Option, fj.F)
     */
    public final <B, C, D> Option<D> bind(Option<B> ob, Option<C> oc, F<T, F<B, F<C, D>>> f) {
        return boxed.bind(ob, oc, f);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @param f
     * @return
     * @see fj.data.Option#bind(fj.data.Option, fj.data.Option, fj.data.Option, fj.F)
     */
    public final <B, C, D, E> Option<E> bind(Option<B> ob, Option<C> oc, Option<D> od, F<T, F<B, F<C, F<D, E>>>> f) {
        return boxed.bind(ob, oc, od, f);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @param oe
     * @param f
     * @return
     * @see fj.data.Option#bind(fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.F)
     */
    public final <B, C, D, E, F$> Option<F$> bind(Option<B> ob, Option<C> oc, Option<D> od, Option<E> oe,
            F<T, F<B, F<C, F<D, F<E, F$>>>>> f) {
        return boxed.bind(ob, oc, od, oe, f);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @param oe
     * @param of
     * @param f
     * @return
     * @see fj.data.Option#bind(fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.F)
     */
    public final <B, C, D, E, F$, G> Option<G> bind(Option<B> ob, Option<C> oc, Option<D> od, Option<E> oe,
            Option<F$> of, F<T, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
        return boxed.bind(ob, oc, od, oe, of, f);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @param oe
     * @param of
     * @param og
     * @param f
     * @return
     * @see fj.data.Option#bind(fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.F)
     */
    public final <B, C, D, E, F$, G, H> Option<H> bind(Option<B> ob, Option<C> oc, Option<D> od, Option<E> oe,
            Option<F$> of, Option<G> og, F<T, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
        return boxed.bind(ob, oc, od, oe, of, og, f);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @param oe
     * @param of
     * @param og
     * @param oh
     * @param f
     * @return
     * @see fj.data.Option#bind(fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.F)
     */
    public final <B, C, D, E, F$, G, H, I> Option<I> bind(Option<B> ob, Option<C> oc, Option<D> od, Option<E> oe,
            Option<F$> of, Option<G> og, Option<H> oh, F<T, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
        return boxed.bind(ob, oc, od, oe, of, og, oh, f);
    }

    /**
     * @param ob
     * @return
     * @see fj.data.Option#bindProduct(fj.data.Option)
     */
    public final <B> Option<P2<T, B>> bindProduct(Option<B> ob) {
        return boxed.bindProduct(ob);
    }

    /**
     * @param ob
     * @param oc
     * @return
     * @see fj.data.Option#bindProduct(fj.data.Option, fj.data.Option)
     */
    public final <B, C> Option<P3<T, B, C>> bindProduct(Option<B> ob, Option<C> oc) {
        return boxed.bindProduct(ob, oc);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @return
     * @see fj.data.Option#bindProduct(fj.data.Option, fj.data.Option, fj.data.Option)
     */
    public final <B, C, D> Option<P4<T, B, C, D>> bindProduct(Option<B> ob, Option<C> oc, Option<D> od) {
        return boxed.bindProduct(ob, oc, od);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @param oe
     * @return
     * @see fj.data.Option#bindProduct(fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option)
     */
    public final <B, C, D, E> Option<P5<T, B, C, D, E>> bindProduct(Option<B> ob, Option<C> oc, Option<D> od,
            Option<E> oe) {
        return boxed.bindProduct(ob, oc, od, oe);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @param oe
     * @param of
     * @return
     * @see fj.data.Option#bindProduct(fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option)
     */
    public final <B, C, D, E, F$> Option<P6<T, B, C, D, E, F$>> bindProduct(Option<B> ob, Option<C> oc, Option<D> od,
            Option<E> oe, Option<F$> of) {
        return boxed.bindProduct(ob, oc, od, oe, of);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @param oe
     * @param of
     * @param og
     * @return
     * @see fj.data.Option#bindProduct(fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option)
     */
    public final <B, C, D, E, F$, G> Option<P7<T, B, C, D, E, F$, G>> bindProduct(Option<B> ob, Option<C> oc,
            Option<D> od, Option<E> oe, Option<F$> of, Option<G> og) {
        return boxed.bindProduct(ob, oc, od, oe, of, og);
    }

    /**
     * @param ob
     * @param oc
     * @param od
     * @param oe
     * @param of
     * @param og
     * @param oh
     * @return
     * @see fj.data.Option#bindProduct(fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option, fj.data.Option)
     */
    public final <B, C, D, E, F$, G, H> Option<P8<T, B, C, D, E, F$, G, H>> bindProduct(Option<B> ob, Option<C> oc,
            Option<D> od, Option<E> oe, Option<F$> of, Option<G> og, Option<H> oh) {
        return boxed.bindProduct(ob, oc, od, oe, of, og, oh);
    }

    /**
     * @param o
     * @return
     * @see fj.data.Option#sequence(fj.data.Option)
     */
    public final <B> Option<B> sequence(Option<B> o) {
        return boxed.sequence(o);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#traverseEither(fj.F)
     */
    public <L, B> Either<L, Option<B>> traverseEither(F<T, Either<L, B>> f) {
        return boxed.traverseEither(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#traverseIO(fj.F)
     */
    public <B> IO<Option<B>> traverseIO(F<T, IO<B>> f) {
        return boxed.traverseIO(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#traverseList(fj.F)
     */
    public <B> List<Option<B>> traverseList(F<T, List<B>> f) {
        return boxed.traverseList(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#traverseOption(fj.F)
     */
    public <B> Option<Option<B>> traverseOption(F<T, Option<B>> f) {
        return boxed.traverseOption(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#traverseStream(fj.F)
     */
    public <B> Stream<Option<B>> traverseStream(F<T, Stream<B>> f) {
        return boxed.traverseStream(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#traverseP1(fj.F)
     */
    public <B> P1<Option<B>> traverseP1(F<T, P1<B>> f) {
        return boxed.traverseP1(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#traverseSeq(fj.F)
     */
    public <B> Seq<Option<B>> traverseSeq(F<T, Seq<B>> f) {
        return boxed.traverseSeq(f);
    }

    /**
     * @param ord
     * @param f
     * @return
     * @see fj.data.Option#traverseSet(fj.Ord, fj.F)
     */
    public <B> Set<Option<B>> traverseSet(Ord<B> ord, F<T, Set<B>> f) {
        return boxed.traverseSet(ord, f);
    }

    /**
     * @return
     * @see fj.data.Option#traverseSet()
     */
    public <B> F2<Ord<B>, F<T, Set<B>>, Set<Option<B>>> traverseSet() {
        return boxed.traverseSet();
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#traverseValidation(fj.F)
     */
    public <E, B> Validation<E, Option<B>> traverseValidation(F<T, Validation<E, B>> f) {
        return boxed.traverseValidation(f);
    }

    /**
     * @param of
     * @return
     * @see fj.data.Option#apply(fj.data.Option)
     */
    public final <B> Option<B> apply(Option<F<T, B>> of) {
        return boxed.apply(of);
    }

    /**
     * @param o
     * @return
     * @see fj.data.Option#orElse(fj.F0)
     */
    public final Option<T> orElse(F0<Option<T>> o) {
        return boxed.orElse(o);
    }

    /**
     * @param o
     * @return
     * @see fj.data.Option#orElse(fj.data.Option)
     */
    public final Option<T> orElse(Option<T> o) {
        return boxed.orElse(o);
    }

    /**
     * @param x
     * @return
     * @see fj.data.Option#toEither(fj.F0)
     */
    public final <X> Either<X, T> toEither(F0<X> x) {
        return boxed.toEither(x);
    }

    /**
     * @param x
     * @return
     * @see fj.data.Option#toEither(java.lang.Object)
     */
    public final <X> Either<X, T> toEither(X x) {
        return boxed.toEither(x);
    }

    /**
     * @param x
     * @return
     * @see fj.data.Option#toValidation(java.lang.Object)
     */
    public final <X> Validation<X, T> toValidation(X x) {
        return boxed.toValidation(x);
    }

    /**
     * @return
     * @see fj.data.Option#toList()
     */
    public final List<T> toList() {
        return boxed.toList();
    }

    /**
     * @return
     * @see fj.data.Option#toStream()
     */
    public final Stream<T> toStream() {
        return boxed.toStream();
    }

    /**
     * @return
     * @see fj.data.Option#toArray()
     */
    public final Array<T> toArray() {
        return boxed.toArray();
    }

    /**
     * @param c
     * @return
     * @see fj.data.Option#toArray(java.lang.Class)
     */
    public final Array<T> toArray(Class<T[]> c) {
        return boxed.toArray(c);
    }

    /**
     * @param c
     * @return
     * @see fj.data.Option#array(java.lang.Class)
     */
    public final T[] array(Class<T[]> c) {
        return boxed.array(c);
    }

    /**
     * @return
     * @see fj.data.Option#toNull()
     */
    public final T toNull() {
        return boxed.toNull();
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#forall(fj.F)
     */
    public final boolean forall(F<T, Boolean> f) {
        return boxed.forall(f);
    }

    /**
     * @param f
     * @return
     * @see fj.data.Option#exists(fj.F)
     */
    public final boolean exists(F<T, Boolean> f) {
        return boxed.exists(f);
    }

    /**
     * @param other
     * @return
     * @see fj.data.Option#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        return boxed.equals(other);
    }

    /**
     * @return
     * @see fj.data.Option#toCollection()
     */
    public final Collection<T> toCollection() {
        return boxed.toCollection();
    }

    /**
     * @return
     * @see fj.data.Option#hashCode()
     */
    public int hashCode() {
        return boxed.hashCode();
    }

    /**
     * @param ob
     * @param f
     * @return
     * @see fj.data.Option#liftM2(fj.data.Option, fj.F2)
     */
    public <B, C> Option<C> liftM2(Option<B> ob, F2<T, B, C> f) {
        return boxed.liftM2(ob, f);
    }

   
   
}
