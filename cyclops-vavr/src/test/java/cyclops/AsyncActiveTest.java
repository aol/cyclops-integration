package cyclops;

import com.aol.cyclops.vavr.hkt.FutureKind;
import com.aol.cyclops.vavr.hkt.OptionKind;
import com.oath.cyclops.data.collections.extensions.IndexedSequenceX;
import com.oath.cyclops.hkt.Higher;
import com.oath.cyclops.types.anyM.AnyMValue;
import cyclops.collections.immutable.LinkedListX;
import cyclops.collections.immutable.VectorX;
import cyclops.collections.mutable.ListX;
import cyclops.collections.vavr.VavrListX;
import cyclops.collections.vavr.VavrVectorX;
import cyclops.companion.Monoids;
import cyclops.companion.vavr.Futures;
import cyclops.companion.vavr.Trys;
import cyclops.control.Option;
import cyclops.monads.AnyM;
import cyclops.monads.VavrWitness.future;
import cyclops.monads.VavrWitness.tryType;
import cyclops.monads.Witness;
import cyclops.monads.Witness.stream;
import cyclops.monads.WitnessType;
import cyclops.reactive.ReactiveSeq;
import cyclops.typeclasses.Active;
import fj.Monoid;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import cyclops.data.tuple.Tuple;
import cyclops.data.tuple.Tuple2;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cyclops.companion.vavr.Futures.allTypeclasses;
import static cyclops.companion.vavr.Futures.anyM;
import static org.junit.Assert.assertTrue;


public class AsyncActiveTest {

    static interface Work{
        Future<String> load();
        Future<Boolean> save(String data);
    }
    @Test
    public void listT(){
        ListX.range(0, 10)
                .groupedT(2)
               .reduce(0, (a, b) -> a + b);
        //[1, 5, 9, 13, 17]

    }

    @Test
    public void iterate(){

        ListX<Integer> list = ListX.of(1,2,3);
        ListX.range(0,10);
        ListX.fill(10,"hello");
        ListX.listX(ReactiveSeq.of(1,2,3));

        LinkedListX<Integer> l = LinkedListX.of(1,2,3);
        LinkedListX<Integer> lv = VavrListX.of(1,2,3);

        VectorX<Integer> v = VectorX.of(1,2,3);
        VectorX<Integer> vv = VavrVectorX.of(1,2,3);

        IndexedSequenceX<Integer> is = v;



        ListX.unfold(1,i->i<10 ? Option.of(Tuple.tuple(i,i+1)) : Option.none());
        //[1, 2, 3, 4, 5, 6, 7, 8, 9]

        ListX.iterate(Long.MAX_VALUE,1,i->i+1)
                .map(i->i*2);


        ListX.range(1,10)
                .takeWhile(i->i<5).printOut();
        //[1,2,3,4]

    }
    @Test
    public void sliding(){

       ListX.of("hello","world")
                .intersperse(" ")
                .foldLeft(Monoids.stringConcat);

        ListX.range(0,10)
                .sliding(3,2);
        //[[0, 1, 2],[2, 3, 4],[4, 5, 6],[6, 7, 8],[8, 9]]
    }
    @Test
    public void eagerLazy(){



        ListX.range(0,10)
                .grouped(2)
                .map(list->list.foldLeft(0,(a,b)->a+b));
        //[1, 5, 9, 13, 17]



        ListX.fill(3,5);
        //[5,5,5]

        ListX.of(1,2,3)
                .reverse();
        //[3,2,1]

        ListX.of(1,2,3)
                .foldLeft(0,(a,b)->a+b);
        //6

        ListX.of(1,2,3)
                .foldRight(1,(a,b)->a*b);
        //6

        ListX<Tuple2<Integer, Character>> zipped = ListX.of(1, 2, 3)
                                                        .zip(ListX.of('a', 'b', 'c'))
                                                        .materialize();

        ListX<Integer> nums = zipped.map(t2-> t2._1());
        ListX<Character> chars = zipped.map(t2->t2._2());

        ListX.of(1,2,3)
                .peek(System.out::println)
                .map(i->i*2);

        //nothing printed out

        ListX.of(1,2,3)
                .eager()
                .peek(System.out::println)
                .map(i->i*2);
        //prints out
        //1
        //2
        //3
    }

    @Test
    public void addRemove(){
       ListX.of(1,2,3)
            .add(1);
       //[1,2,3,4]

       ListX.of(1,2,3)
               .plus(1);
       //[1,2,3,4]

       ListX.of(1,2,3)
               .remove(1);
       //[2,3]

       ListX.of(1,2,3)
               .removeValue((Object)1);
       //[2,3]
    }
    int times =0;
    @org.junit.Test
    public void cycleTests(){
        System.out.println(ListX.of(1,2,3)
                .cycle(2));

        System.out.println(ListX.of(5,6,7)
                .cycleWhile(i->i*times++<100));
    }
    @Test
    public void headAndTail(){
        ListX.of(1,2,3)
                .headAndTail()
                .head();
        //1

        ListX.of(1,2,3)
                .headAndTail()
                .tail();
        //[2,3]

    }
    @Test
    public void testCode(){

        System.out.println("Run asynchronously..");
        Capitalizer<future> processorAsync = new Capitalizer<>(new AsyncWork());
        assertTrue(processorAsync.process()
                                 .foldLeft(false,(a,b)->a|b));


        System.out.println("Run synchronously..");
        Capitalizer<tryType> processorSync = new Capitalizer<>(new SyncWork());
        assertTrue(processorSync.process()
                                .foldLeft(false,(a,b)->a|b));



    }
    static interface GenericWork<W>{
         Active<W,String> get();
         Active<W,Boolean> save(String data);
    }

    @AllArgsConstructor
    static class Capitalizer<W>{

        GenericWork<W> worker;

        public  Active<W,Boolean> process(){
            return worker.get()
                    .map(String::toUpperCase)
                    .peek(System.out::println)
                    .flatMap(i->worker.save(i).getActive())
                    .peek(System.out::println);
        }
    }

    static class AsyncWork implements GenericWork<future>{

        private ExecutorService ex = Executors.newFixedThreadPool(2);

        @Override
        public  Active<future, String> get(){
            return  Futures.allTypeclasses(Future.ofSupplier(ex, () -> "load data asynchronously"));
        }

        @Override
        public Active<future, Boolean> save(String data){
            return allTypeclasses(Future.ofSupplier(ex,()->true));
        }
    }
    static class SyncWork implements GenericWork<tryType>{

        @Override
        public Active<tryType, String> get() {
                return Trys.allTypeclasses(Try.success("load data synchronously"));
        }

        @Override
        public Active<tryType, Boolean> save(String data) {
            return Trys.allTypeclasses(Try.success(true));
        }
    }

}

