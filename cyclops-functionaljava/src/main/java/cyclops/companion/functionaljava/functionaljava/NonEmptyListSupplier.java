package cyclops.companion.functionaljava.functionaljava;

import fj.data.NonEmptyList;

@FunctionalInterface
public interface NonEmptyListSupplier{

    <T> NonEmptyList<T> get();
}