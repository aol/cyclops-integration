package com.aol.cyclops.functionaljava.comprehenders;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.BaseStream;

import com.aol.cyclops.types.extensability.Comprehender;

import fj.data.List;


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
		return t.bind(s->fn.apply(s));
	}

	@Override
	public List of(Object o) {
		return List.cons(o,List.nil());
	}

	@Override
	public List empty() {
		return List.nil();
	}

	@Override
	public Class getTargetClass() {
		return List.class;
	}
	static List unwrapOtherMonadTypes(Comprehender<List> comp,Object apply){
		if(apply instanceof java.util.stream.Stream)
			return List.iteratorList( ((java.util.stream.Stream)apply).iterator());
		if(apply instanceof Iterable)
			return List.iteratorList( ((Iterable)apply).iterator());
		
		if(apply instanceof Collection){
			return List.iterableList((Collection)apply);
		}
		final Object finalApply = apply;
		if(apply instanceof BaseStream){
			return List.iterableList( () -> ((BaseStream)finalApply).iterator());
					
		}
		
		return Comprehender.unwrapOtherMonadTypes(comp,apply);
		
	}
	@Override
	public List fromIterator(Iterator o) {
		return List.iteratorList(o);
	}

}
