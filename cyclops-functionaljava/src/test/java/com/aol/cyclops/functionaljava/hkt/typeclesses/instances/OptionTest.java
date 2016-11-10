package com.aol.cyclops.functionaljava.hkt.typeclesses.instances;
import static com.aol.cyclops.functionaljava.hkt.OptionType.widen;
import static com.aol.cyclops.util.function.Lambda.l1;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.functionaljava.hkt.OptionType;
import com.aol.cyclops.functionaljava.hkt.typeclassess.instances.Options;
import com.aol.cyclops.util.function.Lambda;

import fj.data.Option;

public class OptionTest {

    @Test
    public void unit(){
        
        OptionType<String> opt = Options.unit()
                                            .unit("hello")
                                            .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello")));
    }
    @Test
    public void functor(){
        
        OptionType<Integer> opt = Options.unit()
                                     .unit("hello")
                                     .then(h->Options.functor().map((String v) ->v.length(), h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello".length())));
    }
    @Test
    public void apSimple(){
        Options.applicative()
            .ap(widen(Option.some(l1(this::multiplyByTwo))),widen(Option.some(1)));
    }
    private int multiplyByTwo(int x){
        return x*2;
    }
    @Test
    public void applicative(){
        
        OptionType<Function<Integer,Integer>> optFn =Options.unit().unit(Lambda.l1((Integer i) ->i*2)).convert(OptionType::narrowK);
        
        OptionType<Integer> opt = Options.unit()
                                     .unit("hello")
                                     .then(h->Options.functor().map((String v) ->v.length(), h))
                                     .then(h->Options.applicative().ap(optFn, h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello".length()*2)));
    }
    @Test
    public void monadSimple(){
       OptionType<Integer> opt  = Options.monad()
                                            .<Integer,Integer>flatMap(i->widen(Option.some(i*2)), widen(Option.some(3)))
                                            .convert(OptionType::narrowK);
    }
    @Test
    public void monad(){
        
        OptionType<Integer> opt = Options.unit()
                                     .unit("hello")
                                     .then(h->Options.monad().flatMap((String v) ->Options.unit().unit(v.length()), h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello".length())));
    }
    @Test
    public void monadZeroFilter(){
        
        OptionType<String> opt = Options.unit()
                                     .unit("hello")
                                     .then(h->Options.monadZero().filter((String t)->t.startsWith("he"), h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.some("hello")));
    }
    @Test
    public void monadZeroFilterOut(){
        
        OptionType<String> opt = Options.unit()
                                     .unit("hello")
                                     .then(h->Options.monadZero().filter((String t)->!t.startsWith("he"), h))
                                     .convert(OptionType::narrowK);
        
        assertThat(opt,equalTo(Option.none()));
    }
    
    @Test
    public void monadPlus(){
        OptionType<Integer> opt = Options.<Integer>monadPlus()
                                      .plus(OptionType.widen(Option.none()), OptionType.widen(Option.some(10)))
                                      .convert(OptionType::narrowK);
        assertThat(opt,equalTo(Option.some(10)));
    }
    @Test
    public void monadPlusNonEmpty(){
        
        Monoid<OptionType<Integer>> m = Monoid.of(OptionType.widen(Option.none()), (a,b)->a.isSome() ? b : a);
        OptionType<Integer> opt = Options.<Integer>monadPlus(m)
                                      .plus(OptionType.widen(Option.some(5)), OptionType.widen(Option.some(10)))
                                      .convert(OptionType::narrowK);
        assertThat(opt,equalTo(Option.some(10)));
    }
    @Test
    public void  foldLeft(){
        int sum  = Options.foldable()
                        .foldLeft(0, (a,b)->a+b, OptionType.widen(Option.some(4)));
        
        assertThat(sum,equalTo(4));
    }
    @Test
    public void  foldRight(){
        int sum  = Options.foldable()
                        .foldRight(0, (a,b)->a+b, OptionType.widen(Option.some(1)));
        
        assertThat(sum,equalTo(1));
    }
    
}
