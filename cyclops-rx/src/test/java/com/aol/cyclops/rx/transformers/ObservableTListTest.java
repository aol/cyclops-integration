package com.aol.cyclops.rx.transformers;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.monads.transformers.seq.StreamTSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.rx.transformer.ObservableT;
import com.aol.cyclops.types.anyM.AnyMSeq;

import rx.Observable;

public class ObservableTListTest extends AbstractAnyMSeqOrderedDependentTest {

    @Override
    public <T> AnyMSeq<T> of(T... values) {
        return AnyM.fromIterable(ObservableT.fromIterable(ListX.of(Observable.from(values))));
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
        return AnyM.fromIterable(StreamTSeq.emptyStream());
    }

}
