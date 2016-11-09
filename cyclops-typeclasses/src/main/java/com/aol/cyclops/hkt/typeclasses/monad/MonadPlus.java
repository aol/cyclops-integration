package com.aol.cyclops.hkt.typeclasses.monad;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.hkt.alias.Higher;

public interface MonadPlus<CRE,T> extends MonadZero<CRE>{

    Monoid<Higher<CRE,T>> monoid();
        
   
    @Override
    default Higher<CRE, T> zero(){
        return monoid().zero();
    }
    
    default Higher<CRE,T> plus(Higher<CRE,T> a, Higher<CRE,T> b){
        return this.<T>monoid().apply(a,b);         
    }
}
