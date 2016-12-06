package com.aol.cyclops.reactor.collections.extensions.base;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import com.aol.cyclops.data.collections.extensions.FluentCollectionX;

public interface NativePlusLoop<T> {
    FluentCollectionX<T> plusLoop(int max, IntFunction<T> value);
    FluentCollectionX<T> plusLoop(Supplier<Optional<T>> supplier);
}
