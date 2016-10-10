package com.aol.cyclops.reactor.collections.extensions.base;

import java.util.Collection;

import com.aol.cyclops.data.collections.extensions.FluentCollectionX;

import reactor.core.publisher.Flux;

/**
 * A Lazy Collection with a fluent api. Extended operators act eagerly, direct operations on a collection
 * to add, remove or retrieve elements should be eager unless otherwise stated.
 * 
 * 
 * @author johnmcclean
 *
 * @param <T> the type of elements held in this collection
 */
public interface LazyFluentCollectionX<T> extends FluentCollectionX<T> {
    
    /**
     * Create a LazyFluentCollection from a Flux. 
     * The created LazyFluentCollection will be of the same type as the object this method is called on.
     * i.e. Calling stream(Flux) on a LazyListX results in a LazyListX
     * 
     * 
     * <pre>
     * {@code 
     *     
     *     LazyListX<Integer> lazyInts = LazyListX.of(1,2,3);
     *     LazyListX<String> lazyStrs = lazyInts.stream(Flux.just("hello","world"));
     * 
     * }
     * </pre>
     * Calling stream(Flux) on a LazySetX results in a LazySetX etc.
     * 
     * The same collection / reduction method will be used in the newly created Object. I.e. Calling  stream(Flux) on 
     * a collection which as an Immutable Collector  will result in an Immutable Collection.
     * 

     * 
     * @param stream Flux to create new collection from
     * @return New collection from Flux
     */
    <X> FluentCollectionX<X> stream(Flux<X> stream);
    
    /**
     * Lazily add an element to this Collection.
     * The Collection will not be materialized (unlike via @see {@link LazyFluentCollectionX#plus(Object)}
     * <pre>
     * {@code 
     *    LazyListX<Integer> lazy = LazyListX.of(1,2,3)
     *                                       .map(i->i*2)
     *                                       .plusLazy(5);
     *                                       
     *   //Lazy List that will contain [2,4,6,5] when triggered                                    
     * }
     * </pre>
     * 
     * @param e Element to add
     * @return FluentCollectionX with element added
     */
    default LazyFluentCollectionX<T> plusLazy(T e){
        add(e);
        return this;
    }
    
    default LazyFluentCollectionX<T> plusAllLazy(Collection<? extends T> list){
        addAll(list);
        return this;
    }
    
    default LazyFluentCollectionX<T> minusLazy(Object e){
        remove(e);
        return this;
    }
    
    default LazyFluentCollectionX<T> minusAllLazy(Collection<?> list){
        removeAll(list);
        return this;
    }

    
    default FluentCollectionX<T> plusInOrder(T e){
        return plus(e);
    }
    /* (non-Javadoc)
     * @see com.aol.cyclops.data.collections.extensions.FluentCollectionX#plus(java.lang.Object)
     */
    public FluentCollectionX<T> plus(T e);
    
    /* (non-Javadoc)
     * @see com.aol.cyclops.data.collections.extensions.FluentCollectionX#plusAll(java.util.Collection)
     */
    public FluentCollectionX<T> plusAll(Collection<? extends T> list) ;
    
    /* (non-Javadoc)
     * @see com.aol.cyclops.data.collections.extensions.FluentCollectionX#minus(java.lang.Object)
     */
    public FluentCollectionX<T> minus(Object e);
    
    /* (non-Javadoc)
     * @see com.aol.cyclops.data.collections.extensions.FluentCollectionX#minusAll(java.util.Collection)
     */
    public FluentCollectionX<T> minusAll(Collection<?> list);
    
    /* (non-Javadoc)
     * @see com.aol.cyclops.data.collections.extensions.FluentCollectionX#unit(java.util.Collection)
     */
    public <R> FluentCollectionX<R> unit(Collection<R> col);
}

