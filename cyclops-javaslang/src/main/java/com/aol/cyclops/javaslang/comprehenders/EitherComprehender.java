package com.aol.cyclops.javaslang.comprehenders;

import java.util.function.Function;
import java.util.function.Predicate;

import javaslang.control.Either;
import javaslang.control.Option;

import com.aol.cyclops.lambda.api.Comprehender;

public class EitherComprehender implements Comprehender<Either> {

	public Object filter(Either t, Predicate p){
		return t.right().filter(x->p.test(x));
	}
	@Override
	public Object map(Either t, Function fn) {
		return t.right().map(e->fn.apply(e));
	}

	@Override
	public Object flatMap(Either t, Function fn) {
		return t.right().flatMap(e->fn.apply(e));
	}

	@Override
	public Either of(Object o) {
		return Either.right(o);
	}

	@Override
	public Either empty() {
		return Either.right(Option.none());
	}

	@Override
	public Class getTargetClass() {
		return Either.class;
	}
	public Object resolveForCrossTypeFlatMap(Comprehender comp,Either apply){
		if(apply.isRight())
			return comp.of(apply.right().get());
		return comp.empty();
	}
}
