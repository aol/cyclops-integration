package cyclops;

import cyclops.collections.mutable.ListX;
import cyclops.control.Eval;
import cyclops.control.Reader;
import cyclops.control.Xor;
import cyclops.control.lazy.Either;
import cyclops.function.*;
import cyclops.monads.Witness;
import cyclops.monads.Witness.supplier;
import cyclops.stream.ReactiveSeq;
import cyclops.typeclasses.free.Free;

import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cyclops.function.Lambda.λK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by johnmcclean on 25/07/2017.
 */
public class FunctionTest {

    @Test
    public void memoization(){

        Fn1<Long,Long> maybeExpensive = i-> ReactiveSeq.rangeLong(0,i)
                                                         .foldLeft(0l,(a,b)->a+b);
        Fn1<Long,Long> caching = maybeExpensive.memoize();

        //calculate and cache
        caching.apply(100000000l);
        //4999999950000000

        //retrieve from cache
        caching.apply(100000000l);
        //4999999950000000
    }
    @Test
    public void easy(){
        Function<Integer,Integer> add1 = a->a+1;

        class m{
             Integer addTen(Integer a){
                return a+10;
            }
        }
        Function<Integer,Integer> add10 = new m()::addTen;

        add1.apply(10);
        //11

        add10.apply(10);
        //20

        Function<Integer,Integer> add5 = add1.andThen(add1)
                                             .andThen(add1)
                                             .andThen(add1)
                                             .andThen(add1);

        Fn1<Integer,Integer> add6 = a->add5.apply(a)+1;
        add6.apply(2);
        //8

    }

    public Integer mult10(Integer in){
        return in*10;
    }
    @Test
    public void generate(){
        Fn1<Integer,Integer> add10 = a->a+10;

        add10.fanIn((String b)->b.length())
                .apply(Either.left(10));
        //20
        
      add10.fanIn((String b)->b.length())
                .apply(Either.right("ten"));
        //3

        Fn1<Integer, Tuple2<Integer, Integer>> x = add10.product(this::mult10);
        x.apply(10);
        //[20,100]

        Fn1<Xor<Integer, String>, Xor<Integer, String>> r = add10.leftFn();
        r.apply(Either.left(10));
        //Secondary[20]

    }

    @Test
    public void curry(){

        Fn2<Integer,Integer,Integer> add = (a, b)->a+b;
        add.apply(10,20);
        //30

        Fn1<? super Integer, Fn1<? super Integer, ? extends Integer>> curried = add.curry();
        curried.apply(10).apply(20);
        //30

        Fn1<Integer, Fn1<Integer, Integer>> curried2 = Curry.curry2(add);
        curried2.apply(10).apply(20);
        //30


        Fn1<Integer,Integer> add1 = add.apply(1);

        Reader<Integer, String> reader = add1.reader()
                                             .map(i -> "hello " + i);

        reader.apply(10);
        //hello 11

        Reader<Integer, String> r = reader.flatMap(i -> a -> a + i);
        r.apply(10);
        //10hello 11

        ListX<String> list =  reader.functionOps()
                                    .mapF(ListX.of(1,2,3,4));
        //[hello 2, hello 3, hello 4, hello 5]


    }


    public  <T> Fn2<T,Fn1<T,T>,T> applyTwice(){
            return  (a,b) -> b.apply(b.apply(a));
    }
    public static <T> T twice(T a,Fn1<T,T> fn){
        return   fn.apply(fn.apply(a));
    }

    @Test
    public  void higherOrder(){

        this.<Integer>applyTwice().apply(10,(Integer a)->a+3);

        twice(10,a->a+3);
        //16

        System.out.println(twice("HEY",a->a+ " HAHA"));
        //HEY HAHA HAHA

    }
    @Test
    public void compose(){
        Fn1<Integer,String> fn1 = a->"hello " +a;
        fn1.apply(10);
        //hello 10

        Fn1<Integer,Integer> after = fn1.andThen(a->a.length()*2);
        after.apply(10);
        //16

        Fn1<String,Integer> before = after.compose(d->d.length());
        before.apply("ten");
        //14


        BinaryFn<Stream<Integer>> fn = Stream::concat;

        fn.curry()
                .compose((Integer a) -> ReactiveSeq.range(0, a))
                .apply(5)
                .apply(Stream.of(10,20,30))
                .collect(Collectors.toList());
        //[0, 1, 2, 3, 4, 10, 20, 30]



    }


    @Test
    public void zip(){
        Fn2<Integer,Integer,String> fn2 = (a,b)->"hello " +a * b;

        fn2.fnOps()
           .listXZip().apply(ListX.of(1,2,3),ListX.of(10,20,30));
        //[hello 10, hello 40, hello 90]
    }

    @Test
    public void fn0(){
        Fn0<Integer> s = ()->10;
        Fn0<String> mapped = Eval.later(s)
                                 .map(i->"hello " +i);

        Fn0<Integer> flatMapped = Eval.later(s)
                                     .flatMap(i->Eval.later(()->i+2));


    }
    @Test
    public void partial(){

        PartialApplicator.partial2b(10,(String a,Integer b)-> a+" " + b)
                         .apply("hello");
        //hello 10
    }

    private static Free<supplier, Long> fibonacci(long i){
        return fibonacci(i,1,0);
    }

    private static Free<supplier, Long> fibonacci(long n, long a, long b) {
        return n == 0 ? Free.done(b) : λK( ()->fibonacci(n-1, a+b, a))
                .kindTo(Fn0::suspend)
                .flatMap(i->λK( ()->fibonacci(n-1, a+b, a))
                        .kindTo(Fn0::suspend));
    }
    @Test
    public void testFib(){

        long time = System.currentTimeMillis();
        assertThat(1597l,equalTo(Fn0.run(fibonacci(17L))));
        System.out.println("Taken "  +(System.currentTimeMillis()-time));
    }
}
