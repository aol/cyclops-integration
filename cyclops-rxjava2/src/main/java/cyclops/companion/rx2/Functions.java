package cyclops.companion.rx2;

import com.aol.cyclops2.util.ExceptionSoftener;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by johnmcclean on 13/06/2017.
 */
public class Functions {

    public static <T1,T2,R> BiFunction<? super T1,? super T2, ? extends R> bifunction(io.reactivex.functions.BiFunction<? super T1,? super T2, ? extends R> fn){
        return ExceptionSoftener.softenBiFunction((a,b)->fn.apply(a,b));
    }
    public static <T1,T2,R> io.reactivex.functions.BiFunction<? super T1,? super T2, ? extends R> rxBifunction(BiFunction<? super T1,? super T2, ? extends R> fn){
        return (a,b)->fn.apply(a,b);
    }
    public static <T1,R> Function<? super T1, ? extends R> function(io.reactivex.functions.Function<? super T1, ? extends R> fn){
        return ExceptionSoftener.softenFunction((a)->fn.apply(a));
    }
    public static <T1,R> io.reactivex.functions.Function<? super T1,? extends R> rxFunction(Function<? super T1, ? extends R> fn){
        return (a)->fn.apply(a);
    }
    public static <T1> Predicate<? super T1> predicate(io.reactivex.functions.Predicate<? super T1> fn){
        return ExceptionSoftener.softenPredicate((a)->fn.test(a));
    }
    public static <T1> io.reactivex.functions.Predicate<? super T1> rxPredicate(Predicate<? super T1> fn){
        return (a)->fn.test(a);
    }
}
