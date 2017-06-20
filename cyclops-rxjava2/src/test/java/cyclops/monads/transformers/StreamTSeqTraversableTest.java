package cyclops.monads.transformers;

import com.aol.cyclops2.types.traversable.Traversable;
import cyclops.collections.mutable.ListX;
import cyclops.companion.rx2.Observables;
import cyclops.monads.Witness;
import cyclops.monads.Witness.list;
import io.reactivex.Observable;
import org.junit.Test;



public class StreamTSeqTraversableTest extends AbstractTraversableTest {

    @Override
    public <T> Traversable<T> of(T... elements) {
        return Observables.of(elements).liftM(Witness.reactiveSeq.CO_REACTIVE);
    }

    @Override
    public <T> Traversable<T> empty() {

        return Observables.<T>empty().liftM(Witness.reactiveSeq.CO_REACTIVE);
    }

    @Test
    public void conversion(){
        StreamT<list,Integer> trans = Observables.just(1,2,3).liftM(list.INSTANCE);

        ListX<Observable<Integer>> listObs = Witness.list(trans.unwrapTo(Observables::fromStream));

    }

}
