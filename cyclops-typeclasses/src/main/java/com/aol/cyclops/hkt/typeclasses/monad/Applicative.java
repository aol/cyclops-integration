package com.aol.cyclops.hkt.typeclasses.monad;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;

public interface Applicative<CRE> extends Functor<CRE>,Unit<CRE>{
    
    
    public <T,R> Higher<CRE,R> ap(Higher<CRE, Function<? super T, ? extends R>> fn, Higher<CRE,T> apply);
    
    /**
     * The default implementation of apBiFn is less efficient than ap2 (extra map operation)
     * 
     * @param fn
     * @param apply
     * @param apply2
     * @return
     */
    default <T,T2,R> Higher<CRE,R> apBiFn(Higher<CRE, BiFunction<? super T,? super T2,? extends R>> fn, Higher<CRE,T> apply,Higher<CRE,T2> apply2){
        return  ap(ap(map(Applicative::curry2,fn), apply), apply2);
    }
    
    default <T,T2,R> Higher<CRE,R> ap2(Higher<CRE, Function<? super T,? extends Function<? super T2,? extends R>>> fn, Higher<CRE,T> apply,Higher<CRE,T2> apply2){
        return  ap(ap(fn, apply), apply2);
    }
    default <T,T2,T3,R> Higher<CRE,R> ap3(Higher<CRE, Function<? super T,? extends Function<? super T2,? extends Function<? super T3,? extends R>>>> fn, 
                                                                Higher<CRE,T> apply,Higher<CRE,T2> apply2,Higher<CRE,T3> apply3){
        return  ap(ap(ap(fn, apply), apply2),apply3);
    }
  
    
    public static <T1, T2, R> Function<? super T1, Function<? super T2, ? extends R>> curry2(
            final BiFunction<? super T1, ? super T2, ? extends R> biFunc) {
        return t1 -> t2 -> biFunc.apply(t1, t2);
    }

}
