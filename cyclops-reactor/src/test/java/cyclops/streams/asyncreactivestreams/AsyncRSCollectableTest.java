package cyclops.streams.asyncreactivestreams;

import cyclops.companion.reactor.Fluxs;
import cyclops.streams.CollectableTest;
import org.jooq.lambda.Collectable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ForkJoinPool;

public class AsyncRSCollectableTest extends CollectableTest {


    public <T> Collectable<T> of(T... values){

        return Fluxs.reactiveSeq(Flux.just(values)
                .subscribeOn(Schedulers.fromExecutor(ForkJoinPool.commonPool())))
                .collectors();
    }

}
