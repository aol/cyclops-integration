package com.aol.cyclops.reactor.collections.extensions.base;

import org.jooq.lambda.tuple.Tuple2;
import org.pcollections.PMap;

import com.aol.cyclops.Reducer;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.persistent.PMapX;
import com.aol.cyclops.data.collections.extensions.persistent.PMapXImpl;

public class ExtensiblePMapX<K,V> extends PMapXImpl<K,V> {
    
    private final Reducer<PMapX<K, V>>  reducer;
    
    public ExtensiblePMapX(PMap<K, V> map,Reducer<PMapX<K, V>>  reducer) {
        super(
              map);
        this.reducer = reducer;
     
    }
    
    public PMapX<K, V> fromStream(final ReactiveSeq<Tuple2<K, V>> stream) {
        return stream.mapReduce(reducer);
    }

    /* (non-Javadoc)
     * @see com.aol.cyclops.data.collections.extensions.persistent.PMapXImpl#withMap(org.pcollections.PMap)
     */
    @Override
    public PMapXImpl<K, V> withMap(PMap<K, V> map) {
        return super.withMap(map);
    }
    
}
