package cyclops.monads.transformers.observables;


import com.aol.cyclops2.types.foldable.ConvertableSequence;
import cyclops.companion.rx2.Observables;
import cyclops.monads.Witness;
import cyclops.monads.transformers.AbstractConvertableSequenceTest;


public class StreamTSeqConvertableSequenceTest extends AbstractConvertableSequenceTest {

    @Override
    public <T> ConvertableSequence<T> of(T... elements) {

        return Observables.of(elements).liftM(Witness.list.INSTANCE).to();
    }

    @Override
    public <T> ConvertableSequence<T> empty() {

        return Observables.<T>empty().liftM(Witness.list.INSTANCE).to();
    }

}
