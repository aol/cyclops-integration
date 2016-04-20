package com.aol.cyclops.javaslang.comprehenders;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

import fj.data.Option;
import javaslang.control.Either;
import javaslang.control.Either.LeftProjection;

public class LeftProjectionComprehender implements ValueComprehender<LeftProjection>{

	public Object filter(LeftProjection t, Predicate p){
		return t.filter(x->p.test(x));
	}
	@Override
	public Object map(LeftProjection t, Function fn) {
		return t.map(x ->fn.apply(x));
	}
	
	@Override
	public Object flatMap(LeftProjection t, Function fn) {
		return t.flatMap(x->fn.apply(x));
	}

	@Override
	public LeftProjection of(Object o) {
		return Either.left(o).left();
	}

	@Override
	public LeftProjection empty() {
		return Either.left(Option.none()).left();
	}

	@Override
	public Class getTargetClass() {
		return LeftProjection.class;
	}

	public Object resolveForCrossTypeFlatMap(Comprehender comp,LeftProjection apply){
		Optional present =  apply.toJavaOptional();
		if(present.isPresent())
			return comp.of(apply.get());
		else
			return comp.empty();
		
	}
	
}
