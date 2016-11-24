package com.aol.cyclops.hkt.typeclasses.monad;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.hkt.alias.Higher;

public interface MonadPlus<CRE> extends MonadZero<CRE>{

    Monoid<Higher<CRE,?>> monoid();
        
   default <T> Monoid<Higher<CRE,T>> narrowMonoid(){
       return (Monoid)monoid();
   }
    
    @Override
    default Higher<CRE, ?> zero(){
        return monoid().zero();
    }
    
    default <T> Higher<CRE,T> plus(Higher<CRE,T> a, Higher<CRE,T> b){
        return this.<T>narrowMonoid().apply(a,b);         
    }
}
