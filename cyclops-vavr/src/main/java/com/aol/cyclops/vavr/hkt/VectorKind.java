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
import io.vavr.collection.Vector;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

/**
 * Simulates Higher Kinded Types for Vector's
 * 
 * VectorKind is a Vector and a Higher Kinded Type (VectorKind.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the Vector
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class VectorKind<T> implements Higher<VectorKind.µ, T>{
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
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
    public static <C2,T> Higher<C2, Higher<VectorKind.µ,T>> widen2(Higher<C2, VectorKind<T>> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<VectorKind.µ,T> must be a VectorKind
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for Vector types into the VectorKind type definition class
     * 
     * @param list HKT encoded list into a VectorKind
     * @return VectorKind
     */
    public static <T> VectorKind<T> narrowK(final Higher<VectorKind.µ, T> list) {
       return (VectorKind<T>)list;
    }
    /**
     * Convert the HigherKindedType definition for a Vector into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return VectorX from Higher Kinded Type
     */
    public static <T> Vector<T> narrow(final Higher<VectorKind.µ, T> list) {
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
