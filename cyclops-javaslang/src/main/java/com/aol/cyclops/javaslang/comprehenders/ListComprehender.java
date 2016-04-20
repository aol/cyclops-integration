package com.aol.cyclops.javaslang.comprehenders;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.BaseStream;

import com.aol.cyclops.types.extensability.Comprehender;

import javaslang.collection.List;
import javaslang.collection.Vector;

public class ListComprehender implements Comprehender<List> {

	@Override
	public Object map(List t, Function fn) {
		return t.map(s -> fn.apply(s));
	}
	@Override
	public Object executeflatMap(List t, Function fn){
		return flatMap(t,input -> unwrapOtherMonadTypes(this,fn.apply(input)));
	}
	@Override
	public Object flatMap(List t, Function fn) {
		return t.flatMap(s->fn.apply(s));
	}

	@Override
	public List of(Object o) {
		return List.of(o);
	}

	@Override
	public List empty() {
		return List.empty();
	}

	@Override
	public Class getTargetClass() {
		return List.class;
	}
	static List unwrapOtherMonadTypes(Comprehender<List> comp,Object apply){
		if (comp.instanceOfT(apply))
			return (List) apply;
		if(apply instanceof java.util.stream.Stream)
			return List.of( ((java.util.stream.Stream)apply).iterator());
		if(apply instanceof Iterable)
			return List.ofAll( ((Iterable)apply));
		
		if(apply instanceof Collection){
			return List.ofAll((Collection)apply);
		}
		final Object finalApply = apply;
		if(apply instanceof BaseStream){
			return List.ofAll( () -> ((BaseStream)finalApply).iterator());
					
		}
		return Comprehender.unwrapOtherMonadTypes(comp,apply);
		
	}
	@Override
	public Object resolveForCrossTypeFlatMap(Comprehender comp, List apply) {
		return comp.fromIterator(apply.iterator());
	}
	@Override
	public List fromIterator(Iterator o) {
		return  List.ofAll(()->o);
	}
}
