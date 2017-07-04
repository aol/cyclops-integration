package com.aol.cyclops.vavr.hkt;

import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.types.Unwrapable;
import cyclops.companion.vavr.Arrays;
import cyclops.companion.vavr.Lazys;
import cyclops.monads.VavrWitness;
import cyclops.monads.VavrWitness.array;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import io.vavr.Lazy;
import io.vavr.collection.Array;
import io.vavr.collection.Queue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import java.util.function.Function;

/**
 * Simulates Higher Kinded Types for Array's
 * 
 * ArrayKind is a Array and a Higher Kinded Type (array,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Array
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class ArrayKind<T> implements Higher<array, T>{


    public Active<array,T> allTypeclasses(){
        return Active.of(this, Arrays.Instances.definitions());
    }

    public <W2,R> Nested<array,W2,R> mapM(Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        Array<Higher<W2, R>> e = map(fn);
        ArrayKind<Higher<W2, R>> lk = ArrayKind.widen(e);
        return Nested.of(lk, Arrays.Instances.definitions(), defs);
    }
    public <R> ArrayKind<R> fold(Function<? super Array<? super T>,? extends Array<R>> op){
        return widen(op.apply(boxed));
    }
    public static <T> ArrayKind<T> of(T element) {
        return  widen(Array.of(element));
    }

   
    @SafeVarargs
    public static <T> ArrayKind<T> of(T... elements) {
        return widen(Array.of(elements));
    }
    /**
     * Convert a Array to a simulated HigherKindedType that captures Array nature
     * and Array element data type separately. Recover via @see ArrayKind#narrow
     * 
     * If the supplied Array implements ArrayKind it is returned already, otherwise it
     * is wrapped into a Array implementation that does implement ArrayKind
     * 
     * @param list Array to widen to a ArrayKind
     * @return ArrayKind encoding HKT info about Arrays
     */
    public static <T> ArrayKind<T> widen(final Array<T> list) {
        
        return new ArrayKind<>(list);
    }
    /**
     * Widen a ArrayKind nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a Array to widen
     * @return HKT encoded type with a widened Array
     */
    public static <C2,T> Higher<C2, Higher<array,T>> widen2(Higher<C2, ArrayKind<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<array,T> must be a ArrayKind
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for Array types into the ArrayKind type definition class
     * 
     * @param list HKT encoded list into a ArrayKind
     * @return ArrayKind
     */
    public static <T> ArrayKind<T> narrowK(final Higher<array, T> list) {
       return (ArrayKind<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a Array into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return ArrayX from Higher Kinded Type
     */
    public static <T> Array<T> narrow(final Higher<array, T> list) {
        return ((ArrayKind)list).narrow();
       
    }

    @Delegate
    private final Array<T> boxed;

    /**
     * @return This back as a ArrayX
     */
    public Array<T> narrow() {
        return (Array) (boxed);
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
        return "ArrayKind [" + boxed + "]";
    }


}
