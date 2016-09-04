package com.aol.cyclops.rx;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.For;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.rx.transformer.ObservableT;
import com.aol.cyclops.rx.transformer.ObservableTSeq;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.util.function.QuadFunction;
import com.aol.cyclops.util.function.TriFunction;

import rx.Observable;
import rx.RxReactiveStreams;

public interface RxCyclops {
    public static <T> AnyMSeq<T> fromObservableT(ObservableT<T> flux) {
        return AnyM.ofSeq(flux);
    }

    public static <T> Publisher<T> publisher(Observable<T> observable) {
        return RxReactiveStreams.toPublisher(observable);
    }

    public static <T> ReactiveSeq<T> reactiveSeq(Observable<T> observable) {
        return ReactiveSeq.fromPublisher(publisher(observable));
    }

    public static <T> Observable<T> toObservable(Publisher<T> publisher) {
        return RxReactiveStreams.toObservable(publisher);
    }

    public static <T> AnyMSeq<T> observable(Observable<T> flux) {
        return AnyM.ofSeq(flux);
    }

    public static <T> ObservableTSeq<T> observableT(Publisher<Observable<T>> nested) {
        return ObservableT.fromPublisher(nested);
    }
    public static <T> ObservableTSeq<T> observableT(Observable<Observable<T>> nested) {
        return ObservableT.fromObservable(nested);
    }

    public interface ForObservable {

        static <T1, T2, T3, R1, R2, R3, R> Observable<R> each4(Observable<? extends T1> value1,
                Function<? super T1, ? extends Observable<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends Observable<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(observable(value1))
                                 .anyM(a -> observable(value2.apply(a)))
                                 .anyM(a -> b -> observable(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> observable(value4.apply(a, b, c)))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, T3, R1, R2, R3, R> Observable<R> each4(Observable<? extends T1> value1,
                Function<? super T1, ? extends Observable<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends Observable<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(observable(value1))
                                 .anyM(a -> observable(value2.apply(a)))
                                 .anyM(a -> b -> observable(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> observable(value4.apply(a, b, c)))
                                 .filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> Observable<R> each3(Observable<? extends T1> value1,
                Function<? super T1, ? extends Observable<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(observable(value1))
                                 .anyM(a -> observable(value2.apply(a)))
                                 .anyM(a -> b -> observable(value3.apply(a, b)))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> Observable<R> each3(Observable<? extends T1> value1,
                Function<? super T1, ? extends Observable<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(observable(value1))
                                 .anyM(a -> observable(value2.apply(a)))
                                 .anyM(a -> b -> observable(value3.apply(a, b)))
                                 .filter(a -> b -> c -> filterFunction.apply(a, b, c))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> Observable<R> each2(Observable<? extends T> value1,
                Function<? super T, Observable<R1>> value2,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(observable(value1))
                                 .anyM(a -> observable(value2.apply(a)))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> Observable<R> each2(Observable<? extends T> value1,
                Function<? super T, ? extends Observable<R1>> value2,
                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(observable(value1))
                                 .anyM(a -> observable(value2.apply(a)))
                                 .filter(a -> b -> filterFunction.apply(a, b))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }
    }

    public interface ForObservableTransformer {

        static <T1, T2, T3, R1, R2, R3, R> ObservableT<R> each4(ObservableT<? extends T1> value1,
                Function<? super T1, ? extends ObservableT<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends ObservableT<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends ObservableT<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromObservableT(value1))
                                 .anyM(a -> fromObservableT(value2.apply(a)))
                                 .anyM(a -> b -> fromObservableT(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> fromObservableT(value4.apply(a, b, c)))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, T3, R1, R2, R3, R> ObservableT<R> each4(ObservableT<? extends T1> value1,
                Function<? super T1, ? extends ObservableT<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends ObservableT<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends ObservableT<R3>> value4,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
                QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromObservableT(value1))
                                 .anyM(a -> fromObservableT(value2.apply(a)))
                                 .anyM(a -> b -> fromObservableT(value3.apply(a, b)))
                                 .anyM(a -> b -> c -> fromObservableT(value4.apply(a, b, c)))
                                 .filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d))
                                 .yield4(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> ObservableT<R> each3(ObservableT<? extends T1> value1,
                Function<? super T1, ? extends ObservableT<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends ObservableT<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromObservableT(value1))
                                 .anyM(a -> fromObservableT(value2.apply(a)))
                                 .anyM(a -> b -> fromObservableT(value3.apply(a, b)))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T1, T2, R1, R2, R> ObservableT<R> each3(ObservableT<? extends T1> value1,
                Function<? super T1, ? extends ObservableT<R1>> value2,
                BiFunction<? super T1, ? super R1, ? extends ObservableT<R2>> value3,
                TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
                TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromObservableT(value1))
                                 .anyM(a -> fromObservableT(value2.apply(a)))
                                 .anyM(a -> b -> fromObservableT(value3.apply(a, b)))
                                 .filter(a -> b -> c -> filterFunction.apply(a, b, c))
                                 .yield3(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> ObservableT<R> each2(ObservableT<? extends T> value1,
                Function<? super T, ObservableT<R1>> value2,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromObservableT(value1))
                                 .anyM(a -> fromObservableT(value2.apply(a)))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }

        static <T, R1, R> ObservableT<R> each2(ObservableT<? extends T> value1,
                Function<? super T, ? extends ObservableT<R1>> value2,
                BiFunction<? super T, ? super R1, Boolean> filterFunction,
                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

            return AnyM.ofSeq(For.anyM(fromObservableT(value1))
                                 .anyM(a -> fromObservableT(value2.apply(a)))
                                 .filter(a -> b -> filterFunction.apply(a, b))
                                 .yield2(yieldingFunction)
                                 .unwrap())
                       .unwrap();

        }
    }

}
