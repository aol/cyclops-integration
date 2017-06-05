package cyclops.streams.syncflux;


import cyclops.companion.reactor.Fluxs;
import cyclops.streams.CollectableTest;
import org.jooq.lambda.Collectable;
import reactor.core.publisher.Flux;

public class SyncRSCollectableTest extends CollectableTest {


    public <T> Collectable<T> of(T... values){
        return Fluxs.seq(Flux.just(values)).collectors();
    }

}
