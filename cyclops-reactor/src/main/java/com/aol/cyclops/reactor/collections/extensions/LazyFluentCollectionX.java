package com.aol.cyclops.reactor.collections.extensions;

import java.util.Collection;

import com.aol.cyclops.data.collections.extensions.FluentCollectionX;

import reactor.core.publisher.Flux;

public interface LazyFluentCollectionX<T> extends FluentCollectionX<T> {
    <X> FluentCollectionX<X> stream(Flux<X> stream);
    default FluentCollectionX<T> plusLazy(T e){
        add(e);
        return this;
    }
    
    default FluentCollectionX<T> plusAllLazy(Collection<? extends T> list){
        addAll(list);
        return this;
    }
    
    default FluentCollectionX<T> minusLazy(Object e){
        remove(e);
        return this;
    }
    
    default FluentCollectionX<T> minusAllLazy(Collection<?> list){
        removeAll(list);
        return this;
    }

    
    default FluentCollectionX<T> plusInOrder(T e){
        return plus(e);
    }
    public FluentCollectionX<T> plus(T e);
    
    public FluentCollectionX<T> plusAll(Collection<? extends T> list) ;
    
    public FluentCollectionX<T> minus(Object e);
    
    public FluentCollectionX<T> minusAll(Collection<?> list);
    
    public <R> FluentCollectionX<R> unit(Collection<R> col);
}

