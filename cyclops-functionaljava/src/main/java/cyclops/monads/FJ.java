package cyclops.monads;

import java.util.function.Supplier;


import com.oath.cyclops.types.anyM.AnyMSeq;
import com.oath.cyclops.types.anyM.AnyMValue;
import cyclops.control.Maybe;
import fj.P1;
import fj.data.Either;
import fj.data.IterableW;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;

/**
 * FunctionalJava Cyclops integration point
 *
 * @author johnmcclean
 *
 */
public interface FJ {


    public static <T> Maybe<T> maybe(Option<T> opt){
        return opt.isNone() ? Maybe.nothing() : Maybe.just(opt.some());
    }
    /**
     * Methods for making working with FJ's Trampoline a little more Java8 friendly
     *
     */
    public static class Trampoline8 {
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
        public static <T> fj.control.Trampoline<T> suspend(Supplier<fj.control.Trampoline<T>> s) {
            return fj.control.Trampoline.suspend(new P1<fj.control.Trampoline<T>>() {

                @Override
                public fj.control.Trampoline<T> _1() {
                    return s.get();
                }

            });
        }
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
    public static <T> AnyMValue<FJWitness.validation,T> validation(Validation<?, T> validationM) {
        return AnyM.ofValue(validationM, FJWitness.validation.INSTANCE);
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
    public static <T> AnyMSeq<FJWitness.iterableW,T> iterableW(IterableW<T> iterableWM) {
        return AnyM.ofSeq(iterableWM, FJWitness.iterableW.INSTANCE);
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
    public static <T> AnyMValue<FJWitness.either,T> either(Either<?, T> eitherM) {
        return AnyM.ofValue(eitherM, FJWitness.either.INSTANCE);
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
    public static <T> AnyMValue<FJWitness.option,T> option(Option<T> optionM) {
        return AnyM.ofValue(optionM, FJWitness.option.INSTANCE);
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
    public static <T> AnyMSeq<FJWitness.stream,T> stream(Stream<T> streamM) {
        return AnyM.ofSeq(streamM, FJWitness.stream.INSTANCE);
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
    public static <T> AnyMSeq<FJWitness.list,T> list(List<T> listM) {
        return AnyM.ofSeq(listM, FJWitness.list.INSTANCE);
    }

}
