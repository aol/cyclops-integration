package com.aol.cyclops.rx.comprehenders;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import com.aol.cyclops.rx.transformer.ObservableTValue;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.types.mixins.Printable;

import rx.Observable;



public class ObservableTValueComprehender implements Comprehender<ObservableTValue>, Printable{
	
	@Override
	public Object resolveForCrossTypeFlatMap(Comprehender comp, ObservableTValue apply) {
	  
		return apply.isStreamPresent() ? comp.of(apply.get()) : comp.empty();
	}
	@Override
    public Object filter(ObservableTValue t, Predicate p){
        return t.filter(p);
    }
	@Override
	public Object map(ObservableTValue t, Function fn) {
		return t.map(r->fn.apply(r));
	}

	@Override
	public Object flatMap(ObservableTValue t, Function fn) {
		return t.flatMapT(r->fn.apply(r));
	}

	@Override
	public ObservableTValue of(Object o) {
		return ObservableTValue.of(Observable.just(o));
	}

	@Override
	public ObservableTValue empty() {
		return ObservableTValue.emptyOptional();
	}

	@Override
	public Class getTargetClass() {
		return ObservableTValue.class;
	}
    @Override
    public ObservableTValue fromIterator(Iterator o) {
        return ObservableTValue.of(Observable.from(()->o));
    }
	

}

