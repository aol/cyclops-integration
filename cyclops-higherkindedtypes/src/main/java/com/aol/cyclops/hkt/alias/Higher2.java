package com.aol.cyclops.hkt.alias;

/**
 * 
 * Higher Kinded Type - a core type (e.g. a Xor) and the data types which it may store / manipulate (e.g. String and Exception).
 * A fluent semantic alias for  org.derive4j.hkt.__ (awaiting https://github.com/derive4j/hkt/issues/13).
 * T
 * 
 * @author johnmcclean
 *
 * @param <T1> Core type
 * @param <T2> First data type of the Core Type
 * @param <T3> Second data type of the Core type
 */
public interface Higher2<T1,T2,T3> extends Convert<Higher2<T1,T2,T3>>{//,__2<T1,T2, T3> {
    
   
}
