package com.aol.cyclops.reactor;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.For;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.reactor.transformer.FluxT;
import com.aol.cyclops.reactor.transformer.FluxTSeq;
import com.aol.cyclops.reactor.transformer.MonoT;
import com.aol.cyclops.reactor.transformer.MonoTSeq;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.aol.cyclops.util.function.QuadFunction;
import com.aol.cyclops.util.function.TriFunction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * For Comprehensions for Flux and Mono
 * 
 * AnyM creational methods for Flux and Mono
 * 
 * @author johnmcclean
 *
 */
public interface Reactor {

    public static <T> AnyMSeq<T> fromFluxT(FluxT<T> flux) {
        return AnyM.ofSeq(flux);
    }

    public static <T> AnyMSeq<T> flux(Flux<T> flux) {
        return AnyM.ofSeq(flux);
    }

    public static <T> FluxTSeq<T> fluxT(Publisher<Flux<T>> nested) {
        return FluxT.fromPublisher(nested);
    }

    public static <T> AnyMValue<T> mono(Mono<T> mono) {
        return AnyM.ofValue(mono);
    }

    public static <T> MonoTSeq<T> monoT(Publisher<Mono<T>> nested) {
        return MonoT.fromPublisher(Flux.from(nested));
    }

    public interface ForFlux {

        static <T1, T2, T3, R1, R2, R3, R> Flux<R> each4(Flux<? extends T1> value1,
                Function<? super T1, ? extends Flux<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Flux<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends Flux<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(flux(value1))
                                 .anyM(a -> flux(value2.apply(a)))
                                 .anyM(a -> b -> flux(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> flux(value4.apply(a, b, c)))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, T3, R1, R2, R3, R> Flux<R> each4(Flux<? extends T1> value1,
                Function<? super T1, ? extends Flux<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Flux<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends Flux<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(flux(value1))
                                 .anyM(a -> flux(value2.apply(a)))
                                 .anyM(a -> b -> flux(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> flux(value4.apply(a, b, c)))
                                 .filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> Flux<R> each3(Flux<? extends T1> value1,
                Function<? super T1, ? extends Flux<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Flux<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(flux(value1))
                                 .anyM(a -> flux(value2.apply(a)))
                                 .anyM(a -> b -> flux(value3.apply(a, b)))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> Flux<R> each3(Flux<? extends T1> value1,
                Function<? super T1, ? extends Flux<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Flux<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(flux(value1))
                                 .anyM(a -> flux(value2.apply(a)))
                                 .anyM(a -> b -> flux(value3.apply(a, b)))
                                 .filter(a -> b -> c -> filterFunction.apply(a, b, c))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> Flux<R> each2(Flux<? extends T> value1, Function<? super T, Flux<R1>> value2,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(flux(value1))
                                 .anyM(a -> flux(value2.apply(a)))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> Flux<R> each2(Flux<? extends T> value1, Function<? super T, ? extends Flux<R1>> value2,
                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(flux(value1))
                                 .anyM(a -> flux(value2.apply(a)))
                                 .filter(a -> b -> filterFunction.apply(a, b))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }
    }

    public interface ForFluxTransformer {

        static <T1, T2, T3, R1, R2, R3, R> FluxT<R> each4(FluxT<? extends T1> value1,
                Function<? super T1, ? extends FluxT<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends FluxT<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends FluxT<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromFluxT(value1))
                                 .anyM(a -> fromFluxT(value2.apply(a)))
                                 .anyM(a -> b -> fromFluxT(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> fromFluxT(value4.apply(a, b, c)))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, T3, R1, R2, R3, R> FluxT<R> each4(FluxT<? extends T1> value1,
                Function<? super T1, ? extends FluxT<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends FluxT<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends FluxT<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromFluxT(value1))
                                 .anyM(a -> fromFluxT(value2.apply(a)))
                                 .anyM(a -> b -> fromFluxT(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> fromFluxT(value4.apply(a, b, c)))
                                 .filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> FluxT<R> each3(FluxT<? extends T1> value1,
                Function<? super T1, ? extends FluxT<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends FluxT<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromFluxT(value1))
                                 .anyM(a -> fromFluxT(value2.apply(a)))
                                 .anyM(a -> b -> fromFluxT(value3.apply(a, b)))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> FluxT<R> each3(FluxT<? extends T1> value1,
                Function<? super T1, ? extends FluxT<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends FluxT<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromFluxT(value1))
                                 .anyM(a -> fromFluxT(value2.apply(a)))
                                 .anyM(a -> b -> fromFluxT(value3.apply(a, b)))
                                 .filter(a -> b -> c -> filterFunction.apply(a, b, c))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> FluxT<R> each2(FluxT<? extends T> value1, Function<? super T, FluxT<R1>> value2,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromFluxT(value1))
                                 .anyM(a -> fromFluxT(value2.apply(a)))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> FluxT<R> each2(FluxT<? extends T> value1, Function<? super T, ? extends FluxT<R1>> value2,
                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromFluxT(value1))
                                 .anyM(a -> fromFluxT(value2.apply(a)))
                                 .filter(a -> b -> filterFunction.apply(a, b))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }
    }

    public interface ForMono {

        static <T1, T2, T3, R1, R2, R3, R> Mono<R> each4(Mono<? extends T1> value1,
                Function<? super T1, ? extends Mono<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Mono<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends Mono<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(mono(value1))
                                   .anyM(a -> mono(value2.apply(a)))
                                   .anyM(a -> b -> mono(value3.apply(a, b)))
                                   .anyM(a -> b -> c -> mono(value4.apply(a, b, c)))
                                   .yield4(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T1, T2, T3, R1, R2, R3, R> Mono<R> each4(Mono<? extends T1> value1,
                Function<? super T1, ? extends Mono<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Mono<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends Mono<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(mono(value1))
                                   .anyM(a -> mono(value2.apply(a)))
                                   .anyM(a -> b -> mono(value3.apply(a, b)))
                                   .anyM(a -> b -> c -> mono(value4.apply(a, b, c)))
                                   .filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d))
                                   .yield4(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> Mono<R> each3(Mono<? extends T1> value1,
                Function<? super T1, ? extends Mono<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Mono<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(mono(value1))
                                   .anyM(a -> mono(value2.apply(a)))
                                   .anyM(a -> b -> mono(value3.apply(a, b)))
                                   .yield3(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> Mono<R> each3(Mono<? extends T1> value1,
                Function<? super T1, ? extends Mono<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Mono<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(mono(value1))
                                   .anyM(a -> mono(value2.apply(a)))
                                   .anyM(a -> b -> mono(value3.apply(a, b)))
                                   .filter(a -> b -> c -> filterFunction.apply(a, b, c))
                                   .yield3(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T, R1, R> Mono<R> each2(Mono<? extends T> value1, Function<? super T, Mono<R1>> value2,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(mono(value1))
                                   .anyM(a -> mono(value2.apply(a)))
                                   .yield2(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }

        static <T, R1, R> Mono<R> each2(Mono<? extends T> value1, Function<? super T, ? extends Mono<R1>> value2,
                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofValue(For.anyM(mono(value1))
                                   .anyM(a -> mono(value2.apply(a)))
                                   .filter(a -> b -> filterFunction.apply(a, b))
                                   .yield2(yieldingFunction)
                                   .unwrap())
                       .unwrap();

        }
    }

}
