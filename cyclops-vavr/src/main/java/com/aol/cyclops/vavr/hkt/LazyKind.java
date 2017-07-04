package com.aol.cyclops.vavr.hkt;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


import cyclops.companion.vavr.Eithers;
import cyclops.companion.vavr.Lazys;
import cyclops.conversion.vavr.FromCyclopsReact;
import cyclops.conversion.vavr.ToCyclopsReact;

import com.aol.cyclops2.hkt.Higher;
import cyclops.control.Eval;

import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.lazy;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.EvalT;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import io.vavr.Lazy;
import io.vavr.collection.Iterator;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for Lazy's
 * 
 * LazyKind is a Lazy and a Higher Kinded Type (lazy,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Lazy
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final  class LazyKind<T> implements Higher<lazy, T> {

    public  Active<lazy,T> allTypeclasses(){
        return Active.of(this, Lazys.Instances.definitions());
    }
    public <W2,R> Nested<lazy,W2,R> mapM(Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Lazy<Higher<W2, R>> e = map(fn);
        LazyKind<Higher<W2, R>> lk = LazyKind.widen(e);
        return Nested.of(lk, Lazys.Instances.definitions(), defs);
    }
    public <R> LazyKind<R> fold(Function<? super Lazy<? super T>,? extends Lazy<R>> op){
        return widen(op.apply(boxed));
    }
    public <T,W extends WitnessType<W>> EvalT<W, T> liftM(Lazy<T> lazy, W witness) {
        return EvalT.of(witness.adapter().unit(ToCyclopsReact.eval(lazy)));
    }
    /**
     * Convert the raw Higher Kinded Type for MaybeType types into the MaybeType type definition class
     * 
     * @param future HKT encoded list into a OptionalType
     * @return MaybeType
     */
    public static <T> LazyKind<T> narrowK(final Higher<lazy, T> future) {
       return (LazyKind<T>)future;
    }
    /**
     * Lazily create an Lazy from the specified Supplier. Supplier#get will only be called once. Return values of Lazy operations will also
     * be cached (later indicates lazy and caching - characteristics can be changed using flatMap).
     * 
     * <pre>
     * {@code
     *   LazyKind<Integer> e = LazyKind.of(()->10)
     *                                 .map(i->i*2);
     *   //LazyKind[20] - lazy so will not be executed until the value is accessed
     * }</pre>
     * 
     * 
     * @param value Supplier to (lazily) populate this Lazy
     * @return LazyKind with specified value
     */
    public static <T> LazyKind<T> of(final Supplier<T> value) {
        return widen(Lazy.of(value));
    }

  
    /**
     * Convert a Lazy to a simulated HigherKindedType that captures Lazy nature
     * and Lazy element data type separately. Recover via @see LazyKind#narrow
     * 
     * If the supplied Lazy implements LazyKind it is returned already, otherwise it
     * is wrapped into a Lazy implementation that does implement LazyKind
     * 
     * @param eval Lazy to widen to a LazyKind
     * @return LazyKind encoding HKT info about Lazys
     */
    public static <T> LazyKind<T> widen(final Lazy<T> eval) {
       
        return new LazyKind<>(eval);
    }
    public static <T> LazyKind<T> widen(final Eval<T> eval) {
        
        return new LazyKind<>(FromCyclopsReact.lazy(eval));
    }

    /**
     * Convert the HigherKindedType definition for a Lazy into
     * 
     * @param eval Type Constructor to convert back into narrowed type
     * @return LazyX from Higher Kinded Type
     */
    public static <T> Lazy<T> narrow(final Higher<lazy, T> eval) {
       
            return ((LazyKind) eval).boxed;
       
    }
    public static <T> Eval<T> narrowEval(final Higher<lazy, T> eval) {
        
        return ToCyclopsReact.eval(((LazyKind) eval).boxed);
   
}

   
       
        private final Lazy<T> boxed;

        /**
         * @return This back as a LazyX
         */
        public Lazy<T> narrow() {
            return (Lazy)boxed;
        }


       

        public T get() {
            return boxed.get();
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
            return "LazyKind [" + boxed + "]";
        }





        /**
         * @param predicate
         * @return
         * @see io.vavr.Lazy#filter(java.util.function.Predicate)
         */
        public Option<T> filter(Predicate<? super T> predicate) {
            return boxed.filter(predicate);
        }

        /**
         * @return
         * @see io.vavr.Lazy#isEmpty()
         */
        public boolean isEmpty() {
            return boxed.isEmpty();
        }




        /**
         * @return
         * @see io.vavr.Lazy#isEvaluated()
         */
        public boolean isEvaluated() {
            return boxed.isEvaluated();
        }




        /**
         * @return
         * @see io.vavr.Lazy#isSingleValued()
         */
        public boolean isSingleValued() {
            return boxed.isSingleValued();
        }




        /**
         * @return
         * @see io.vavr.Lazy#iterator()
         */
        public Iterator<T> iterator() {
            return boxed.iterator();
        }




        /**
         * @param mapper
         * @return
         * @see io.vavr.Lazy#map(java.util.function.Function)
         */
        public <U> Lazy<U> map(Function<? super T, ? extends U> mapper) {
            return boxed.map(mapper);
        }




        /**
         * @param action
         * @return
         * @see io.vavr.Lazy#peek(java.util.function.Consumer)
         */
        public Lazy<T> peek(Consumer<? super T> action) {
            return boxed.peek(action);
        }




        /**
         * @param f
         * @return
         * @see io.vavr.Lazy#transform(java.util.function.Function)
         */
        public <U> U transform(Function<? super Lazy<T>, ? extends U> f) {
            return boxed.transform(f);
        }




        /**
         * @return
         * @see io.vavr.Lazy#stringPrefix()
         */
        public String stringPrefix() {
            return boxed.stringPrefix();
        }




        
              

    

}
