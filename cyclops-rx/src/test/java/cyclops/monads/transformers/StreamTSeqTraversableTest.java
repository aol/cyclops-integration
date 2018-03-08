package cyclops.monads.transformers;

import com.oath.cyclops.types.traversable.Traversable;

import cyclops.companion.rx.Observables;
import cyclops.monads.AnyM;
import cyclops.monads.AnyMs;
import cyclops.monads.Witness;
import cyclops.monads.Witness.list;
import cyclops.reactive.collections.mutable.ListX;
import org.junit.Test;
import rx.Observable;


import java.util.stream.Stream;


public class StreamTSeqTraversableTest extends AbstractTraversableTest {

    @Override
    public <T> Traversable<T> of(T... elements) {
       return AnyMs.liftM(Observables.of(elements),Witness.reactiveSeq.CO_REACTIVE);

    }

    @Override
    public <T> Traversable<T> empty() {
      return AnyMs.liftM(Observables.empty(),Witness.reactiveSeq.CO_REACTIVE);
    }

    @Test
    public void conversion(){
        StreamT<list,Integer> trans =  AnyMs.liftM(Observables.just(1,2,3), list.INSTANCE);

        ListX<Observable<Integer>> listObs = Witness.list(trans.unwrapTo(Observables::fromStream));

    }

}
