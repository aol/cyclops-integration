package com.oath.cyclops.vavr.hkt;

import java.util.function.Function;


import com.oath.cyclops.hkt.Higher;
import cyclops.collections.vavr.VavrVectorX;
import cyclops.companion.vavr.Vectors;
import cyclops.monads.VavrWitness.vector;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.ListT;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import io.vavr.collection.Vector;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Simulates Higher Kinded Types for Vector's
 *
 * VectorKind is a Vector and a Higher Kinded Type (vector,T)
 *
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Vector
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class VectorKind<T> implements Higher<vector, T>{

    public static <T> Higher<vector,T> widenK(final Vector<T> completableList) {

        return new VectorKind<>(
                completableList);
    }
    public Active<vector,T> allTypeclasses(){
        return Active.of(this, Vectors.Instances.definitions());
    }

    public <W2,R> Nested<vector,W2,R> mapM(Function<? super T,? extends Higher<W2,R>> fn, InstanceDefinitions<W2> defs){
        return Vectors.mapM(boxed,fn,defs);
    }
    public <R> VectorKind<R> fold(Function<? super Vector<? super T>,? extends Vector<R>> op){
        return widen(op.apply(boxed));
    }
    public <W extends WitnessType<W>> ListT<W, T> liftM(W witness) {
        return ListT.of(witness.adapter().unit(VavrVectorX.from(boxed)));
    }
    public static <T> VectorKind<T> of(T element) {
        return  widen(Vector.of(element));
    }


    @SafeVarargs
    public static <T> VectorKind<T> of(T... elements) {
        return widen(Vector.of(elements));
    }
    /**
     * Convert a Vector to a simulated HigherKindedType that captures Vector nature
     * and Vector element data type separately. Recover via @see VectorKind#narrow
     *
     * If the supplied Vector implements VectorKind it is returned already, otherwise it
     * is wrapped into a Vector implementation that does implement VectorKind
     *
     * @param list Vector to widen to a VectorKind
     * @return VectorKind encoding HKT info about Vectors
     */
    public static <T> VectorKind<T> widen(final Vector<T> list) {

        return new VectorKind<>(list);
    }
    /**
     * Widen a VectorKind nested inside another HKT encoded type
     *
     * @param list HTK encoded type containing  a Vector to widen
     * @return HKT encoded type with a widened Vector
     */
    public static <C2,T> Higher<C2, Higher<vector,T>> widen2(Higher<C2, VectorKind<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<vector,T> must be a VectorKind
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for Vector types into the VectorKind type definition class
     *
     * @param list HKT encoded list into a VectorKind
     * @return VectorKind
     */
    public static <T> VectorKind<T> narrowK(final Higher<vector, T> list) {
       return (VectorKind<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a Vector into
     *
     * @param list Type Constructor to convert back into narrowed type
     * @return VectorX from Higher Kinded Type
     */
    public static <T> Vector<T> narrow(final Higher<vector, T> list) {
        return ((VectorKind)list).narrow();

    }


    @Delegate
    private final Vector<T> boxed;

    /**
     * @return This back as a VectorX
     */
    public Vector<T> narrow() {
        return (Vector) (boxed);
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
        return "VectorKind [" + boxed + "]";
    }





}
