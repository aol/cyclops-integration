package cyclops.streams.flowables.asyncreactivestreams;


import cyclops.companion.rx2.Flowables;

import cyclops.streams.flowables.CollectableTest;
import org.jooq.lambda.Collectable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ForkJoinPool;

public class AsyncRSCollectableTest extends CollectableTest {


    public <T> Collectable<T> of(T... values){

        return Flowables.reactiveSeq(Flux.just(values)
                .subscribeOn(Schedulers.fromExecutor(ForkJoinPool.commonPool())))
                .collectors();
    }

}
