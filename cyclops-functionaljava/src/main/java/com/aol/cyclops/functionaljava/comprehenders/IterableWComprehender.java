package com.aol.cyclops.functionaljava.comprehenders;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.BaseStream;

import com.aol.cyclops.internal.comprehensions.comprehenders.StreamableComprehender;
import com.aol.cyclops.types.extensability.Comprehender;
import com.aol.cyclops.util.stream.StreamUtils;

import fj.data.IterableW;
import fj.data.Stream;


public class IterableWComprehender implements Comprehender<IterableW> {

	@Override
	public Object map(IterableW t, Function fn) {
		return t.map(s -> fn.apply(s));
	}
	@Override
	public Object executeflatMap(IterableW t, Function fn){
		return flatMap(t,input -> unwrapOtherMonadTypes(this,fn.apply(input)));
	}
	@Override
	public Object flatMap(IterableW t, Function fn) {
		return t.bind(s->fn.apply(s));
	}

	@Override
	public IterableW of(Object o) {
		
		return IterableW.wrap( Arrays.asList(o));
	}

	@Override
	public IterableW empty() {
		return IterableW.wrap(() -> new Iterator(){

			@Override
			public boolean hasNext() {
				
				return false;
			}

			@Override
			public Object next() {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
	}

	
	
	@Override
	public Class getTargetClass() {
		return IterableW.class;
	}
	
	
	@Override
	public Object resolveForCrossTypeFlatMap(Comprehender comp, IterableW apply) {
		
			if(comp instanceof com.aol.cyclops.internal.comprehensions.comprehenders.StreamComprehender || comp instanceof StreamableComprehender){
				return StreamUtils.stream(apply);
			}
			
		
		return Comprehender.super.resolveForCrossTypeFlatMap(comp, apply);
	}
	static IterableW unwrapOtherMonadTypes(Comprehender<IterableW> comp,final Object apply){
		if(apply instanceof java.util.stream.Stream)
			return IterableW.wrap( ()-> ((java.util.stream.Stream)apply).iterator());
		if(apply instanceof Iterable)
			return IterableW.wrap( ((Iterable)apply));
		
		final Object finalApply = apply;
		if(apply instanceof BaseStream){
			return IterableW.wrap( () -> ((BaseStream)finalApply).iterator());
					
		}
		
		
		return Comprehender.unwrapOtherMonadTypes(comp,apply);
		
	}
	@Override
	public IterableW fromIterator(Iterator o) {
		return IterableW.wrap(()->o);
	}

}
