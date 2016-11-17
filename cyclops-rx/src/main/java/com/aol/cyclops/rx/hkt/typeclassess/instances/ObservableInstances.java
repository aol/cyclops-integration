package com.aol.cyclops.rx.hkt.typeclassess.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.foldable.Foldable;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;
import com.aol.cyclops.hkt.typeclasses.monad.Applicative;
import com.aol.cyclops.hkt.typeclasses.monad.Monad;
import com.aol.cyclops.hkt.typeclasses.monad.MonadPlus;
import com.aol.cyclops.hkt.typeclasses.monad.MonadZero;
import com.aol.cyclops.hkt.typeclasses.monad.Traverse;
import com.aol.cyclops.rx.hkt.ObservableType;

import lombok.experimental.UtilityClass;
import rx.Observable;
import rx.functions.Func1;

/**
 * Companion class for creating Type Class instances for working with Observables
 * @author johnmcclean
 *
 */
@UtilityClass
public class ObservableInstances {

   
    /**
     * 
     * Transform a observable, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  ObservableType<Integer> observable = Observables.functor().map(i->i*2, ObservableType.widen(Observable.of(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Observables
     * <pre>
     * {@code 
     *   ObservableType<Integer> observable = Observables.unit()
                                       .unit("hello")
                                       .then(h->Observables.functor().map((String v) ->v.length(), h))
                                       .convert(ObservableType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Observables
     */
    public static <T,R>Functor<ObservableType.µ> functor(){
        BiFunction<ObservableType<T>,Function<? super T, ? extends R>,ObservableType<R>> map = ObservableInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * ObservableType<String> observable = Observables.unit()
                                     .unit("hello")
                                     .convert(ObservableType::narrowK);
        
        //Observable.of("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Observables
     */
    public static Unit<ObservableType.µ> unit(){
        return General.unit(ObservableInstances::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ObservableType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * 
       Observables.zippingApplicative()
            .ap(widen(Observable.of(l1(this::multiplyByTwo))),widen(Observable.of(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * ObservableType<Function<Integer,Integer>> observableFn =Observables.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(ObservableType::narrowK);
        
        ObservableType<Integer> observable = Observables.unit()
                                      .unit("hello")
                                      .then(h->Observables.functor().map((String v) ->v.length(), h))
                                      .then(h->Observables.zippingApplicative().ap(observableFn, h))
                                      .convert(ObservableType::narrowK);
        
        //Observable.of("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Observables
     */
    public static <T,R> Applicative<ObservableType.µ> zippingApplicative(){
        BiFunction<ObservableType< Function<T, R>>,ObservableType<T>,ObservableType<R>> ap = ObservableInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.ObservableType.widen;
     * ObservableType<Integer> observable  = Observables.monad()
                                      .flatMap(i->widen(ObservableX.range(0,i)), widen(Observable.of(1,2,3)))
                                      .convert(ObservableType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    ObservableType<Integer> observable = Observables.unit()
                                        .unit("hello")
                                        .then(h->Observables.monad().flatMap((String v) ->Observables.unit().unit(v.length()), h))
                                        .convert(ObservableType::narrowK);
        
        //Observable.of("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Observables
     */
    public static <T,R> Monad<ObservableType.µ> monad(){
  
        BiFunction<Higher<ObservableType.µ,T>,Function<? super T, ? extends Higher<ObservableType.µ,R>>,Higher<ObservableType.µ,R>> flatMap = ObservableInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  ObservableType<String> observable = Observables.unit()
                                         .unit("hello")
                                         .then(h->Observables.monadZero().filter((String t)->t.startsWith("he"), h))
                                         .convert(ObservableType::narrowK);
        
       //Observable.of("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<ObservableType.µ> monadZero(){
        BiFunction<Higher<ObservableType.µ,T>,Predicate<? super T>,Higher<ObservableType.µ,T>> filter = ObservableInstances::filter;
        Supplier<Higher<ObservableType.µ, T>> zero = ()->ObservableType.widen(Observable.empty());
        return General.<ObservableType.µ,T,R>monadZero(monad(), zero,filter);
    }
    /**
     * <pre>
     * {@code 
     *  ObservableType<Integer> observable = Observables.<Integer>monadPlus()
                                      .plus(ObservableType.widen(Observable.of()), ObservableType.widen(Observable.of(10)))
                                      .convert(ObservableType::narrowK);
        //Observable.of(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Observables by concatenation
     */
    public static <T> MonadPlus<ObservableType.µ,T> monadPlus(){
        Monoid<ObservableType<T>> m = Monoid.of(ObservableType.widen(Observable.<T>empty()), ObservableInstances::concat);
        Monoid<Higher<ObservableType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<ObservableType<Integer>> m = Monoid.of(ObservableType.widen(Observable.of()), (a,b)->a.isEmpty() ? b : a);
        ObservableType<Integer> observable = Observables.<Integer>monadPlus(m)
                                      .plus(ObservableType.widen(Observable.of(5)), ObservableType.widen(Observable.of(10)))
                                      .convert(ObservableType::narrowK);
        //Observable.of(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Observables
     * @return Type class for combining Observables
     */
    public static <T> MonadPlus<ObservableType.µ,T> monadPlus(Monoid<ObservableType<T>> m){
        Monoid<Higher<ObservableType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<ObservableType.µ> traverse(){
        BiFunction<Applicative<C2>,ObservableType<Higher<C2, T>>,Higher<C2, ObservableType<T>>> sequenceFn = (ap,observable) -> {
        
            Higher<C2,ObservableType<T>> identity = ap.unit(ObservableType.widen(Observable.empty()));

            BiFunction<Higher<C2,ObservableType<T>>,Higher<C2,T>,Higher<C2,ObservableType<T>>> combineToObservable =   (acc,next) -> ap.apBiFn(ap.unit((a,b) -> ObservableType.widen(Observable.concat(ObservableType.narrow(a),Observable.just(b)))),acc,next);

            BinaryOperator<Higher<C2,ObservableType<T>>> combineObservables = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> { return ObservableType.widen(Observable.concat(l1.narrow(),l2.narrow()));}),a,b); ;  

            return ReactiveSeq.fromPublisher(observable).reduce(identity,
                                                            combineToObservable,
                                                            combineObservables);  

   
        };
        BiFunction<Applicative<C2>,Higher<ObservableType.µ,Higher<C2, T>>,Higher<C2, Higher<ObservableType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> ObservableType.widen2(sequenceFn.apply(a, ObservableType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Observables.foldable()
                        .foldLeft(0, (a,b)->a+b, ObservableType.widen(Observable.of(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<ObservableType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<ObservableType.µ,T>,T> foldRightFn =  (m,l)-> ReactiveSeq.fromPublisher(ObservableType.narrowK(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<ObservableType.µ,T>,T> foldLeftFn = (m,l)-> ReactiveSeq.fromPublisher(ObservableType.narrowK(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> ObservableType<T> concat(ObservableType<T> l1, ObservableType<T> l2){
        return ObservableType.widen(Observable.concat(l1.narrow(),l2.narrow()));
    }
    private <T> ObservableType<T> of(T value){
        return ObservableType.widen(Observable.just(value));
    }
    private static <T,R> ObservableType<R> ap(ObservableType<Function< T, R>> lt,  ObservableType<T> observable){
       return ObservableType.widen(lt.zipWith(observable.narrow(),(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<ObservableType.µ,R> flatMap( Higher<ObservableType.µ,T> lt, Function<? super T, ? extends  Higher<ObservableType.µ,R>> fn){
        Func1<? super T, ? extends  Observable<R>> f =t->fn.andThen(ObservableType::narrow).apply(t);
        
        return ObservableType.widen(ObservableType.narrowK(lt)
                                        .flatMap(f));
    }
    private static <T,R> ObservableType<R> map(ObservableType<T> lt, Function<? super T, ? extends R> fn){
        return ObservableType.widen(lt.map(in->fn.apply(in)));
    }
    private static <T> ObservableType<T> filter(Higher<ObservableType.µ,T> lt, Predicate<? super T> fn){
        return ObservableType.widen(ObservableType.narrow(lt).filter(in->fn.test(in)));
    }
}
