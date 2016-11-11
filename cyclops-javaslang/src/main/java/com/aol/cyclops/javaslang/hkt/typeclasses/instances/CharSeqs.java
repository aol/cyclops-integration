package com.aol.cyclops.javaslang.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.foldable.Foldable;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;
import com.aol.cyclops.javaslang.hkt.CharSeqType;

import javaslang.collection.CharSeq;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Lists
 * @author johnmcclean
 *
 */
@UtilityClass
public class CharSeqs {

    
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  CharSeqType<Integer> list = Lists.functor().map(i->i*2, CharSeqType.widen(Arrays.asList(1,2,3));
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
     *   CharSeqType<Integer> list = Lists.unit()
                                       .unit("hello")
                                       .then(h->Lists.functor().map((String v) ->v.length(), h))
                                       .convert(CharSeqType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Lists
     */
    public static <T,R>Functor<CharSeqType.µ> functor(){
        BiFunction<CharSeqType,Function<? super Character, ? extends Character>,CharSeqType> map = CharSeqs::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * CharSeqType<String> list = Lists.unit()
                                     .unit("hello")
                                     .convert(CharSeqType::narrowK);
        
        //Arrays.asList("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Lists
     */
    public static Unit<CharSeqType.µ> unit(){
        return General.unit(CharSeqs::of);
    }
   
   
  
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Lists.foldable()
                        .foldLeft(0, (a,b)->a+b, CharSeqType.widen(Arrays.asList(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static  Foldable<CharSeqType.µ> foldable(){
        BiFunction<Monoid<Character>,Higher<CharSeqType.µ,Character>,Character> foldRightFn =  (m,l)-> ListX.fromIterable(CharSeqType.narrow(l).toStream()).foldRight(m);
        BiFunction<Monoid<Character>,Higher<CharSeqType.µ,Character>,Character> foldLeftFn = (m,l)-> ListX.fromIterable(CharSeqType.narrow(l).toStream()).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
   
    private  CharSeqType of(Character value){
        return CharSeqType.widen(CharSeq.of(value));
    }
    
    private static  CharSeqType map(CharSeqType lt, Function<? super Character, ? extends Character> fn){

        return CharSeqType.widen(CharSeqType.narrow(lt).map(fn).toCharSeq());
    }
}
