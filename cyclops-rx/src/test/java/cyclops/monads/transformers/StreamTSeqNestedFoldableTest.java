package cyclops.monads.transformers;


import com.oath.cyclops.types.anyM.transformers.FoldableTransformerSeq;
import cyclops.companion.rx.Observables;
import cyclops.monads.Witness;


public class StreamTSeqNestedFoldableTest extends AbstractNestedFoldableTest<Witness.list> {

    @Override
    public <T> FoldableTransformerSeq<Witness.list,T> of(T... elements) {
        return  Observables.just(elements).liftM(Witness.list.INSTANCE);
    }

    @Override
    public <T> FoldableTransformerSeq<Witness.list,T> empty() {
        return  Observables.<T>empty().liftM(Witness.list.INSTANCE);
    }

}
