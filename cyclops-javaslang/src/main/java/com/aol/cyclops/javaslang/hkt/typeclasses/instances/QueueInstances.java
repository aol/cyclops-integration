package com.aol.cyclops.javaslang.hkt.typeclasses.instances;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

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
import com.aol.cyclops.javaslang.FromCyclopsReact;
import com.aol.cyclops.javaslang.hkt.QueueType;

import javaslang.collection.Queue;
import lombok.experimental.UtilityClass;

/**
 * Companion class for creating Type Class instances for working with Queues
 * @author johnmcclean
 *
 */
@UtilityClass
public class QueueInstances {

    public static void main(String[] args){
        Queue<Integer> small = Queue.of(1,2,3);
        QueueType<Integer> list = QueueInstances.functor()
                                     .map(i->i*2, QueueType.widen(small))
                                    // .then_(functor()::map, Lambda.<Integer,Integer>l1(i->i*3))
                                     .then(h-> functor().map((Integer i)->""+i,h))
                                     .then(h-> monad().flatMap(s->QueueType.widen(Queue.of(1)), h))
                                     .convert(QueueType::narrowK);
          QueueType<Integer> string = list.convert(QueueType::narrowK);
                    
        System.out.println(QueueInstances.functor().map(i->i*2, QueueType.widen(small)));
    }
    /**
     * 
     * Transform a list, mulitplying every element by 2
     * 
     * <pre>
     * {@code 
     *  QueueType<Integer> list = Queues.functor().map(i->i*2, QueueType.widen(Arrays.asQueue(1,2,3));
     *  
     *  //[2,4,6]
     *  
     * 
     * }
     * </pre>
     * 
     * An example fluent api working with Queues
     * <pre>
     * {@code 
     *   QueueType<Integer> list = Queues.unit()
                                       .unit("hello")
                                       .then(h->Queues.functor().map((String v) ->v.length(), h))
                                       .convert(QueueType::narrowK);
     * 
     * }
     * </pre>
     * 
     * 
     * @return A functor for Queues
     */
    public static <T,R>Functor<QueueType.µ> functor(){
        BiFunction<QueueType<T>,Function<? super T, ? extends R>,QueueType<R>> map = QueueInstances::map;
        return General.functor(map);
    }
    /**
     * <pre>
     * {@code 
     * QueueType<String> list = Queues.unit()
                                     .unit("hello")
                                     .convert(QueueType::narrowK);
        
        //Arrays.asQueue("hello"))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A factory for Queues
     */
    public static Unit<QueueType.µ> unit(){
        return General.unit(QueueType::of);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.QueueType.widen;
     * import static com.aol.cyclops.util.function.Lambda.l1;
     * import static java.util.Arrays.asQueue;
     * 
       Queues.zippingApplicative()
            .ap(widen(asQueue(l1(this::multiplyByTwo))),widen(asQueue(1,2,3)));
     * 
     * //[2,4,6]
     * }
     * </pre>
     * 
     * 
     * Example fluent API
     * <pre>
     * {@code 
     * QueueType<Function<Integer,Integer>> listFn =Queues.unit()
     *                                                  .unit(Lambda.l1((Integer i) ->i*2))
     *                                                  .convert(QueueType::narrowK);
        
        QueueType<Integer> list = Queues.unit()
                                      .unit("hello")
                                      .then(h->Queues.functor().map((String v) ->v.length(), h))
                                      .then(h->Queues.zippingApplicative().ap(listFn, h))
                                      .convert(QueueType::narrowK);
        
        //Arrays.asQueue("hello".length()*2))
     * 
     * }
     * </pre>
     * 
     * 
     * @return A zipper for Queues
     */
    public static <T,R> Applicative<QueueType.µ> zippingApplicative(){
        BiFunction<QueueType< Function<T, R>>,QueueType<T>,QueueType<R>> ap = QueueInstances::ap;
        return General.applicative(functor(), unit(), ap);
    }
    /**
     * 
     * <pre>
     * {@code 
     * import static com.aol.cyclops.hkt.jdk.QueueType.widen;
     * QueueType<Integer> list  = Queues.monad()
                                      .flatMap(i->widen(QueueX.range(0,i)), widen(Arrays.asQueue(1,2,3)))
                                      .convert(QueueType::narrowK);
     * }
     * </pre>
     * 
     * Example fluent API
     * <pre>
     * {@code 
     *    QueueType<Integer> list = Queues.unit()
                                        .unit("hello")
                                        .then(h->Queues.monad().flatMap((String v) ->Queues.unit().unit(v.length()), h))
                                        .convert(QueueType::narrowK);
        
        //Arrays.asQueue("hello".length())
     * 
     * }
     * </pre>
     * 
     * @return Type class with monad functions for Queues
     */
    public static <T,R> Monad<QueueType.µ> monad(){
  
        BiFunction<Higher<QueueType.µ,T>,Function<? super T, ? extends Higher<QueueType.µ,R>>,Higher<QueueType.µ,R>> flatMap = QueueInstances::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  QueueType<String> list = Queues.unit()
                                     .unit("hello")
                                     .then(h->Queues.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(QueueType::narrowK);
        
       //Arrays.asQueue("hello"));
     * 
     * }
     * </pre>
     * 
     * 
     * @return A filterable monad (with default value)
     */
    public static <T,R> MonadZero<QueueType.µ> monadZero(){
        
        return General.monadZero(monad(), QueueType.widen(Queue.empty()));
    }
    /**
     * <pre>
     * {@code 
     *  QueueType<Integer> list = Queues.<Integer>monadPlus()
                                      .plus(QueueType.widen(Arrays.asQueue()), QueueType.widen(Arrays.asQueue(10)))
                                      .convert(QueueType::narrowK);
        //Arrays.asQueue(10))
     * 
     * }
     * </pre>
     * @return Type class for combining Queues by concatenation
     */
    public static <T> MonadPlus<QueueType.µ,T> monadPlus(){
        Monoid<QueueType<T>> m = Monoid.of(QueueType.widen(Queue.empty()), QueueInstances::concat);
        Monoid<Higher<QueueType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    /**
     * 
     * <pre>
     * {@code 
     *  Monoid<QueueType<Integer>> m = Monoid.of(QueueType.widen(Arrays.asQueue()), (a,b)->a.isEmpty() ? b : a);
        QueueType<Integer> list = Queues.<Integer>monadPlus(m)
                                      .plus(QueueType.widen(Arrays.asQueue(5)), QueueType.widen(Arrays.asQueue(10)))
                                      .convert(QueueType::narrowK);
        //Arrays.asQueue(5))
     * 
     * }
     * </pre>
     * 
     * @param m Monoid to use for combining Queues
     * @return Type class for combining Queues
     */
    public static <T> MonadPlus<QueueType.µ,T> monadPlus(Monoid<QueueType<T>> m){
        Monoid<Higher<QueueType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
 
    /**
     * @return Type class for traversables with traverse / sequence operations
     */
    public static <C2,T> Traverse<QueueType.µ> traverse(){
     
        BiFunction<Applicative<C2>,QueueType<Higher<C2, T>>,Higher<C2, QueueType<T>>> sequenceFn = (ap,list) -> {
        
            Higher<C2,QueueType<T>> identity = ap.unit(QueueType.widen(Queue.empty()));

            BiFunction<Higher<C2,QueueType<T>>,Higher<C2,T>,Higher<C2,QueueType<T>>> combineToQueue =   (acc,next) -> ap.apBiFn(ap.unit((a,b) -> QueueType.widen(QueueType.narrow(a).append(b))),
                                                                                                                             acc,next);

            BinaryOperator<Higher<C2,QueueType<T>>> combineQueues = (a,b)-> ap.apBiFn(ap.unit((l1,l2)-> QueueType.widen(QueueType.narrow(l1).appendAll(l2))),a,b); ;  

            return ReactiveSeq.fromIterable(QueueType.narrow(list))
                      .reduce(identity,
                              combineToQueue,
                              combineQueues);  

   
        };
        BiFunction<Applicative<C2>,Higher<QueueType.µ,Higher<C2, T>>,Higher<C2, Higher<QueueType.µ,T>>> sequenceNarrow  = 
                                                        (a,b) -> QueueType.widen2(sequenceFn.apply(a, QueueType.narrowK(b)));
        return General.traverse(zippingApplicative(), sequenceNarrow);
    }
    
    /**
     * 
     * <pre>
     * {@code 
     * int sum  = Queues.foldable()
                        .foldLeft(0, (a,b)->a+b, QueueType.widen(Arrays.asQueue(1,2,3,4)));
        
        //10
     * 
     * }
     * </pre>
     * 
     * 
     * @return Type class for folding / reduction operations
     */
    public static <T> Foldable<QueueType.µ> foldable(){
        BiFunction<Monoid<T>,Higher<QueueType.µ,T>,T> foldRightFn =  (m,l)-> ReactiveSeq.fromIterable(QueueType.narrow(l)).foldRight(m);
        BiFunction<Monoid<T>,Higher<QueueType.µ,T>,T> foldLeftFn = (m,l)-> ReactiveSeq.fromIterable(QueueType.narrow(l)).reduce(m);
        return General.foldable(foldRightFn, foldLeftFn);
    }
  
    private static  <T> QueueType<T> concat(QueueType<T> l1, QueueType<T> l2){

        return QueueType.widen(l1.appendAll(QueueType.narrow(l2)));
       
    }
    
    private static <T,R> QueueType<R> ap(QueueType<Function< T, R>> lt,  QueueType<T> list){
        return QueueType.widen(FromCyclopsReact.fromStream(ReactiveSeq.fromIterable(lt).zip(list, (a,b)->a.apply(b))).toQueue());
    }
    private static <T,R> Higher<QueueType.µ,R> flatMap( Higher<QueueType.µ,T> lt, Function<? super T, ? extends  Higher<QueueType.µ,R>> fn){
        return QueueType.widen(QueueType.narrow(lt).flatMap(fn.andThen(QueueType::narrow)));
    }
    private static <T,R> QueueType<R> map(QueueType<T> lt, Function<? super T, ? extends R> fn){
        return QueueType.widen(QueueType.narrow(lt).map(in->fn.apply(in)));
    }
}
