package cyclops.monads.transformers;

import com.aol.cyclops2.types.traversable.Traversable;
import cyclops.companion.rx.Observables;
import cyclops.monads.Witness;


public class StreamTSeqTraversableTest extends AbstractTraversableTest {

    @Override
    public <T> Traversable<T> of(T... elements) {
        return Observables.of(elements).liftM(Witness.reactiveSeq.CO_REACTIVE);
    }

    @Override
    public <T> Traversable<T> empty() {

        return Observables.<T>empty().liftM(Witness.reactiveSeq.CO_REACTIVE);
    }

}
