package cyclops.monads.transformers;


import com.oath.anym.transformers.FoldableTransformerSeq;
import cyclops.companion.rx.Observables;
import cyclops.monads.AnyMs;
import cyclops.monads.Witness;


public class StreamTSeqNestedFoldableTest extends AbstractNestedFoldableTest<Witness.list> {

    @Override
    public <T> FoldableTransformerSeq<Witness.list,T> of(T... elements) {
        return AnyMs.liftM(Observables.of(elements),Witness.list.INSTANCE);
    }

    @Override
    public <T> FoldableTransformerSeq<Witness.list,T> empty() {
        return  AnyMs.liftM(Observables.<T>empty(),Witness.list.INSTANCE);
    }

}
