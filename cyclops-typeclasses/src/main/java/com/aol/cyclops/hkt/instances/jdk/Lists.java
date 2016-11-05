package com.aol.cyclops.hkt.instances.jdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.hkt.alias.Higher;
import com.aol.cyclops.hkt.instances.General;
import com.aol.cyclops.hkt.jdk.ListType;
import com.aol.cyclops.hkt.typeclasses.Unit;
import com.aol.cyclops.hkt.typeclasses.functor.Functor;
import com.aol.cyclops.hkt.typeclasses.monad.Applicative;
import com.aol.cyclops.hkt.typeclasses.monad.Monad;
import com.aol.cyclops.hkt.typeclasses.monad.MonadPlus;
import com.aol.cyclops.hkt.typeclasses.monad.MonadZero;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Lists {

    public static void main(String[] args){
        List<Integer> small = Arrays.asList(1,2,3);
        ListType<Integer> list = Lists.functor()
                                     .map(i->i*2, ListType.widen(small))
                                    // .then_(functor()::map, Lambda.<Integer,Integer>l1(i->i*3))
                                     .then(h-> functor().map((Integer i)->""+i,h))
                                     .then(h-> monad().flatMap(s->ListType.widen(Arrays.asList(1)), h))
                                     .convert(ListType::narrowK);
          ListType<Integer> string = list.convert(ListType::narrowK);
                    
        System.out.println(Lists.functor().map(i->i*2, ListType.widen(small)));
    }
    public static <T,R>Functor<ListType.µ> functor(){
        BiFunction<ListType<T>,Function<? super T, ? extends R>,ListType<R>> map = Lists::map;
        return General.functor(map);
    }
    public static Unit<ListType.µ> unit(){
        return General.unit(Lists::of);
    }
    public static <T,R> Applicative<ListType.µ> zippingApplicative(){
        BiFunction<ListType< Function<? super T, ? extends R>>,ListType<T>,ListType<R>> ap = Lists::ap;
        return General.applicative(functor(), unit(), ap);
    }
    public static <T,R> Monad<ListType.µ> monad(){
  
        BiFunction<Higher<ListType.µ,T>,Function<? super T, ? extends Higher<ListType.µ,R>>,Higher<ListType.µ,R>> flatMap = Lists::flatMap;
        return General.monad(zippingApplicative(), flatMap);
    }
    public static <T,R> MonadZero<ListType.µ> monadZero(){
        
        return General.monadZero(monad(), ListType.widen(new ArrayList<T>()));
    }
    public static <T> MonadPlus<ListType.µ,T> monadPlus(){
        Monoid<ListType<T>> m = Monoid.of(ListType.widen(new ArrayList<T>()), (l1,l2)->{ l1.addAll(l2); return l1;});
        Monoid<Higher<ListType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
    public static <T> MonadPlus<ListType.µ,T> monadPlus(Monoid<ListType<T>> m){
        Monoid<Higher<ListType.µ,T>> m2= (Monoid)m;
        return General.monadPlus(monadZero(),m2);
    }
        private <T> ListType<T> of(T value){
        return ListType.widen(Arrays.asList(value));
    }
    private static <T,R> ListType<R> ap(ListType<Function<? super T, ? extends R>> lt,  ListType<T> list){
        return ListType.widen(ListX.fromIterable(lt).zip(list,(a,b)->a.apply(b)));
    }
    private static <T,R> Higher<ListType.µ,R> flatMap( Higher<ListType.µ,T> lt, Function<? super T, ? extends  Higher<ListType.µ,R>> fn){
        return ListType.widen(ListX.fromIterable(ListType.narrowK(lt)).flatMap(fn.andThen(ListType::narrowK)));
    }
    private static <T,R> ListType<R> map(ListType<T> lt, Function<? super T, ? extends R> fn){
        return ListType.widen(ListX.fromIterable(lt).map(fn));
    }
}
