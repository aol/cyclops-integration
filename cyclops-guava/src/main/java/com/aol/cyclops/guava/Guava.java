
package com.aol.cyclops.guava;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.For;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.aol.cyclops.util.function.QuadFunction;
import com.aol.cyclops.util.function.TriFunction;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class Guava {

    public static <T> Maybe<T> asMaybe(Optional<T> option) {
        return option.isPresent() ? Maybe.just(option.get()) : Maybe.none();
    }

    /**
     * <pre>
     * {@code
     * Guava.optional(Optional.of("hello world"))
    			.map(String::toUpperCase)
    			.toSequence()
    			.toList()
     * }
     * //[HELLO WORLD]
     * </pre>
     * 
     * @param optionM to construct AnyM from
     * @return AnyM
     */
    public static <T> AnyMValue<T> optional(Optional<T> optionM) {
        return AnyM.ofValue(optionM);
    }

    /**
     * <pre>
     * {@code
     * Guava.anyM(FluentIterable.of(new String[]{"hello world"}))
    			.map(String::toUpperCase)
    			.flatMap(i->AnyMonads.anyM(java.util.stream.Stream.of(i)))
    			.toSequence()
    			.toList()
     * }
     *  //[HELLO WORLD]
     * </pre>
     * 
     * @param streamM to construct AnyM from
     * @return AnyM
     */
    public static <T> AnyMSeq<T> fluentIterable(FluentIterable<T> streamM) {
        return AnyM.ofSeq(streamM);
    }

    public interface ForOptional {

        static <T1, T2, T3, R1, R2, R3, R> Optional<R> each4(Optional<? extends T1> value1,
                Function<? super T1, ? extends Optional<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Optional<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends Optional<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(optional(value1))
                                   .anyM(a -> optional(value2.apply(a)))
                                   .anyM(a -> b -> optional(value3.apply(a, b)))
                                   .anyM(a -> b -> c -> optional(value4.apply(a, b, c)))
                                   .yield4(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T1, T2, T3, R1, R2, R3, R> Optional<R> each4(Optional<? extends T1> value1,
                Function<? super T1, ? extends Optional<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Optional<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends Optional<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(optional(value1))
                                   .anyM(a -> optional(value2.apply(a)))
                                   .anyM(a -> b -> optional(value3.apply(a, b)))
                                   .anyM(a -> b -> c -> optional(value4.apply(a, b, c)))
                                   .filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d))
                                   .yield4(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> Optional<R> each3(Optional<? extends T1> value1,
                Function<? super T1, ? extends Optional<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Optional<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(optional(value1))
                                   .anyM(a -> optional(value2.apply(a)))
                                   .anyM(a -> b -> optional(value3.apply(a, b)))
                                   .yield3(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> Optional<R> each3(Optional<? extends T1> value1,
                Function<? super T1, ? extends Optional<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Optional<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(optional(value1))
                                   .anyM(a -> optional(value2.apply(a)))
                                   .anyM(a -> b -> optional(value3.apply(a, b)))
                                   .filter(a -> b -> c -> filterFunction.apply(a, b, c))
                                   .yield3(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T, R1, R> Optional<R> each2(Optional<? extends T> value1, Function<? super T, Optional<R1>> value2,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(optional(value1))
                                   .anyM(a -> optional(value2.apply(a)))
                                   .yield2(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T, R1, R> Optional<R> each2(Optional<? extends T> value1,
                Function<? super T, ? extends Optional<R1>> value2,
                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(optional(value1))
                                   .anyM(a -> optional(value2.apply(a)))
                                   .filter(a -> b -> filterFunction.apply(a, b))
                                   .yield2(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }
    }

    public interface ForFluentIterable {

        static <T1, T2, T3, R1, R2, R3, R> FluentIterable<R> each4(FluentIterable<? extends T1> value1,
                Function<? super T1, ? extends FluentIterable<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends FluentIterable<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends FluentIterable<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fluentIterable(value1))
                                 .anyM(a -> fluentIterable(value2.apply(a)))
                                 .anyM(a -> b -> fluentIterable(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> fluentIterable(value4.apply(a, b, c)))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, T3, R1, R2, R3, R> FluentIterable<R> each4(FluentIterable<? extends T1> value1,
                Function<? super T1, ? extends FluentIterable<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends FluentIterable<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends FluentIterable<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fluentIterable(value1))
                                 .anyM(a -> fluentIterable(value2.apply(a)))
                                 .anyM(a -> b -> fluentIterable(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> fluentIterable(value4.apply(a, b, c)))
                                 .filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> FluentIterable<R> each3(FluentIterable<? extends T1> value1,
                Function<? super T1, ? extends FluentIterable<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends FluentIterable<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fluentIterable(value1))
                                 .anyM(a -> fluentIterable(value2.apply(a)))
                                 .anyM(a -> b -> fluentIterable(value3.apply(a, b)))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> FluentIterable<R> each3(FluentIterable<? extends T1> value1,
                Function<? super T1, ? extends FluentIterable<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends FluentIterable<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fluentIterable(value1))
                                 .anyM(a -> fluentIterable(value2.apply(a)))
                                 .anyM(a -> b -> fluentIterable(value3.apply(a, b)))
                                 .filter(a -> b -> c -> filterFunction.apply(a, b, c))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> FluentIterable<R> each2(FluentIterable<? extends T> value1,
                Function<? super T, FluentIterable<R1>> value2,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fluentIterable(value1))
                                 .anyM(a -> fluentIterable(value2.apply(a)))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> FluentIterable<R> each2(FluentIterable<? extends T> value1,
                Function<? super T, ? extends FluentIterable<R1>> value2,
                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fluentIterable(value1))
                                 .anyM(a -> fluentIterable(value2.apply(a)))
                                 .filter(a -> b -> filterFunction.apply(a, b))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }
    }
}
