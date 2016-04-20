package com.aol.cyclops.reactor;

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
import com.aol.cyclops.util.function.QuadFunction;
import com.aol.cyclops.util.function.TriFunction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Reactor {

	public static <T> AnyMSeq<T> flux(Flux<T> flux){
		return AnyM.ofSeq(flux);
	}
	public static <T> StreamTSeq<T> fluxT(Publisher<Flux<T>> nested){
		return StreamT.fromPublisher(flux(Flux.from(nested).map(f->ReactiveSeq.fromPublisher(f))));
	}
	
	public static <T> AnyMValue<T> mono(Mono<T> mono){
		return AnyM.ofValue(mono);
	}
	public static <T> FutureWTSeq<T> monoT(Publisher<Mono<T>> nested){
		return FutureWT.fromPublisher(flux(Flux.from(nested).map(f->FutureW.of(f.toCompletableFuture()))));
	}
	public interface ForFlux {

		static <T1, T2, T3, R1, R2, R3, R> Flux<R> each4(Flux<? extends T1> value1,
				Function<? super T1, ? extends Flux<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Flux<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Flux<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(flux(value1)).anyM(a -> flux(value2.apply(a))).anyM(a -> b -> flux(value3.apply(a, b)))
							.anyM(a -> b -> c -> flux(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> Flux<R> each4(Flux<? extends T1> value1,
				Function<? super T1, ? extends Flux<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Flux<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Flux<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(flux(value1)).anyM(a -> flux(value2.apply(a)))
					.anyM(a -> b -> flux(value3.apply(a, b))).anyM(a -> b -> c -> flux(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> Flux<R> each3(Flux<? extends T1> value1,
				Function<? super T1, ? extends Flux<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Flux<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(flux(value1)).anyM(a -> flux(value2.apply(a)))
					.anyM(a -> b -> flux(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <T1, T2, R1, R2, R> Flux<R> each3(Flux<? extends T1> value1,
				Function<? super T1, ? extends Flux<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Flux<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofSeq(For.anyM(flux(value1)).anyM(a -> flux(value2.apply(a))).anyM(a -> b -> flux(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Flux<R> each2(Flux<? extends T> value1, Function<? super T, Flux<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(flux(value1)).anyM(a -> flux(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Flux<R> each2(Flux<? extends T> value1, Function<? super T, ? extends Flux<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(flux(value1)).anyM(a -> flux(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	public interface ForMono {

		static <T1, T2, T3, R1, R2, R3, R> Mono<R> each4(Mono<? extends T1> value1,
				Function<? super T1, ? extends Mono<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Mono<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Mono<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(mono(value1)).anyM(a -> mono(value2.apply(a))).anyM(a -> b -> mono(value3.apply(a, b)))
							.anyM(a -> b -> c -> mono(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> Mono<R> each4(Mono<? extends T1> value1,
				Function<? super T1, ? extends Mono<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Mono<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Mono<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(mono(value1)).anyM(a -> mono(value2.apply(a)))
					.anyM(a -> b -> mono(value3.apply(a, b))).anyM(a -> b -> c -> mono(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> Mono<R> each3(Mono<? extends T1> value1,
				Function<? super T1, ? extends Mono<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Mono<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(mono(value1)).anyM(a -> mono(value2.apply(a)))
					.anyM(a -> b -> mono(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <T1, T2, R1, R2, R> Mono<R> each3(Mono<? extends T1> value1,
				Function<? super T1, ? extends Mono<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Mono<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.anyM(mono(value1)).anyM(a -> mono(value2.apply(a))).anyM(a -> b -> mono(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Mono<R> each2(Mono<? extends T> value1, Function<? super T, Mono<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(mono(value1)).anyM(a -> mono(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Mono<R> each2(Mono<? extends T> value1, Function<? super T, ? extends Mono<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(mono(value1)).anyM(a -> mono(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	
	public static void main(String[] args){
	   Flux<Integer> flux = Flux.just(1,2,3,4).map(i->i+2);
	   Flux<String> string = flux.map(s->"hello"+s);
	  System.out.println(flux.toList().get());
	  System.out.println(flux.toList().get());
	  System.out.println(string.toList().get());
	  System.out.println(string.toList().get());
	}
	
}
