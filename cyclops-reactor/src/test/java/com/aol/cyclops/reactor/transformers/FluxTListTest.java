package com.aol.cyclops.reactor.transformers;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.reactor.FluxTs;
import com.aol.cyclops.reactor.transformer.FluxT;
import com.aol.cyclops.types.anyM.AnyMSeq;

import reactor.core.publisher.Flux;

public class FluxTListTest extends AbstractAnyMSeqOrderedDependentTest {

    @Override
    public <T> AnyMSeq<T> of(T... values) {
        return FluxTs.anyM(FluxT.fromIterable(ListX.of(Flux.just(values))));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.functions.collections.extensions.AbstractCollectionXTest#
     * empty()
     */
    @Override
    public <T> AnyMSeq<T> empty() {
        return AnyM.fromIterable(FluxT.emptyFlux());
    }

}
