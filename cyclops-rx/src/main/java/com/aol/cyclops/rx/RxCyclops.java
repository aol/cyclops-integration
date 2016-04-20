package com.aol.cyclops.rx;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.For;
import com.aol.cyclops.control.FutureW;
import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.control.monads.transformers.FutureWT;
import com.aol.cyclops.control.monads.transformers.StreamT;
import com.aol.cyclops.control.monads.transformers.seq.FutureWTSeq;
import com.aol.cyclops.control.monads.transformers.seq.StreamTSeq;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.aol.cyclops.types.stream.reactive.SeqSubscriber;
import com.aol.cyclops.util.function.QuadFunction;
import com.aol.cyclops.util.function.TriFunction;

import rx.Observable;
import rx.RxReactiveStreams;

public interface RxCyclops {

	public static <T> AnyMSeq<T> observable(Observable<T> flux){
		return AnyM.ofSeq(flux);
	}
	public static <T> StreamTSeq<T> observableT(Publisher<Observable<T>> nested){
		return StreamT.fromPublisher(ReactiveSeq.fromPublisher(nested).map(f->{
			SeqSubscriber<T> sub = SeqSubscriber.subscriber();
			RxReactiveStreams.toPublisher(f).subscribe(sub);
			return sub.stream();
		}));
	}
	
	
	public interface ForObservable {

		static <T1, T2, T3, R1, R2, R3, R> Observable<R> each4(Observable<? extends T1> value1,
				Function<? super T1, ? extends Observable<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Observable<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(observable(value1)).anyM(a -> observable(value2.apply(a))).anyM(a -> b -> observable(value3.apply(a, b)))
							.anyM(a -> b -> c -> observable(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> Observable<R> each4(Observable<? extends T1> value1,
				Function<? super T1, ? extends Observable<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Observable<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(observable(value1)).anyM(a -> observable(value2.apply(a)))
					.anyM(a -> b -> observable(value3.apply(a, b))).anyM(a -> b -> c -> observable(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> Observable<R> each3(Observable<? extends T1> value1,
				Function<? super T1, ? extends Observable<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(observable(value1)).anyM(a -> observable(value2.apply(a)))
					.anyM(a -> b -> observable(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <T1, T2, R1, R2, R> Observable<R> each3(Observable<? extends T1> value1,
				Function<? super T1, ? extends Observable<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Observable<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofSeq(For.anyM(observable(value1)).anyM(a -> observable(value2.apply(a))).anyM(a -> b -> observable(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Observable<R> each2(Observable<? extends T> value1, Function<? super T, Observable<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(observable(value1)).anyM(a -> observable(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Observable<R> each2(Observable<? extends T> value1, Function<? super T, ? extends Observable<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(observable(value1)).anyM(a -> observable(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	
	
	
	
}
