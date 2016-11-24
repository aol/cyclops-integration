package com.aol.cyclops.functionaljava.hkt;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.jdk.DequeType;

import fj.Equal;
import fj.F;
import fj.F0;
import fj.F2;
import fj.Monoid;
import fj.Ord;
import fj.Ordering;
import fj.P1;
import fj.P2;
import fj.Unit;
import fj.control.Trampoline;
import fj.control.parallel.Promise;
import fj.data.Array;
import fj.data.Either;
import fj.data.IO;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.TreeMap;
import fj.data.Validation;
import fj.data.vector.V2;
import fj.function.Effect1;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for List's
 * 
 * ListType is a List and a Higher Kinded Type (ListType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the List
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class ListType<T> implements Higher<ListType.µ, T> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }
    public static <T> ListType<T> list(final T... values) {
        
        return widen(List.list(values));
    }
    /**
     * Convert a List to a simulated HigherKindedType that captures List nature
     * and List element data type separately. Recover via @see ListType#narrow
     * 
     * If the supplied List implements ListType it is returned already, otherwise it
     * is wrapped into a List implementation that does implement ListType
     * 
     * @param list List to widen to a ListType
     * @return ListType encoding HKT info about Lists
     */
    public static <T> ListType<T> widen(final List<T> list) {
        
        return new ListType<>(list);
    }
    /**
     * Widen a ListType nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a List to widen
     * @return HKT encoded type with a widened List
     */
    public static <C2,T> Higher<C2, Higher<ListType.µ,T>> widen2(Higher<C2, ListType<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<ListType.µ,T> must be a ListType
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for List types into the ListType type definition class
     * 
     * @param list HKT encoded list into a ListType
     * @return ListType
     */
    public static <T> ListType<T> narrowK(final Higher<ListType.µ, T> list) {
       return (ListType<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a List into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return ListX from Higher Kinded Type
     */
    public static <T> List<T> narrow(final Higher<ListType.µ, T> list) {
        return ((ListType)list).narrow();
       
    }


    private final List<T> boxed;

    /**
     * @return This back as a ListX
     */
    public List<T> narrow() {
        return (List) (boxed);
    }
    
    /**
     * @return
     * @see fj.data.List#iterator()
     */
    public final Iterator<T> iterator() {
        return boxed.iterator();
    }
    /**
     * @return
     * @see fj.data.List#head()
     */
    public T head() {
        return boxed.head();
    }
    /**
     * @return
     * @see fj.data.List#tail()
     */
    public List<T> tail() {
        return boxed.tail();
    }
    
    /**
     * @return
     * @see fj.data.List#length()
     */
    public final int length() {
        return boxed.length();
    }
    /**
     * @return
     * @see fj.data.List#isEmpty()
     */
    public final boolean isEmpty() {
        return boxed.isEmpty();
    }
    /**
     * @return
     * @see fj.data.List#isNotEmpty()
     */
    public final boolean isNotEmpty() {
        return boxed.isNotEmpty();
    }
    /**
     * @param nil
     * @param cons
     * @return
     * @deprecated
     * @see fj.data.List#list(java.lang.Object, fj.F)
     */
    public final <B> B list(B nil, F<T, F<List<T>, B>> cons) {
        return boxed.list(nil, cons);
    }
    /**
     * @param cons
     * @param nil
     * @return
     * @see fj.data.List#uncons(fj.F2, java.lang.Object)
     */
    public final <B> B uncons(F2<T, List<T>, B> cons, B nil) {
        return boxed.uncons(cons, nil);
    }
    /**
     * @param a
     * @return
     * @see fj.data.List#orHead(fj.F0)
     */
    public final T orHead(F0<T> a) {
        return boxed.orHead(a);
    }
    /**
     * @param as
     * @return
     * @see fj.data.List#orTail(fj.F0)
     */
    public final List<T> orTail(F0<List<T>> as) {
        return boxed.orTail(as);
    }
    /**
     * @return
     * @deprecated
     * @see fj.data.List#toOption()
     */
    public final Option<T> toOption() {
        return boxed.toOption();
    }
    /**
     * @return
     * @see fj.data.List#headOption()
     */
    public Option<T> headOption() {
        return boxed.headOption();
    }
    /**
     * @param x
     * @return
     * @see fj.data.List#toEither(fj.F0)
     */
    public final <X> Either<X, T> toEither(F0<X> x) {
        return boxed.toEither(x);
    }
    /**
     * @return
     * @see fj.data.List#toStream()
     */
    public final Stream<T> toStream() {
        return boxed.toStream();
    }
    /**
     * @return
     * @see fj.data.List#toArray()
     */
    public final Array<T> toArray() {
        return boxed.toArray();
    }
    /**
     * @return
     * @see fj.data.List#toArrayObject()
     */
    public final Object[] toArrayObject() {
        return boxed.toArrayObject();
    }
    /**
     * @return
     * @see fj.data.List#toJavaArray()
     */
    public final T[] toJavaArray() {
        return boxed.toJavaArray();
    }
    /**
     * @param c
     * @return
     * @see fj.data.List#toArray(java.lang.Class)
     */
    public final Array<T> toArray(Class<T[]> c) {
        return boxed.toArray(c);
    }
    /**
     * @param c
     * @return
     * @see fj.data.List#array(java.lang.Class)
     */
    public final T[] array(Class<T[]> c) {
        return boxed.array(c);
    }
    /**
     * @param a
     * @return
     * @see fj.data.List#cons(java.lang.Object)
     */
    public final List<T> cons(T a) {
        return boxed.cons(a);
    }
    /**
     * @param a
     * @return
     * @see fj.data.List#conss(java.lang.Object)
     */
    public final List<T> conss(T a) {
        return boxed.conss(a);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#map(fj.F)
     */
    public final <B> List<B> map(F<T, B> f) {
        return boxed.map(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#foreach(fj.F)
     */
    public final Unit foreach(F<T, Unit> f) {
        return boxed.foreach(f);
    }
    /**
     * @param f
     * @see fj.data.List#foreachDoEffect(fj.function.Effect1)
     */
    public final void foreachDoEffect(Effect1<T> f) {
        boxed.foreachDoEffect(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#filter(fj.F)
     */
    public final List<T> filter(F<T, Boolean> f) {
        return boxed.filter(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#removeAll(fj.F)
     */
    public final List<T> removeAll(F<T, Boolean> f) {
        return boxed.removeAll(f);
    }
    /**
     * @param a
     * @param e
     * @return
     * @see fj.data.List#delete(java.lang.Object, fj.Equal)
     */
    public final List<T> delete(T a, Equal<T> e) {
        return boxed.delete(a, e);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#takeWhile(fj.F)
     */
    public final List<T> takeWhile(F<T, Boolean> f) {
        return boxed.takeWhile(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#dropWhile(fj.F)
     */
    public final List<T> dropWhile(F<T, Boolean> f) {
        return boxed.dropWhile(f);
    }
    /**
     * @param p
     * @return
     * @see fj.data.List#span(fj.F)
     */
    public final P2<List<T>, List<T>> span(F<T, Boolean> p) {
        return boxed.span(p);
    }
    /**
     * @param p
     * @return
     * @see fj.data.List#breakk(fj.F)
     */
    public final P2<List<T>, List<T>> breakk(F<T, Boolean> p) {
        return boxed.breakk(p);
    }
    /**
     * @param e
     * @return
     * @see fj.data.List#group(fj.Equal)
     */
    public final List<List<T>> group(Equal<T> e) {
        return boxed.group(e);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#bind(fj.F)
     */
    public final <B> List<B> bind(F<T, List<B>> f) {
        return boxed.bind(f);
    }
    /**
     * @param lb
     * @param f
     * @return
     * @see fj.data.List#bind(fj.data.List, fj.F)
     */
    public final <B, C> List<C> bind(List<B> lb, F<T, F<B, C>> f) {
        return boxed.bind(lb, f);
    }
    /**
     * @param lb
     * @param f
     * @return
     * @see fj.data.List#bind(fj.data.List, fj.F2)
     */
    public final <B, C> List<C> bind(List<B> lb, F2<T, B, C> f) {
        return boxed.bind(lb, f);
    }
    /**
     * @param lb
     * @param lc
     * @param f
     * @return
     * @see fj.data.List#bind(fj.data.List, fj.data.List, fj.F)
     */
    public final <B, C, D> List<D> bind(List<B> lb, List<C> lc, F<T, F<B, F<C, D>>> f) {
        return boxed.bind(lb, lc, f);
    }
    /**
     * @param lb
     * @param lc
     * @param ld
     * @param f
     * @return
     * @see fj.data.List#bind(fj.data.List, fj.data.List, fj.data.List, fj.F)
     */
    public final <B, C, D, E> List<E> bind(List<B> lb, List<C> lc, List<D> ld, F<T, F<B, F<C, F<D, E>>>> f) {
        return boxed.bind(lb, lc, ld, f);
    }
    /**
     * @param lb
     * @param lc
     * @param ld
     * @param le
     * @param f
     * @return
     * @see fj.data.List#bind(fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.F)
     */
    public final <B, C, D, E, F$> List<F$> bind(List<B> lb, List<C> lc, List<D> ld, List<E> le,
            F<T, F<B, F<C, F<D, F<E, F$>>>>> f) {
        return boxed.bind(lb, lc, ld, le, f);
    }
    /**
     * @param lb
     * @param lc
     * @param ld
     * @param le
     * @param lf
     * @param f
     * @return
     * @see fj.data.List#bind(fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.F)
     */
    public final <B, C, D, E, F$, G> List<G> bind(List<B> lb, List<C> lc, List<D> ld, List<E> le, List<F$> lf,
            F<T, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
        return boxed.bind(lb, lc, ld, le, lf, f);
    }
    /**
     * @param lb
     * @param lc
     * @param ld
     * @param le
     * @param lf
     * @param lg
     * @param f
     * @return
     * @see fj.data.List#bind(fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.F)
     */
    public final <B, C, D, E, F$, G, H> List<H> bind(List<B> lb, List<C> lc, List<D> ld, List<E> le, List<F$> lf,
            List<G> lg, F<T, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
        return boxed.bind(lb, lc, ld, le, lf, lg, f);
    }
    /**
     * @param lb
     * @param lc
     * @param ld
     * @param le
     * @param lf
     * @param lg
     * @param lh
     * @param f
     * @return
     * @see fj.data.List#bind(fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.data.List, fj.F)
     */
    public final <B, C, D, E, F$, G, H, I> List<I> bind(List<B> lb, List<C> lc, List<D> ld, List<E> le, List<F$> lf,
            List<G> lg, List<H> lh, F<T, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
        return boxed.bind(lb, lc, ld, le, lf, lg, lh, f);
    }
    /**
     * @param bs
     * @return
     * @see fj.data.List#sequence(fj.data.List)
     */
    public final <B> List<B> sequence(List<B> bs) {
        return boxed.sequence(bs);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseOption(fj.F)
     */
    public <B> Option<List<B>> traverseOption(F<T, Option<B>> f) {
        return boxed.traverseOption(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseEither(fj.F)
     */
    public <B, E> Either<E, List<B>> traverseEither(F<T, Either<E, B>> f) {
        return boxed.traverseEither(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseStream(fj.F)
     */
    public <B> Stream<List<B>> traverseStream(F<T, Stream<B>> f) {
        return boxed.traverseStream(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseP1(fj.F)
     */
    public <B> P1<List<B>> traverseP1(F<T, P1<B>> f) {
        return boxed.traverseP1(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseIO(fj.F)
     */
    public <B> IO<List<B>> traverseIO(F<T, IO<B>> f) {
        return boxed.traverseIO(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseF(fj.F)
     */
    public <C, B> F<C, List<B>> traverseF(F<T, F<C, B>> f) {
        return boxed.traverseF(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseTrampoline(fj.F)
     */
    public <B> Trampoline<List<B>> traverseTrampoline(F<T, Trampoline<B>> f) {
        return boxed.traverseTrampoline(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traversePromise(fj.F)
     */
    public <B> Promise<List<B>> traversePromise(F<T, Promise<B>> f) {
        return boxed.traversePromise(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseList(fj.F)
     */
    public <B> List<List<B>> traverseList(F<T, List<B>> f) {
        return boxed.traverseList(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseValidation(fj.F)
     */
    public <E, B> Validation<E, List<B>> traverseValidation(F<T, Validation<E, B>> f) {
        return boxed.traverseValidation(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#traverseV2(fj.F)
     */
    public <B> V2<List<B>> traverseV2(F<T, V2<B>> f) {
        return boxed.traverseV2(f);
    }
    /**
     * @param lf
     * @return
     * @see fj.data.List#apply(fj.data.List)
     */
    public final <B> List<B> apply(List<F<T, B>> lf) {
        return boxed.apply(lf);
    }
    /**
     * @param as
     * @return
     * @see fj.data.List#append(fj.data.List)
     */
    public final List<T> append(List<T> as) {
        return boxed.append(as);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.List#foldRight(fj.F, java.lang.Object)
     */
    public final <B> B foldRight(F<T, F<B, B>> f, B b) {
        return boxed.foldRight(f, b);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.List#foldRight(fj.F2, java.lang.Object)
     */
    public final <B> B foldRight(F2<T, B, B> f, B b) {
        return boxed.foldRight(f, b);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.List#foldRightC(fj.F2, java.lang.Object)
     */
    public final <B> Trampoline<B> foldRightC(F2<T, B, B> f, B b) {
        return boxed.foldRightC(f, b);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.List#foldLeft(fj.F, java.lang.Object)
     */
    public final <B> B foldLeft(F<B, F<T, B>> f, B b) {
        return boxed.foldLeft(f, b);
    }
    /**
     * @param f
     * @param b
     * @return
     * @see fj.data.List#foldLeft(fj.F2, java.lang.Object)
     */
    public final <B> B foldLeft(F2<B, T, B> f, B b) {
        return boxed.foldLeft(f, b);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#foldLeft1(fj.F2)
     */
    public final T foldLeft1(F2<T, T, T> f) {
        return boxed.foldLeft1(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#foldLeft1(fj.F)
     */
    public final T foldLeft1(F<T, F<T, T>> f) {
        return boxed.foldLeft1(f);
    }
    /**
     * @return
     * @see fj.data.List#reverse()
     */
    public final List<T> reverse() {
        return boxed.reverse();
    }
    /**
     * @param i
     * @return
     * @see fj.data.List#index(int)
     */
    public final T index(int i) {
        return boxed.index(i);
    }
    /**
     * @param i
     * @return
     * @see fj.data.List#take(int)
     */
    public final List<T> take(int i) {
        return boxed.take(i);
    }
    /**
     * @param i
     * @return
     * @see fj.data.List#drop(int)
     */
    public final List<T> drop(int i) {
        return boxed.drop(i);
    }
    /**
     * @param i
     * @return
     * @see fj.data.List#splitAt(int)
     */
    public final P2<List<T>, List<T>> splitAt(int i) {
        return boxed.splitAt(i);
    }
    /**
     * @param n
     * @return
     * @see fj.data.List#partition(int)
     */
    public final List<List<T>> partition(int n) {
        return boxed.partition(n);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#partition(fj.F)
     */
    public P2<List<T>, List<T>> partition(F<T, Boolean> f) {
        return boxed.partition(f);
    }
    /**
     * @return
     * @see fj.data.List#inits()
     */
    public final List<List<T>> inits() {
        return boxed.inits();
    }
    /**
     * @return
     * @see fj.data.List#tails()
     */
    public final List<List<T>> tails() {
        return boxed.tails();
    }
    /**
     * @param o
     * @return
     * @see fj.data.List#sort(fj.Ord)
     */
    public final List<T> sort(Ord<T> o) {
        return boxed.sort(o);
    }
    /**
     * @param bs
     * @param f
     * @return
     * @see fj.data.List#zipWith(fj.data.List, fj.F)
     */
    public final <B, C> List<C> zipWith(List<B> bs, F<T, F<B, C>> f) {
        return boxed.zipWith(bs, f);
    }
    /**
     * @param bs
     * @param f
     * @return
     * @see fj.data.List#zipWith(fj.data.List, fj.F2)
     */
    public final <B, C> List<C> zipWith(List<B> bs, F2<T, B, C> f) {
        return boxed.zipWith(bs, f);
    }
    /**
     * @param bs
     * @return
     * @see fj.data.List#zip(fj.data.List)
     */
    public final <B> List<P2<T, B>> zip(List<B> bs) {
        return boxed.zip(bs);
    }
    /**
     * @return
     * @see fj.data.List#zipIndex()
     */
    public final List<P2<T, Integer>> zipIndex() {
        return boxed.zipIndex();
    }
    /**
     * @param a
     * @return
     * @see fj.data.List#snoc(java.lang.Object)
     */
    public final List<T> snoc(T a) {
        return boxed.snoc(a);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#forall(fj.F)
     */
    public final boolean forall(F<T, Boolean> f) {
        return boxed.forall(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#exists(fj.F)
     */
    public final boolean exists(F<T, Boolean> f) {
        return boxed.exists(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#find(fj.F)
     */
    public final Option<T> find(F<T, Boolean> f) {
        return boxed.find(f);
    }
    /**
     * @param a
     * @return
     * @see fj.data.List#intersperse(java.lang.Object)
     */
    public final List<T> intersperse(T a) {
        return boxed.intersperse(a);
    }
    /**
     * @param as
     * @return
     * @see fj.data.List#intercalate(fj.data.List)
     */
    public final List<T> intercalate(List<List<T>> as) {
        return boxed.intercalate(as);
    }
    /**
     * @return
     * @see fj.data.List#nub()
     */
    public final List<T> nub() {
        return boxed.nub();
    }
    /**
     * @param eq
     * @return
     * @see fj.data.List#nub(fj.Equal)
     */
    public final List<T> nub(Equal<T> eq) {
        return boxed.nub(eq);
    }
    /**
     * @param o
     * @return
     * @see fj.data.List#nub(fj.Ord)
     */
    public final List<T> nub(Ord<T> o) {
        return boxed.nub(o);
    }
    /**
     * @return
     * @see fj.data.List#tailOption()
     */
    public Option<List<T>> tailOption() {
        return boxed.tailOption();
    }
    /**
     * @param eq
     * @param xs
     * @return
     * @see fj.data.List#minus(fj.Equal, fj.data.List)
     */
    public final List<T> minus(Equal<T> eq, List<T> xs) {
        return boxed.minus(eq, xs);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#mapM(fj.F)
     */
    public final <B, C> F<B, List<C>> mapM(F<T, F<B, C>> f) {
        return boxed.mapM(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#mapMOption(fj.F)
     */
    public final <B> Option<List<B>> mapMOption(F<T, Option<B>> f) {
        return boxed.mapMOption(f);
    }
    /**
     * @param f
     * @return
     * @see fj.data.List#mapMTrampoline(fj.F)
     */
    public final <B> Trampoline<List<B>> mapMTrampoline(F<T, Trampoline<B>> f) {
        return boxed.mapMTrampoline(f);
    }
    /**
     * @param e
     * @param a
     * @return
     * @see fj.data.List#elementIndex(fj.Equal, java.lang.Object)
     */
    public final Option<Integer> elementIndex(Equal<T> e, T a) {
        return boxed.elementIndex(e, a);
    }
    /**
     * @return
     * @see fj.data.List#last()
     */
    public final T last() {
        return boxed.last();
    }
    /**
     * @return
     * @see fj.data.List#init()
     */
    public final List<T> init() {
        return boxed.init();
    }
    /**
     * @param f
     * @param x
     * @return
     * @see fj.data.List#insertBy(fj.F, java.lang.Object)
     */
    public final List<T> insertBy(F<T, F<T, Ordering>> f, T x) {
        return boxed.insertBy(f, x);
    }
    /**
     * @param o
     * @return
     * @see fj.data.List#mode(fj.Ord)
     */
    public final T mode(Ord<T> o) {
        return boxed.mode(o);
    }
    /**
     * @param keyFunction
     * @return
     * @see fj.data.List#groupBy(fj.F)
     */
    public final <B> TreeMap<B, List<T>> groupBy(F<T, B> keyFunction) {
        return boxed.groupBy(keyFunction);
    }
    /**
     * @param keyFunction
     * @param keyOrd
     * @return
     * @see fj.data.List#groupBy(fj.F, fj.Ord)
     */
    public final <B> TreeMap<B, List<T>> groupBy(F<T, B> keyFunction, Ord<B> keyOrd) {
        return boxed.groupBy(keyFunction, keyOrd);
    }
    /**
     * @param keyFunction
     * @param valueFunction
     * @return
     * @see fj.data.List#groupBy(fj.F, fj.F)
     */
    public final <B, C> TreeMap<B, List<C>> groupBy(F<T, B> keyFunction, F<T, C> valueFunction) {
        return boxed.groupBy(keyFunction, valueFunction);
    }
    /**
     * @param keyFunction
     * @param valueFunction
     * @param keyOrd
     * @return
     * @see fj.data.List#groupBy(fj.F, fj.F, fj.Ord)
     */
    public final <B, C> TreeMap<B, List<C>> groupBy(F<T, B> keyFunction, F<T, C> valueFunction, Ord<B> keyOrd) {
        return boxed.groupBy(keyFunction, valueFunction, keyOrd);
    }
    /**
     * @param keyFunction
     * @param valueFunction
     * @param monoid
     * @param keyOrd
     * @return
     * @see fj.data.List#groupBy(fj.F, fj.F, fj.Monoid, fj.Ord)
     */
    public final <B, C> TreeMap<B, C> groupBy(F<T, B> keyFunction, F<T, C> valueFunction, Monoid<C> monoid,
            Ord<B> keyOrd) {
        return boxed.groupBy(keyFunction, valueFunction, monoid, keyOrd);
    }
    /**
     * @param keyFunction
     * @param valueFunction
     * @param groupingIdentity
     * @param groupingAcc
     * @param keyOrd
     * @return
     * @see fj.data.List#groupBy(fj.F, fj.F, java.lang.Object, fj.F2, fj.Ord)
     */
    public final <B, C, D> TreeMap<B, D> groupBy(F<T, B> keyFunction, F<T, C> valueFunction, D groupingIdentity,
            F2<C, D, D> groupingAcc, Ord<B> keyOrd) {
        return boxed.groupBy(keyFunction, valueFunction, groupingIdentity, groupingAcc, keyOrd);
    }
    /**
     * @param eq
     * @return
     * @see fj.data.List#allEqual(fj.Equal)
     */
    public boolean allEqual(Equal<T> eq) {
        return boxed.allEqual(eq);
    }
    /**
     * @param eq
     * @param xs
     * @return
     * @see fj.data.List#isPrefixOf(fj.Equal, fj.data.List)
     */
    public final boolean isPrefixOf(Equal<T> eq, List<T> xs) {
        return boxed.isPrefixOf(eq, xs);
    }
    /**
     * @param eq
     * @param xs
     * @return
     * @see fj.data.List#isSuffixOf(fj.Equal, fj.data.List)
     */
    public final boolean isSuffixOf(Equal<T> eq, List<T> xs) {
        return boxed.isSuffixOf(eq, xs);
    }
    /**
     * @param o
     * @return
     * @see fj.data.List#maximum(fj.Ord)
     */
    public final T maximum(Ord<T> o) {
        return boxed.maximum(o);
    }
    /**
     * @param o
     * @return
     * @see fj.data.List#minimum(fj.Ord)
     */
    public final T minimum(Ord<T> o) {
        return boxed.minimum(o);
    }
    /**
     * @return
     * @see fj.data.List#toJavaList()
     */
    public final java.util.List<T> toJavaList() {
        return boxed.toJavaList();
    }
    /**
     * @return
     * @see fj.data.List#toCollection()
     */
    public final Collection<T> toCollection() {
        return boxed.toCollection();
    }
    /**
     * @param obj
     * @return
     * @see fj.data.List#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return boxed.equals(obj);
    }
    /**
     * @return
     * @see fj.data.List#hashCode()
     */
    public int hashCode() {
        return boxed.hashCode();
    }
    /**
     * @return
     * @see fj.data.List#toString()
     */
    public String toString() {
        return boxed.toString();
    }
    /**
     * @return
     * @see fj.data.List#isSingle()
     */
    public boolean isSingle() {
        return boxed.isSingle();
    }

}
