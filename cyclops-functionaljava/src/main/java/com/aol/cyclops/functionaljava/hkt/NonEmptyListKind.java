package com.aol.cyclops.functionaljava.hkt;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;


import com.aol.cyclops2.hkt.Higher;
import cyclops.collections.mutable.ListX;
import cyclops.companion.functionaljava.Lists;
import cyclops.companion.functionaljava.NonEmptyLists;
import cyclops.monads.FJWitness;
import cyclops.monads.FJWitness.nonEmptyList;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.ListT;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for NonEmptyList's
 * 
 * NonEmptyListKind is a NonEmptyList and a Higher Kinded Type (list,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the NonEmptyList
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class NonEmptyListKind<T> implements Higher<nonEmptyList, T> {

    public Active<nonEmptyList,T> allTypeclasses(){
        return Active.of(this, NonEmptyLists.Instances.definitions());
    }

    public <W2,R> Nested<nonEmptyList,W2,R> mapM(Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        return NonEmptyLists.mapM(boxed,fn,defs);
    }

    public <W extends WitnessType<W>> ListT<W, T> liftM(W witness) {
        return ListT.of(witness.adapter().unit(ListX.fromIterable(boxed)));
    }
    public <R> NonEmptyListKind<R> fold(Function<? super NonEmptyList<?  super T>,? extends NonEmptyList<R>> op){
        return widen(op.apply(boxed));
    }
    public static<T> NonEmptyListKind<T> of(T first, T...v){
        
        return widen(NonEmptyList.fromList(List.list(v).cons(first)).some());
    }
    public static <T> Higher<nonEmptyList,T> widenK(final NonEmptyList<T> completableList) {

        return new NonEmptyListKind<T>(
                completableList);
    }
    /**
     * Convert a NonEmptyList to a simulated HigherKindedType that captures NonEmptyList nature
     * and NonEmptyList element data type separately. Recover via @see NonEmptyListKind#narrow
     * 
     * If the supplied NonEmptyList implements NonEmptyListKind it is returned already, otherwise it
     * is wrapped into a NonEmptyList implementation that does implement NonEmptyListKind
     * 
     * @param list NonEmptyList to widen to a NonEmptyListKind
     * @return NonEmptyListKind encoding HKT info about NonEmptyLists
     */
    public static <T> NonEmptyListKind<T> widen(final NonEmptyList<T> list) {
        
        return new NonEmptyListKind<>(list);
    }
    /**
     * Widen a NonEmptyListKind nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a NonEmptyList to widen
     * @return HKT encoded type with a widened NonEmptyList
     */
    public static <C2,T> Higher<C2, Higher<nonEmptyList,T>> widen2(Higher<C2, NonEmptyListKind<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<list,T> must be a NonEmptyListKind
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for NonEmptyList types into the NonEmptyListKind type definition class
     * 
     * @param list HKT encoded list into a NonEmptyListKind
     * @return NonEmptyListKind
     */
    public static <T> NonEmptyListKind<T> narrowK(final Higher<nonEmptyList, T> list) {
       return (NonEmptyListKind<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a NonEmptyList into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return NonEmptyListX from Higher Kinded Type
     */
    public static <T> NonEmptyList<T> narrow(final Higher<nonEmptyList, T> list) {
        return ((NonEmptyListKind)list).narrow();
       
    }


    private final NonEmptyList<T> boxed;

        /**
         * @return This back as a NonEmptyListX
         */
        public NonEmptyList<T> narrow() {
            return (NonEmptyList<T>)(boxed);
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#iterator()
         */
        public Iterator<T> iterator() {
            return boxed.iterator();
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#head()
         */
        public T head() {
            return boxed.head();
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#tail()
         */
        public fj.data.List<T> tail() {
            return boxed.tail();
        }
        /**
         * @param action
         * @see java.lang.Iterable#forEach(java.util.function.Consumer)
         */
        public void forEach(Consumer<? super T> action) {
            boxed.forEach(action);
        }
        /**
         * @param a
         * @return
         * @see fj.data.NonEmptyList#cons(java.lang.Object)
         */
        public NonEmptyList<T> cons(T a) {
            return boxed.cons(a);
        }
        /**
         * @param a
         * @return
         * @see fj.data.NonEmptyList#snoc(java.lang.Object)
         */
        public NonEmptyList<T> snoc(T a) {
            return boxed.snoc(a);
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#length()
         */
        public int length() {
            return boxed.length();
        }
        /**
         * @return
         * @see java.lang.Iterable#spliterator()
         */
        public  Spliterator<T> spliterator() {
            return boxed.spliterator();
        }
        /**
         * @param as
         * @return
         * @see fj.data.NonEmptyList#append(fj.data.NonEmptyList)
         */
        public NonEmptyList<T> append(NonEmptyList<T> as) {
            return boxed.append(as);
        }
        /**
         * @param f
         * @return
         * @see fj.data.NonEmptyList#map(fj.F)
         */
        public <B> NonEmptyList<B> map(F<T, B> f) {
            return boxed.map(f);
        }
        /**
         * @param f
         * @return
         * @see fj.data.NonEmptyList#bind(fj.F)
         */
        public <B> NonEmptyList<B> bind(F<T, NonEmptyList<B>> f) {
            return boxed.bind(f);
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#sublists()
         */
        public NonEmptyList<NonEmptyList<T>> sublists() {
            return boxed.sublists();
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#tails()
         */
        public NonEmptyList<NonEmptyList<T>> tails() {
            return boxed.tails();
        }
        /**
         * @param f
         * @return
         * @see fj.data.NonEmptyList#mapTails(fj.F)
         */
        public <B> NonEmptyList<B> mapTails(F<NonEmptyList<T>, B> f) {
            return boxed.mapTails(f);
        }
        /**
         * @param a
         * @return
         * @see fj.data.NonEmptyList#intersperse(java.lang.Object)
         */
        public NonEmptyList<T> intersperse(T a) {
            return boxed.intersperse(a);
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#reverse()
         */
        public NonEmptyList<T> reverse() {
            return boxed.reverse();
        }
        /**
         * @param o
         * @return
         * @see fj.data.NonEmptyList#sort(fj.Ord)
         */
        public NonEmptyList<T> sort(Ord<T> o) {
            return boxed.sort(o);
        }
        /**
         * @param bs
         * @return
         * @see fj.data.NonEmptyList#zip(fj.data.NonEmptyList)
         */
        public <B> NonEmptyList<P2<T, B>> zip(NonEmptyList<B> bs) {
            return boxed.zip(bs);
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#zipIndex()
         */
        public NonEmptyList<P2<T, Integer>> zipIndex() {
            return boxed.zipIndex();
        }
        /**
         * @param bs
         * @param f
         * @return
         * @see fj.data.NonEmptyList#zipWith(fj.data.List, fj.F)
         */
        public <B, C> NonEmptyList<C> zipWith(fj.data.List<B> bs, F<T, F<B, C>> f) {
            return boxed.zipWith(bs, f);
        }
        /**
         * @param bs
         * @param f
         * @return
         * @see fj.data.NonEmptyList#zipWith(fj.data.List, fj.F2)
         */
        public <B, C> NonEmptyList<C> zipWith(fj.data.List<B> bs, F2<T, B, C> f) {
            return boxed.zipWith(bs, f);
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#toList()
         */
        public fj.data.List<T> toList() {
            return boxed.toList();
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#toCollection()
         */
        public Collection<T> toCollection() {
            return boxed.toCollection();
        }
        /**
         * @param obj
         * @return
         * @see fj.data.NonEmptyList#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            return boxed.equals(obj);
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#hashCode()
         */
        public int hashCode() {
            return boxed.hashCode();
        }
        /**
         * @return
         * @see fj.data.NonEmptyList#toString()
         */
        public String toString() {
            return boxed.toString();
        }

      
}
