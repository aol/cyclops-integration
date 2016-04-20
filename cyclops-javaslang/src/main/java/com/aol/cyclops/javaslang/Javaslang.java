package com.aol.cyclops.javaslang;

import java.util.Optional;

import com.aol.cyclops.monad.AnyM;

import javaslang.Value;
import javaslang.collection.LazyStream;
import javaslang.collection.List;
import javaslang.control.Either;
import javaslang.control.Either.LeftProjection;
import javaslang.control.Either.RightProjection;
import javaslang.control.Option;
import javaslang.control.Try;

public class Javaslang {
	public static <T> AnyM<T> anyMonad(Value<T> monadM){
		return AnyM.ofMonad(monadM);
	}
	
	public static <T> AnyM<T> anyM(Try<T> tryM){
		return AnyM.ofMonad(tryM);
	}
	public static <T> AnyM<T> anyMFailure(Try<T> tryM){
		if(tryM.isFailure())
			return AnyM.ofMonad(Try.of(()->tryM.toEither().left().get()));
		return AnyM.ofMonad(Optional.empty());
	}
	public static <T> AnyM<T> anyM(Either<?,T> tryM){
		return AnyM.ofMonad(tryM);
	}
	public static <T> AnyM<T> anyM(RightProjection<?,T> tryM){
		if(tryM.toJavaOptional().isPresent())
			return AnyM.ofMonad(Either.right(tryM.get()));
		else
			return AnyM.ofMonad(Optional.empty());
	}
	public static <T> AnyM<T> anyM(LeftProjection<T,?> tryM){
		if(tryM.toJavaOptional().isPresent())
			return AnyM.ofMonad(Either.right(tryM.get()));
		else
			return AnyM.ofMonad(Optional.empty());
	}
	public static <T> AnyM<T> anyM(Option<T> tryM){
		return AnyM.ofMonad(tryM);
	}
	
	public static <T> AnyM<T> anyM(List<T> tryM){
		return AnyM.ofMonad(tryM);
	}
	
	public interface ForTraversable {

		static <T1, T2, T3, R1, R2, R3, R> Traversable<R> each4(Traversable<? extends T1> value1,
				Function<? super T1, ? extends Traversable<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Traversable<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Traversable<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(traversable(value1)).anyM(a -> traversable(value2.apply(a))).anyM(a -> b -> traversable(value3.apply(a, b)))
							.anyM(a -> b -> c -> traversable(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> Traversable<R> each4(Traversable<? extends T1> value1,
				Function<? super T1, ? extends Traversable<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Traversable<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Traversable<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(traversable(value1)).anyM(a -> traversable(value2.apply(a)))
					.anyM(a -> b -> traversable(value3.apply(a, b))).anyM(a -> b -> c -> traversable(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> Traversable<R> each3(Traversable<? extends T1> value1,
				Function<? super T1, ? extends Traversable<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Traversable<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(traversable(value1)).anyM(a -> traversable(value2.apply(a)))
					.anyM(a -> b -> traversable(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			;

		}

		static <T1, T2, R1, R2, R> Traversable<R> each3(Traversable<? extends T1> value1,
				Function<? super T1, ? extends Traversable<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Traversable<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofSeq(For.anyM(traversable(value1)).anyM(a -> traversable(value2.apply(a))).anyM(a -> b -> traversable(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			;

		}

		static <T, R1, R> Traversable<R> each2(Traversable<? extends T> value1, Function<? super T, Traversable<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(traversable(value1)).anyM(a -> traversable(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			;

		}

		static <T, R1, R> Traversable<R> each2(Traversable<? extends T> value1, Function<? super T, ? extends Traversable<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(traversable(value1)).anyM(a -> traversable(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			;

		}
	}
	public interface ForValue {

		static <T1, T2, T3, R1, R2, R3, R> Value<R> each4(Value<? extends T1> value1,
				Function<? super T1, ? extends Value<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Value<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Value<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(value(value1)).anyM(a -> value(value2.apply(a))).anyM(a -> b -> value(value3.apply(a, b)))
							.anyM(a -> b -> c -> value(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> Value<R> each4(Value<? extends T1> value1,
				Function<? super T1, ? extends Value<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Value<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Value<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(value(value1)).anyM(a -> value(value2.apply(a)))
					.anyM(a -> b -> value(value3.apply(a, b))).anyM(a -> b -> c -> value(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> Value<R> each3(Value<? extends T1> value1,
				Function<? super T1, ? extends Value<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Value<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(value(value1)).anyM(a -> value(value2.apply(a)))
					.anyM(a -> b -> value(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			;

		}

		static <T1, T2, R1, R2, R> Value<R> each3(Value<? extends T1> value1,
				Function<? super T1, ? extends Value<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Value<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.anyM(value(value1)).anyM(a -> value(value2.apply(a))).anyM(a -> b -> value(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			;

		}

		static <T, R1, R> Value<R> each2(Value<? extends T> value1, Function<? super T, Value<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(value(value1)).anyM(a -> value(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			;

		}

		static <T, R1, R> Value<R> each2(Value<? extends T> value1, Function<? super T, ? extends Value<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(value(value1)).anyM(a -> value(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			;

		}
	}
}
