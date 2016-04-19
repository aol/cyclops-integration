package com.aol.cyclops.functionaljava.comprehenders;

import java.util.function.Function;

import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.extensability.ValueComprehender;

import fj.control.Trampoline;
import fj.data.Option;

public class TrampolineComprehender implements ValueComprehender<Trampoline>{
	
	
	@Override
	public Object resolveForCrossTypeFlatMap(Comprehender comp, Trampoline apply) {
		return comp.of(apply.run());
	}

	@Override
	public Object map(Trampoline t, Function fn) {
		return t.map(r->fn.apply(r));
	}

	@Override
	public Object flatMap(Trampoline t, Function fn) {
		return t.bind(r->fn.apply(r));
	}

	@Override
	public Trampoline of(Object o) {
		return Trampoline.pure(o);
	}

	@Override
	public Trampoline empty() {
		return Trampoline.pure(Option.none());
	}

	@Override
	public Class getTargetClass() {
		return Trampoline.class;
	}

}
