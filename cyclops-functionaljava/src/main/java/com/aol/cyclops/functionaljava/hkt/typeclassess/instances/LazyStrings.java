package com.aol.cyclops.functionaljava.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.functionaljava.hkt.LazyStringType;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.foldable.Foldable;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;

import fj.data.LazyString;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Lists
 * @author johnmcclean
 *
 */
@UtilityClass
public class LazyStrings {

    
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  LazyStringType<Integer> list = Lists.functor().map(i->i*2, LazyStringType.widen(Arrays.asList(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Lists
     * <pre>
     * {@code 
     *   LazyStringType<Integer> list = Lists.unit()
                                       .unit("hello")
                                       .then(h->Lists.functor().map((String v) ->v.length(), h))
                                       .convert(LazyStringType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Lists
     */
    public static <T,R>Functor<LazyStringType.µ> functor(){
        BiFunction<LazyStringType,Function<? super Character, ? extends Character>,LazyStringType> map = LazyStrings::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * LazyStringType<String> list = Lists.unit()
                                     .unit("hello")
                                     .convert(LazyStringType::narrowK);
        
        //Arrays.asList("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Lists
     */
    public static Unit<LazyStringType.µ> unit(){
        return General.unit(LazyStrings::of);
    }
   
   
  
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Lists.foldable()
                        .foldLeft(0, (a,b)->a+b, LazyStringType.widen(Arrays.asList(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static  Foldable<LazyStringType.µ> foldable(){
        BiFunction<Monoid<Character>,Higher<LazyStringType.µ,Character>,Character> foldRightFn =  (m,l)-> ListX.fromIterable(LazyStringType.narrow(l).toStream()).foldRight(m);
        BiFunction<Monoid<Character>,Higher<LazyStringType.µ,Character>,Character> foldLeftFn = (m,l)-> ListX.fromIterable(LazyStringType.narrow(l).toStream()).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
   
    private  LazyStringType of(Character value){
        return LazyStringType.widen(LazyString.str(""+value));
    }
    
    private static  LazyStringType map(LazyStringType lt, Function<? super Character, ? extends Character> fn){
        return LazyStringType.widen(LazyStringType.narrow(lt).map(in->fn.apply(in)));
    }
}
