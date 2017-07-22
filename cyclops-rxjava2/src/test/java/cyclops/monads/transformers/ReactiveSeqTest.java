package cyclops.monads.transformers;

import com.aol.cyclops2.types.anyM.AnyMSeq;
import com.aol.cyclops2.types.foldable.Evaluation;
import cyclops.collections.immutable.VectorX;
import cyclops.collections.mutable.ListX;
import cyclops.companion.reactor.Fluxs;
import cyclops.companion.rx2.Flowables;
import cyclops.companion.rx2.Observables;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import cyclops.monads.Witness;
import cyclops.monads.Witness.reactiveSeq;
import cyclops.stream.FutureStream;
import cyclops.stream.ReactiveSeq;
import cyclops.stream.Spouts;
import cyclops.typeclasses.Active;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.aol.cyclops2.types.foldable.Evaluation.LAZY;
import static cyclops.collections.immutable.VectorX.vectorX;
import static cyclops.collections.mutable.ListX.listX;
import static reactor.core.scheduler.Schedulers.*;

/**
 * Created by johnmcclean on 10/07/2017.
 */
public class ReactiveSeqTest {

    @Test
    public void stream(){


        Stream<Integer> stream = ReactiveSeq.of(1,2,3);
        stream.map(i->i*100)
                .filter(i->i<250)
                .forEach(System.out::println);



    }

    @Test
    public void equivalent(){


        Arrays.asList(1,2,3,4,5,6)
                .stream()
                .map(i->i*2)
                .collect(Collectors.toList());


        ListX.of(1,2,3,4,5,6)
                .map(i->i*2);




    }
    /**
    @Test
    public void grouping(){


        Arrays.asList(1,2,3,4,5,6)
                .stream()
                .map(i->i*2)
                .grouped(2)
                .collect(Collectors.toList());


        ListX.of(1,2,3,4,5,6)
                .map(i->i*2)
                .grouped(2);




    }
    long maxIndex = 10;
    @Test
    public void zipping(){



        Arrays.asList("dog","cat","budgie")
                .stream()
                .zipWithIndex()
                .takeWhile(t->t.v2<maxIndex)
                .collect(Collectors.toList());


        ListX.of("dog","cat","budgie")
                .zipWithIndex()
                .takeWhile(t->t.v2<maxIndex);




    }
     **/
    @Test
    public void spouts(){


        Executor ex = Executors.newFixedThreadPool(1);
        Stream<Integer> stream = Spouts.async(Stream.of(1,2,3),ex);
        stream.map(i->i+2)
                .peek(i->System.out.println("Thread "
                                    + Thread.currentThread().getId()))
                .forEach(System.out::println);



    }

    @Test
    public void observables(){


        Stream<Long> stream = Observables.interval(1, TimeUnit.SECONDS);

        stream.map(s->"Seconds " + s)
                .peek(i->System.out.println("Thread "
                        + Thread.currentThread().getId()))
              .forEach(System.out::println);



    }
    @Test
    public void replayableJava(){


        Stream<Integer> stream = Stream.of(1,2,3);

        stream.map(i->i*100)
                .filter(i->i<250)
                .forEach(System.out::println);

        stream.map(i->i*2)
                .filter(i->i>2)
                .forEach(System.out::println);



    }
    @Test
    public void replayable(){


        Stream<Integer> stream = ReactiveSeq.of(1,2,3);

        stream.map(i->i*100)
                .filter(i->i<250)
                .forEach(System.out::println);

        stream.map(i->i*2)
                .filter(i->i>2)
                .forEach(System.out::println);



    }
    Queue<String> queue;
    public String nextFromQueue(){
        return queue.poll();
    }

    class Listener  {
        public void register(Consumer<String> c){
            Thread t= new Thread(()->{
                Stream.iterate(0,a->a+1).forEach(n->c.accept(""+n));
            });
            t.start();
        }
    }
    public String transform(String in){
        return "transformed " + in;
    }
    @Test
    public void concurrent2() throws InterruptedException {



        Flux<Integer> fast = Flux.range(0,Integer.MAX_VALUE)
                                 .subscribeOn(fromExecutor(ForkJoinPool.commonPool()));


        Flux<String> slow = Flux.just("hello","world")
                                .repeat()
                                .delayElements(Duration.ofMillis(1))
                                .subscribeOn(fromExecutor(ForkJoinPool.commonPool()));

        ListX<Tuple2<Integer, String>> async = listX(Fluxs.reactiveSeq(fast)).zipP(slow);


        ListX<String> transformed =  async.filter(t->t.v1%10==0)
                                          .map(t->transform(t.v2))
                                          .peek(i-> System.out.println("Thread " + Thread.currentThread().getId() + " value " + i))
                                          .limit(10);


        System.out.println("Transformed [2] = " + transformed.get(2));


    }
    @Test
    public void concurrent() throws InterruptedException {



        Flux<Integer> fast = Flux.range(0,Integer.MAX_VALUE)
                                 .subscribeOn(fromExecutor(ForkJoinPool.commonPool()));


        Flux<String> slow = Flux.just("hello","world")
                                .repeat()
                                .delayElements(Duration.ofMillis(1))
                                .subscribeOn(fromExecutor(ForkJoinPool.commonPool()));

        ListX<Tuple2<Integer, String>> async = listX(Fluxs.reactiveSeq(fast)).zipP(slow);


        ListX<String> transformed =  async.filter(t->t.v1%10==0)
                                          .map(t->transform(t.v2))
                                          .limit(10);
        System.out.println("Blocked ? ");

        transformed.stream()
                   .forEachAsync(i->{
            System.out.println("Thread " + Thread.currentThread().getId() + " value " + i);

        });

        System.out.println("Nope");


        System.out.println("Transformed = " + transformed);


    }
    public String process(long t){
        return "data "+ t;
    }
    @Test
    public void async() throws InterruptedException {




        ListX<String> async = Observables.interval(1, TimeUnit.SECONDS)
                                         .map(this::process)
                                         .take(10)
                                         .to().listX(LAZY)
                                         .peek(i-> System.out.println("Thread " + Thread.currentThread().getId() + " value " + i));




        System.out.println("3rd value "  +async.get(3));







    }
    @Test
    public void examples(){

        Stream<Integer> stream = Stream.of(1,2,3);
        Stream<Integer> sequential = ReactiveSeq.of(1,2,3);
        Stream<Integer> asynchronous = Spouts.async(stream, Executors.newFixedThreadPool(1));




        //Multiple Stream implementations...
        ReactiveSeq<Integer> synchronous                 = ReactiveSeq.of(1,2,3);
        ReactiveSeq<Integer> concurrentAsync             = FutureStream.builder()
                                                                       .withExecutor(Executors.newFixedThreadPool(4))
                                                                       .of(1,2,3);
        ReactiveSeq<Long> rxObservable                   = Observables.interval(1, TimeUnit.SECONDS);
        ReactiveSeq<Integer> asynchonousPush             = Spouts.async(Stream.of(1,2,3), Executors.newFixedThreadPool(1));
        ReactiveSeq<Integer> reactiveStreamsCyclopsReact = Spouts.reactive(Stream.of(1,2,3),Executors.newFixedThreadPool(1));

        ReactiveSeq<Integer> reactiveStreamsRxJava       = Flowables.reactiveSeq(Flowable.just(1,2,3).subscribeOn(Schedulers.computation()));

        ReactiveSeq<Integer> reactiveStreamsReactor      = Fluxs.reactiveSeq(Flux.just(1,2,3).subscribeOn(elastic()));


        ListX<Integer> sychronousList                  = listX(synchronous);
        ListX<Integer> concurrentAsyncList             = listX(concurrentAsync);

        ListX<Long> rxObservableList                   = listX(rxObservable);
        ListX<Integer> asynchonousPushList             = listX(asynchonousPush);
        ListX<Integer> reactiveStreamsCyclopsReactList = listX(reactiveStreamsCyclopsReact);

        ListX<Integer> reactiveStreamsRxJavaList       = listX(reactiveStreamsRxJava);

        ListX<Integer> reactiveStreamsReactorList      = listX(reactiveStreamsReactor);



        //but why?
        VectorX<Integer> reactiveCollections = VectorX.vectorX(reactiveStreamsReactor);

        reactiveCollections.filter(i->i<10)
                           .map(i->i*3)
                           .flatMap(i->vectorX(reactiveStreamsRxJava.map(x->i+x)))
                           .cycle(2); //multiple chained commands execute much more performantly with Xtended collection types


        //advanced functional abstractions & generalizations become possible..
        Active<reactiveSeq,Integer> higherKindedAbstraction = reactiveStreamsRxJava.allTypeclasses();
        Active<reactiveSeq, Integer> forComp = higherKindedAbstraction.forEach2(i -> Flowables.range(i, 10), (a, b) -> a + b);

        AnyMSeq<reactiveSeq,Integer> abstractSequence = reactiveStreamsCyclopsReact.anyM();
        AnyMSeq<reactiveSeq, VectorX<Integer>> sliding = abstractSequence.sliding(2);





        asynchonousPush.printOut();


    }
}
