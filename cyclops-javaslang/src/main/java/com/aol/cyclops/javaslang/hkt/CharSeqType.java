package com.aol.cyclops.javaslang.hkt;

import com.aol.cyclops.hkt.alias.Higher;

import javaslang.collection.CharSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Simulates Higher Kinded Types for CharSeq's
 * 
 * CharSeqType is a CharSeq and a Higher Kinded Type (CharSeqType.µ,T)
 * 
 * @author johnmcclean
 *
 * @param <T> Data type stored within the CharSeq
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public  class CharSeqType implements Higher<CharSeqType.µ, Character> {
    /**
     * Witness type
     * 
     * @author johnmcclean
     *
     */
    public static class µ {
    }

    /**
     * Convert a CharSeq to a simulated HigherKindedType that captures CharSeq nature
     * and CharSeq element data type separately. Recover via @see CharSeqType#narrow
     * 
     * If the supplied CharSeq implements CharSeqType it is returned already, otherwise it
     * is wrapped into a CharSeq implementation that does implement CharSeqType
     * 
     * @param list CharSeq to widen to a CharSeqType
     * @return CharSeqType encoding HKT info about CharSeqs
     */
    public static <T> CharSeqType widen(final CharSeq list) {
        
        return new CharSeqType(list);
    }
    /**
     * Widen a CharSeqType nested inside another HKT encoded type
     * 
     * @param list HTK encoded type containing  a CharSeq to widen
     * @return HKT encoded type with a widened CharSeq
     */
    public static <C2> Higher<C2, Higher<CharSeqType.µ,Character>> widen2(Higher<C2, CharSeqType> list){
        //a functor could be used (if C2 is a functor / one exists for C2 type) instead of casting
        //cast seems safer as Higher<CharSeqType.µ,T> must be a CharSeqType
        return (Higher)list;
    }
    /**
     * Convert the raw Higher Kinded Type for CharSeq types into the CharSeqType type definition class
     * 
     * @param list HKT encoded list into a CharSeqType
     * @return CharSeqType
     */
    public static  CharSeqType narrowK(final Higher<CharSeqType.µ, Character> list) {
       return (CharSeqType)list;
    }
    /**
     * Convert the HigherKindedType definition for a CharSeq into
     * 
     * @param list Type Constructor to convert back into narrowed type
     * @return CharSeqX from Higher Kinded Type
     */
    public static <T> CharSeq narrow(final Higher<CharSeqType.µ, T> list) {
        return ((CharSeqType)list).narrow();
       
    }


    private final CharSeq boxed;

        /**
         * @return This back as a CharSeqX
         */
        public CharSeq narrow() {
            return (CharSeq)(boxed);
        }

      
}
