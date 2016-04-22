package com.aol.cyclops.functionaljava.comprehenders;

import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

import fj.data.Either;
import fj.data.Either.RightProjection;
import fj.data.Option;

public class RightProjectionComprehender implements ValueComprehender<RightProjection>{
	@Override
	public Object filter(RightProjection t, Predicate p){
		return t.filter(x->p.test(x));
	}
	@Override
	public Object map(RightProjection t, Function fn) {
		return t.map(x ->fn.apply(x));
	}

	@Override
	public Object flatMap(RightProjection t, Function fn) {
		return t.bind(x->fn.apply(x));
	}

	@Override
	public RightProjection of(Object o) {
		return  Either.right(o).right();
	}

	@Override
	public RightProjection empty() {
		return  Either.right(Option.none()).right();
	}

	@Override
	public Class getTargetClass() {
		return RightProjection.class;
	}
	public Object resolveForCrossTypeFlatMap(Comprehender comp,RightProjection apply){
		
		Option present =  apply.toOption();
		if(present.isSome())
			return comp.of(present.some());
		else
			return comp.empty();
		
	}
}
