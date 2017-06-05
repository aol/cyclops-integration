package cyclops.monads.transformers;

import com.aol.cyclops2.types.traversable.Traversable;
import cyclops.companion.reactor.Fluxs;
import cyclops.monads.Witness;
import cyclops.stream.ReactiveSeq;


public class StreamTSeqTraversableTest extends AbstractTraversableTest {

    @Override
    public <T> Traversable<T> of(T... elements) {
        return Fluxs.of(elements).liftM(Witness.reactiveSeq.CO_REACTIVE);
    }

    @Override
    public <T> Traversable<T> empty() {

        return Fluxs.<T>empty().liftM(Witness.reactiveSeq.CO_REACTIVE);
    }

}
