package cyclops.monads.transformers;


import com.aol.cyclops2.types.anyM.transformers.FoldableTransformerSeq;
import cyclops.companion.reactor.Fluxs;
import cyclops.monads.Witness;
import cyclops.stream.ReactiveSeq;


public class StreamTSeqNestedFoldableTest extends AbstractNestedFoldableTest<Witness.list> {

    @Override
    public <T> FoldableTransformerSeq<Witness.list,T> of(T... elements) {
        return  Fluxs.of(elements).liftM(Witness.list.INSTANCE);
    }

    @Override
    public <T> FoldableTransformerSeq<Witness.list,T> empty() {
        return  Fluxs.<T>empty().liftM(Witness.list.INSTANCE);
    }

}
