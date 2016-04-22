package com.aol.cyclops.functionaljava;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.For;
import com.aol.cyclops.internal.monads.ComprehenderSelector;
import com.aol.cyclops.types.anyM.AnyMSeq;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.aol.cyclops.util.function.QuadFunction;
import com.aol.cyclops.util.function.TriFunction;

import fj.P1;
import fj.control.Trampoline;
import fj.data.Either;
import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.IterableW;
import fj.data.List;
import fj.data.Option;
import fj.data.Reader;
import fj.data.State;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.Writer;

/**
 * FunctionalJava Cyclops integration point
 * 
 * @author johnmcclean
 *
 */
public interface FJ {
	
	/**
	 * Methods for making working with FJ's Trampoline a little more Java8 friendly
	 *
	 */
	public static class Trampoline8{
		/**
		 * 
		 * <pre>
		 * {@code
		 * List<String> list = FJ.anyM(FJ.Trampoline.suspend(() -> Trampoline.pure("hello world")))
								.map(String::toUpperCase)
								.asSequence()
								.toList();
		      // ["HELLO WORLD"]
		 * }
		 * </pre>
		 * 
		 * @param s Suspend using a Supplier
		 * 
		 * @return Next Trampoline stage
		 */
		public static <T> fj.control.Trampoline<T> suspend(Supplier<fj.control.Trampoline<T>> s ){
			return fj.control.Trampoline.suspend(new P1<fj.control.Trampoline<T>>(){

				@Override
				public fj.control.Trampoline<T> _1() {
					return s.get();
				}
			
			});
		}
	}
	
	
	
	/**
	 * Unwrap an AnyM to a Reader
	 * 
	 * <pre>
	 * {@code 
	 *   FJ.unwrapReader(FJ.anyM(Reader.unit( (Integer a) -> "hello "+a ))
						.map(String::toUpperCase))
						.f(10)
	 * 
	 * }
	 * </pre>
	 * 
	 * @param anyM Monad to unwrap
	 * @return unwrapped reader
	 */
	public static  <A,B> Reader<A,B> unwrapReader(AnyM<B> anyM){
		
		Reader unwrapper = Reader.unit(a->1);
		return (Reader)new ComprehenderSelector()
							.selectComprehender(unwrapper)
							.executeflatMap(unwrapper, i-> anyM.unwrap());
		
	}
	/**
	 * <pre>
	 * {@code 
	 * 		FJ.unwrapWriter(FJ.anyM(writer)
				.map(String::toUpperCase),writer)
				.value()
	 * }
	 * </pre>
	 * 
	 * @param anyM AnyM to unwrap to Writer
	 * @param unwrapper Writer of same type to do unwrapping
	 * @return Unwrapped writer
	 */
	public static  <A,B> Writer<A,B> unwrapWriter(AnyM<B> anyM,Writer<B,?> unwrapper){
		
		
		return (Writer)new ComprehenderSelector()
							.selectComprehender(unwrapper)
							.executeflatMap(unwrapper, i-> anyM.unwrap());
		
	}
	/**
	 * <pre>
	 * {@code 
	 * 		FJ.unwrapState(FJ.anyM(State.constant("hello"))
								.map(String::toUpperCase))
								.run("")
								._2()
	 * 
	 * }
	 * </pre>
	 * @param anyM AnyM to unwrap to State monad
	 * @return State monad
	 */
	public static <A,B> State<A,B> unwrapState(AnyM<B> anyM){
		
		State unwrapper = State.constant(1);
		return (State)new ComprehenderSelector()
							.selectComprehender(unwrapper)
							.executeflatMap(unwrapper, i-> anyM.unwrap());
		
	}
	/**
	 * <pre>
	 * {@code
	 *    FJ.unwrapIO( 
				FJ.anyM(IOFunctions.lazy(a->{ System.out.println("hello world"); return a;}))
				.map(a-> {System.out.println("hello world2"); return a;})   )
				.run();
	 * 
	 * }
	 * </pre>
	 * @param anyM to unwrap to IO Monad
	 * @return IO Monad
	 */
	public static  <B> IO<B> unwrapIO(AnyMValue<B> anyM){
		
		IO unwrapper = IOFunctions.unit(1);
		return (IO)new ComprehenderSelector()
							.selectComprehender(unwrapper)
							.executeflatMap(unwrapper, i-> anyM.unwrap());
		
	}
	/**
	 * 
	 * <pre>
	 * {@code
	 * FJ.anyM(IOFunctions.lazy(a->{ System.out.println("hello world"); return a;}))
	 * }
	 * </pre>
	 * 
	 * @param ioM Construct an AnyM from the supplied IO Monad
	 * @return AnyM
	 */
	public static <T> AnyMValue<T> io(IO<T> ioM){
		return AnyM.ofValue(ioM);
	}
	/**
	 * <pre>
	 * {@code
	 * AnyM<String> anyM  = FJ.anyM(State.constant("hello"))
							.map(String::toUpperCase)
		}
		</pre>
	 * @param stateM Construct an AnyM from the supplied State Monad
	 * @return AnyM
	 */
	public static <T> AnyMValue<T> state(State<?,T> stateM){
		return  AnyM.ofValue(stateM);
	}
	/**
	 * <pre>
	 * {@code
	 * FJ.anyM(Validation.success(success()))
			.map(String::toUpperCase)
			.toSequence()
			.toList()
	 * }
	 * 
	 * @param validationM to  construct an AnyM from
	 * @return AnyM
	 */
	public static <T> AnyMValue<T> validation(Validation<?,T> validationM){
		return  AnyM.ofValue(validationM);
	}
	
	
	
	/**
	 * <pre>
	 * {@code
	 * FJ.anyMValue(Writer.unit("hello",Monoid.stringMonoid))
	 * }
	 * </pre>
	 * 
	 * @param writerM to construct an AnyM from
	 * @return AnyM
	 */
	public static <W,T> AnyMValue<T> writer(Writer<W,T> writerM){
			return  AnyM.ofValue(writerM);
	}
	/**
	 * <pre>
	 * {@code 
	 * 	FJ.reader(Reader.unit( (Integer a) -> "hello "+a )
	 * }
	 * </pre>
	 * 
	 * Create an AnyM, input type will be ignored, while Reader is wrapped in AnyM
	 * Extract to access and provide input value
	 * 
	 * @param readerM to create AnyM from 
	 * @return AnyM
	 */
	public static <T> AnyMValue<T> reader(Reader<?,T> readerM){
		return  AnyM.ofValue(readerM);
	}
	/**
	 * <pre>
	 * {@code
	 * FJ.trampoline(FJ.Trampoline.suspend(()-> Trampoline.pure(finalStage()))
	 * }
	 * </pre>
	 * 
	 * @param trampolineM to create AnyM from
	 * @return AnyM
	 */
	public static <T> AnyMValue<T> trampoline(fj.control.Trampoline<T> trampolineM){
		return  AnyM.ofValue(trampolineM);
	}
	/**
	 * <pre>
	 * {@code
	 * FJ.iterableW(IterableW.wrap(Arrays.asList("hello world")))
				.map(String::toUpperCase)
				.toSequence()
				.toList()
	 * 
	 * }
	 * 
	 * @param iterableWM to create AnyM from
	 * @return AnyM
	 */
	public static <T> AnyMSeq<T> iterableW(IterableW<T> iterableWM){
		return  AnyM.ofSeq(iterableWM);
	}
	/**
	 * (Right biased)
	 * <pre>
	 * {@code 
	 * FJ.either(Either.right("hello world"))
			.map(String::toUpperCase)
			.flatMapOptional(Optional::of)
			.toSequence()
			.toList()
	 * }
	 * </pre>
	 * 
	 * @param eitherM to construct AnyM from
	 * @return AnyM
	 */
	public static <T> AnyMValue<T> either(Either<?,T> eitherM){
		return  AnyM.ofValue(eitherM);
	}
	/**
	 * <pre>
	 * {@code
	 * FJ.right(Either.right("hello world").right())
			.map(String::toUpperCase)
			.toSequence()
			.toList()
			
			//[HELLO WORLD]
	 * }</pre>
	 * 
	 * 
	 * @param rM Right projection to construct AnyM from
	 * @return AnyM
	 */
	public static <T> AnyMValue<T> right(Either<?,T>.RightProjection<?,T> rM){
		if(rM.toOption().isSome())
			return  AnyM.ofValue(Either.right(rM.value()).right());
		else
			return  AnyM.ofValue(Optional.empty());
	}
	/**
	 * <pre>
	 * {@code
	 *  FJ.left(Either.<String,String>left("hello world").left())
			.map(String::toUpperCase)
			.flatMapOptional(Optional::of)
			.toSequence()
			.toList() 
	 * }
	 * //[HELLO WORLD]
	 * </pre>
	 * 
	 * @param lM Left Projection to construct AnyM from
	 * @return AnyM
	 */
	public static <T> AnyMValue<T> left(Either<T,?>.LeftProjection<T,?> lM){
		if(lM.toOption().isSome()) //works in the opposite way to javaslang
			return  AnyM.ofValue(Either.right(lM.value()).right());
		else
			return  AnyM.ofValue(Optional.empty());
	}
	/**
	 * <pre>
	 * {@code
	 * FJ.anyM(Option.some("hello world"))
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
	public static <T> AnyMValue<T> option(Option<T> optionM){
		return  AnyM.ofValue(optionM);
	}
	/**
	 * <pre>
	 * {@code
	 * FJ.stream(Stream.stream("hello world"))
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
	public static <T> AnyMSeq<T> stream(Stream<T> streamM){
		return  AnyM.ofSeq(streamM);
	}
	/**
	 * <pre>
	 * {@code 
	 * FJ.list(List.list("hello world"))
				.map(String::toUpperCase)
				.toSequence()
				.toList()
	 * }
	 * </pre>
	 * @param listM to Construct AnyM from
	 * @return AnyM
	 */
	public static <T> AnyMSeq<T> list(List<T> listM){
		return  AnyM.ofSeq(listM);
	}

	public interface ForOption {

		static <T1, T2, T3, R1, R2, R3, R> Option<R> each4(Option<? extends T1> value1,
				Function<? super T1, ? extends Option<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Option<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Option<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.iterable(value1).iterable(a -> value2.apply(a)).iterable(a -> b -> value3.apply(a, b))
							.iterable(a -> b -> c -> value4.apply(a, b, c)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> Option<R> each4(Option<? extends T1> value1,
				Function<? super T1, ? extends Option<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Option<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Option<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.iterable(value1).iterable(a -> value2.apply(a))
					.iterable(a -> b -> value3.apply(a, b)).iterable(a -> b -> c -> value4.apply(a, b, c))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> Option<R> each3(Option<? extends T1> value1,
				Function<? super T1, ? extends Option<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Option<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.iterable(value1).iterable(a -> value2.apply(a))
					.iterable(a -> b -> value3.apply(a, b)).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <T1, T2, R1, R2, R> Option<R> each3(Option<? extends T1> value1,
				Function<? super T1, ? extends Option<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Option<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.iterable(value1).iterable(a -> value2.apply(a)).iterable(a -> b -> value3.apply(a, b))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Option<R> each2(Option<? extends T> value1, Function<? super T, Option<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.iterable(value1).iterable(a -> value2.apply(a)).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Option<R> each2(Option<? extends T> value1, Function<? super T, ? extends Option<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.iterable(value1).iterable(a -> value2.apply(a))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	public interface ForState {

		static <S,T1, T2, T3, R1, R2, R3, R> State<S,R> each4(State<S,? extends T1> value1,
				Function<? super T1, ? extends State<S,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends State<S,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends State<S,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(state(value1)).anyM(a -> state(value2.apply(a))).anyM(a -> b -> state(value3.apply(a, b)))
							.anyM(a -> b -> c -> state(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <S,T1, T2, T3, R1, R2, R3, R> State<S,R> each4(State<S,? extends T1> value1,
				Function<? super T1, ? extends State<S,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends State<S,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends State<S,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(state(value1)).anyM(a -> state(value2.apply(a)))
					.anyM(a -> b -> state(value3.apply(a, b))).anyM(a -> b -> c -> state(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <S,T1, T2, R1, R2, R> State<S,R> each3(State<S,? extends T1> value1,
				Function<? super T1, ? extends State<S,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends State<S,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(state(value1)).anyM(a -> state(value2.apply(a)))
					.anyM(a -> b -> state(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <S,T1, T2, R1, R2, R> State<S,R> each3(State<S,? extends T1> value1,
				Function<? super T1, ? extends State<S,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends State<S,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.anyM(state(value1)).anyM(a -> state(value2.apply(a))).anyM(a -> b -> state(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <S,T, R1, R> State<S,R> each2(State<S,? extends T> value1, Function<? super T, State<S,R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(state(value1)).anyM(a -> state(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <S,T, R1, R> State<S,R> each2(State<S,? extends T> value1, Function<? super T, ? extends State<S,R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(state(value1)).anyM(a -> state(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	public interface ForEither {

		static <L,T1, T2, T3, R1, R2, R3, R> Either<L,R> each4(Either<L,? extends T1> value1,
				Function<? super T1, ? extends Either<L,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Either<L,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Either<L,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(either(value1)).anyM(a -> either(value2.apply(a))).anyM(a -> b -> either(value3.apply(a, b)))
							.anyM(a -> b -> c -> either(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <L,T1, T2, T3, R1, R2, R3, R> Either<L,R> each4(Either<L,? extends T1> value1,
				Function<? super T1, ? extends Either<L,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Either<L,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Either<L,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(either(value1)).anyM(a -> either(value2.apply(a)))
					.anyM(a -> b -> either(value3.apply(a, b))).anyM(a -> b -> c -> either(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <L,T1, T2, R1, R2, R> Either<L,R> each3(Either<L,? extends T1> value1,
				Function<? super T1, ? extends Either<L,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Either<L,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(either(value1)).anyM(a -> either(value2.apply(a)))
					.anyM(a -> b -> either(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <L,T1, T2, R1, R2, R> Either<L,R> each3(Either<L,? extends T1> value1,
				Function<? super T1, ? extends Either<L,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Either<L,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.anyM(either(value1)).anyM(a -> either(value2.apply(a))).anyM(a -> b -> either(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <L,T, R1, R> Either<L,R> each2(Either<L,? extends T> value1, Function<? super T, Either<L,R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(either(value1)).anyM(a -> either(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <L,T, R1, R> Either<L,R> each2(Either<L,? extends T> value1, Function<? super T, ? extends Either<L,R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(either(value1)).anyM(a -> either(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	public interface ForReader {

		static <A,T1, T2, T3, R1, R2, R3, R> Reader<A,R> each4(Reader<A,? extends T1> value1,
				Function<? super T1, ? extends Reader<A,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Reader<A,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Reader<A,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(reader(value1)).anyM(a -> reader(value2.apply(a))).anyM(a -> b -> reader(value3.apply(a, b)))
							.anyM(a -> b -> c -> reader(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <A,T1, T2, T3, R1, R2, R3, R> Reader<A,R> each4(Reader<A,? extends T1> value1,
				Function<? super T1, ? extends Reader<A,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Reader<A,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Reader<A,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(reader(value1)).anyM(a -> reader(value2.apply(a)))
					.anyM(a -> b -> reader(value3.apply(a, b))).anyM(a -> b -> c -> reader(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <A,T1, T2, R1, R2, R> Reader<A,R> each3(Reader<A,? extends T1> value1,
				Function<? super T1, ? extends Reader<A,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Reader<A,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(reader(value1)).anyM(a -> reader(value2.apply(a)))
					.anyM(a -> b -> reader(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <A,T1, T2, R1, R2, R> Reader<A,R> each3(Reader<A,? extends T1> value1,
				Function<? super T1, ? extends Reader<A,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Reader<A,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.anyM(reader(value1)).anyM(a -> reader(value2.apply(a))).anyM(a -> b -> reader(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			
		}

		static <A,T, R1, R> Reader<A,R> each2(Reader<A,? extends T> value1, Function<? super T, Reader<A,R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(reader(value1)).anyM(a -> reader(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <A,T, R1, R> Reader<A,R> each2(Reader<A,? extends T> value1, Function<? super T, ? extends Reader<A,R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(reader(value1)).anyM(a -> reader(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
		}
		
	}
	public interface ForWriter {

		static <W,T1, T2, T3, R1, R2, R3, R> Writer<W,R> each4(Writer<W,? extends T1> value1,
				Function<? super T1, ? extends Writer<W,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Writer<W,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Writer<W,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(writer(value1)).anyM(a -> writer(value2.apply(a))).anyM(a -> b -> writer(value3.apply(a, b)))
							.anyM(a -> b -> c -> writer(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <W,T1, T2, T3, R1, R2, R3, R> Writer<W,R> each4(Writer<W,? extends T1> value1,
				Function<? super T1, ? extends Writer<W,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Writer<W,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Writer<W,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(writer(value1)).anyM(a -> writer(value2.apply(a)))
					.anyM(a -> b -> writer(value3.apply(a, b))).anyM(a -> b -> c -> writer(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <W,T1, T2, R1, R2, R> Writer<W,R> each3(Writer<W,? extends T1> value1,
				Function<? super T1, ? extends Writer<W,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Writer<W,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(writer(value1)).anyM(a -> writer(value2.apply(a)))
					.anyM(a -> b -> writer(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <W,T1, T2, R1, R2, R> Writer<W,R> each3(Writer<W,? extends T1> value1,
				Function<? super T1, ? extends Writer<W,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Writer<W,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.anyM(writer(value1)).anyM(a -> writer(value2.apply(a))).anyM(a -> b -> writer(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <W,T, R1, R> Writer<W,R> each2(Writer<W,? extends T> value1, Function<? super T, Writer<W,R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(writer(value1)).anyM(a -> writer(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <W,T, R1, R> Writer<W,R> each2(Writer<W,? extends T> value1, Function<? super T, ? extends Writer<W,R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			
			return AnyM.ofValue(For.anyM(writer(value1)).anyM(a -> writer(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	public interface ForValidation {

		static <E,T1, T2, T3, R1, R2, R3, R> Validation<E,R> each4(Validation<E,? extends T1> value1,
				Function<? super T1, ? extends Validation<E,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Validation<E,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Validation<E,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(validation(value1)).anyM(a -> validation(value2.apply(a))).anyM(a -> b -> validation(value3.apply(a, b)))
							.anyM(a -> b -> c -> validation(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <E,T1, T2, T3, R1, R2, R3, R> Validation<E,R> each4(Validation<E,? extends T1> value1,
				Function<? super T1, ? extends Validation<E,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Validation<E,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Validation<E,R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(validation(value1)).anyM(a -> validation(value2.apply(a)))
					.anyM(a -> b -> validation(value3.apply(a, b))).anyM(a -> b -> c -> validation(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <E,T1, T2, R1, R2, R> Validation<E,R> each3(Validation<E,? extends T1> value1,
				Function<? super T1, ? extends Validation<E,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Validation<E,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(validation(value1)).anyM(a -> validation(value2.apply(a)))
					.anyM(a -> b -> validation(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <E,T1, T2, R1, R2, R> Validation<E,R> each3(Validation<E,? extends T1> value1,
				Function<? super T1, ? extends Validation<E,R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Validation<E,R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.anyM(validation(value1)).anyM(a -> validation(value2.apply(a))).anyM(a -> b -> validation(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <E,T, R1, R> Validation<E,R> each2(Validation<E,? extends T> value1, Function<? super T, Validation<E,R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(validation(value1)).anyM(a -> validation(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <E,T, R1, R> Validation<E,R> each2(Validation<E,? extends T> value1, Function<? super T, ? extends Validation<E,R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(validation(value1)).anyM(a -> validation(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	public interface ForIO {

		static <T1, T2, T3, R1, R2, R3, R> IO<R> each4(IO<? extends T1> value1,
				Function<? super T1, ? extends IO<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends IO<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends IO<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(io(value1)).anyM(a -> io(value2.apply(a))).anyM(a -> b -> io(value3.apply(a, b)))
							.anyM(a -> b -> c -> io(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> IO<R> each4(IO<? extends T1> value1,
				Function<? super T1, ? extends IO<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends IO<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends IO<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(io(value1)).anyM(a -> io(value2.apply(a)))
					.anyM(a -> b -> io(value3.apply(a, b))).anyM(a -> b -> c -> io(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> IO<R> each3(IO<? extends T1> value1,
				Function<? super T1, ? extends IO<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends IO<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(io(value1)).anyM(a -> io(value2.apply(a)))
					.anyM(a -> b -> io(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <T1, T2, R1, R2, R> IO<R> each3(IO<? extends T1> value1,
				Function<? super T1, ? extends IO<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends IO<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.anyM(io(value1)).anyM(a -> io(value2.apply(a))).anyM(a -> b -> io(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> IO<R> each2(IO<? extends T> value1, Function<? super T, IO<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(io(value1)).anyM(a -> io(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> IO<R> each2(IO<? extends T> value1, Function<? super T, ? extends IO<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(io(value1)).anyM(a -> io(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	
	public interface ForTrampoline {

		static <T1, T2, T3, R1, R2, R3, R> Trampoline<R> each4(Trampoline<? extends T1> value1,
				Function<? super T1, ? extends Trampoline<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Trampoline<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Trampoline<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(trampoline(value1)).anyM(a -> trampoline(value2.apply(a))).anyM(a -> b -> trampoline(value3.apply(a, b)))
							.anyM(a -> b -> c -> trampoline(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> Trampoline<R> each4(Trampoline<? extends T1> value1,
				Function<? super T1, ? extends Trampoline<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Trampoline<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Trampoline<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(trampoline(value1)).anyM(a -> trampoline(value2.apply(a)))
					.anyM(a -> b -> trampoline(value3.apply(a, b))).anyM(a -> b -> c -> trampoline(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> Trampoline<R> each3(Trampoline<? extends T1> value1,
				Function<? super T1, ? extends Trampoline<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Trampoline<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(trampoline(value1)).anyM(a -> trampoline(value2.apply(a)))
					.anyM(a -> b -> trampoline(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <T1, T2, R1, R2, R> Trampoline<R> each3(Trampoline<? extends T1> value1,
				Function<? super T1, ? extends Trampoline<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Trampoline<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofValue(For.anyM(trampoline(value1)).anyM(a -> trampoline(value2.apply(a))).anyM(a -> b -> trampoline(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Trampoline<R> each2(Trampoline<? extends T> value1, Function<? super T, Trampoline<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(trampoline(value1)).anyM(a -> trampoline(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Trampoline<R> each2(Trampoline<? extends T> value1, Function<? super T, ? extends Trampoline<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofValue(For.anyM(trampoline(value1)).anyM(a -> trampoline(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	public interface ForList {

		static <T1, T2, T3, R1, R2, R3, R> List<R> each4(List<? extends T1> value1,
				Function<? super T1, ? extends List<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends List<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends List<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(list(value1)).anyM(a -> list(value2.apply(a))).anyM(a -> b -> list(value3.apply(a, b)))
							.anyM(a -> b -> c -> list(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> List<R> each4(List<? extends T1> value1,
				Function<? super T1, ? extends List<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends List<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends List<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(list(value1)).anyM(a -> list(value2.apply(a)))
					.anyM(a -> b -> list(value3.apply(a, b))).anyM(a -> b -> c -> list(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> List<R> each3(List<? extends T1> value1,
				Function<? super T1, ? extends List<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends List<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(list(value1)).anyM(a -> list(value2.apply(a)))
					.anyM(a -> b -> list(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <T1, T2, R1, R2, R> List<R> each3(List<? extends T1> value1,
				Function<? super T1, ? extends List<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends List<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofSeq(For.anyM(list(value1)).anyM(a -> list(value2.apply(a))).anyM(a -> b -> list(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> List<R> each2(List<? extends T> value1, Function<? super T, List<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(list(value1)).anyM(a -> list(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> List<R> each2(List<? extends T> value1, Function<? super T, ? extends List<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(list(value1)).anyM(a -> list(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	public interface ForStream {

		static <T1, T2, T3, R1, R2, R3, R> Stream<R> each4(Stream<? extends T1> value1,
				Function<? super T1, ? extends Stream<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Stream<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Stream<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(stream(value1)).anyM(a -> stream(value2.apply(a))).anyM(a -> b -> stream(value3.apply(a, b)))
							.anyM(a -> b -> c -> stream(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> Stream<R> each4(Stream<? extends T1> value1,
				Function<? super T1, ? extends Stream<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Stream<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends Stream<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(stream(value1)).anyM(a -> stream(value2.apply(a)))
					.anyM(a -> b -> stream(value3.apply(a, b))).anyM(a -> b -> c -> stream(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> Stream<R> each3(Stream<? extends T1> value1,
				Function<? super T1, ? extends Stream<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Stream<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(stream(value1)).anyM(a -> stream(value2.apply(a)))
					.anyM(a -> b -> stream(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <T1, T2, R1, R2, R> Stream<R> each3(Stream<? extends T1> value1,
				Function<? super T1, ? extends Stream<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends Stream<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofSeq(For.anyM(stream(value1)).anyM(a -> stream(value2.apply(a))).anyM(a -> b -> stream(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Stream<R> each2(Stream<? extends T> value1, Function<? super T, Stream<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(stream(value1)).anyM(a -> stream(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> Stream<R> each2(Stream<? extends T> value1, Function<? super T, ? extends Stream<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(stream(value1)).anyM(a -> stream(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
	public interface ForIterableW {

		static <T1, T2, T3, R1, R2, R3, R> IterableW<R> each4(IterableW<? extends T1> value1,
				Function<? super T1, ? extends IterableW<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends IterableW<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends IterableW<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(iterableW(value1)).anyM(a -> iterableW(value2.apply(a))).anyM(a -> b -> iterableW(value3.apply(a, b)))
							.anyM(a -> b -> c -> iterableW(value4.apply(a, b, c))).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, T3, R1, R2, R3, R> IterableW<R> each4(IterableW<? extends T1> value1,
				Function<? super T1, ? extends IterableW<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends IterableW<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends IterableW<R3>> value4,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, Boolean> filterFunction,
				QuadFunction<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(iterableW(value1)).anyM(a -> iterableW(value2.apply(a)))
					.anyM(a -> b -> iterableW(value3.apply(a, b))).anyM(a -> b -> c -> iterableW(value4.apply(a, b, c)))
					.filter(a -> b -> c -> d -> filterFunction.apply(a, b, c, d)).yield4(yieldingFunction).unwrap())
					.unwrap();

		}

		static <T1, T2, R1, R2, R> IterableW<R> each3(IterableW<? extends T1> value1,
				Function<? super T1, ? extends IterableW<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends IterableW<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(iterableW(value1)).anyM(a -> iterableW(value2.apply(a)))
					.anyM(a -> b -> iterableW(value3.apply(a, b))).yield3(yieldingFunction).unwrap()).unwrap();
			

		}

		static <T1, T2, R1, R2, R> IterableW<R> each3(IterableW<? extends T1> value1,
				Function<? super T1, ? extends IterableW<R1>> value2,
				BiFunction<? super T1, ? super R1, ? extends IterableW<R2>> value3,
				TriFunction<? super T1, ? super R1, ? super R2, Boolean> filterFunction,
				TriFunction<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

			return AnyM
					.ofSeq(For.anyM(iterableW(value1)).anyM(a -> iterableW(value2.apply(a))).anyM(a -> b -> iterableW(value3.apply(a, b)))
							.filter(a -> b -> c -> filterFunction.apply(a, b, c)).yield3(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> IterableW<R> each2(IterableW<? extends T> value1, Function<? super T, IterableW<R1>> value2,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(iterableW(value1)).anyM(a -> iterableW(value2.apply(a))).yield2(yieldingFunction).unwrap())
					.unwrap();
			

		}

		static <T, R1, R> IterableW<R> each2(IterableW<? extends T> value1, Function<? super T, ? extends IterableW<R1>> value2,
				BiFunction<? super T, ? super R1, Boolean> filterFunction,
				BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

			return AnyM.ofSeq(For.anyM(iterableW(value1)).anyM(a -> iterableW(value2.apply(a)))
					.filter(a -> b -> filterFunction.apply(a, b)).yield2(yieldingFunction).unwrap()).unwrap();
			

		}
	}
}
