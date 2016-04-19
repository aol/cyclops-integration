
package com.aol.cyclops.guava;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.types.anyM.AnyMValue;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

public class Guava {
	
	public static <T> Maybe<T> asMaybe(Optional<T> option){
		return  option.isPresent() ? Maybe.just(option.get()) : Maybe.none();
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
	public static <T> AnyMValue<T> optional(Optional<T> optionM){
		return  AnyM.ofValue(optionM);
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
	public static <T> AnyMValue<T> fluentIterable(FluentIterable<T> streamM){
		return  AnyM.ofValue(streamM);
	}
}
