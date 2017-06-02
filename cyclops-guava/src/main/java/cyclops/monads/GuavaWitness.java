package cyclops.monads;


import com.aol.cyclops.guava.adapter.FluentIterableAdapter;
import com.aol.cyclops.guava.adapter.OptionalAdapter;

import com.aol.cyclops2.types.extensability.FunctionalAdapter;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;


public interface GuavaWitness {

    public static <T> Optional<T> optional(AnyM<optional,? extends T> anyM){
        return anyM.unwrap();
    }
    public static <T> FluentIterable<T> fluentIterable(AnyM<fluentIterable,? extends T> anyM){
        return anyM.unwrap();
    }
    static interface FluentIterableWitness<W extends FluentIterableWitness<W>>  extends WitnessType<W> {

    }
    public static enum fluentIterable implements FluentIterableWitness<fluentIterable> {
        INSTANCE;

        @Override
        public FunctionalAdapter<fluentIterable> adapter() {
            return new FluentIterableAdapter();
        }

    }
    static interface OptionalWitness<W extends GuavaWitness.OptionalWitness<W>>  extends WitnessType<W> {

    }
    public static enum optional implements OptionalWitness<optional> {
        INSTANCE;

        @Override
        public FunctionalAdapter<optional> adapter() {
            return new OptionalAdapter();
        }

    }
}
