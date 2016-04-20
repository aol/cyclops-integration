package com.aol.cyclops.rx;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.aol.cyclops.control.ReactiveSeq;
import com.aol.cyclops.data.collections.extensions.standard.ListX;

import rx.Observable;

public class RxTest {

	@Test
	public void observable() {
		assertThat(RxCyclops.observable(Observable.just(1,2,3)).toListX(),equalTo(ListX.of(1,2,3)));
	}
	@Test
	public void observableFlatMap() {
		assertThat(RxCyclops.observable(Observable.just(1,2,3)).flatMap(a->RxCyclops.observable(Observable.just(a+10))).toListX(),equalTo(ListX.of(11,12,13)));
	}
	
	@Test
	public void observableT() {
		assertThat(RxCyclops.observableT(ReactiveSeq.of(Observable.just(1,2,3),Observable.just(10,20,30))).toListX(),equalTo(ListX.of(1,2,3,10,20,30)));
	}
	

	@Test
	public void observableComp(){
		Observable<Integer> result = RxCyclops.ForObservable.each2(Observable.just(10,20),a->Observable.<Integer>just(a+10),(a,b)->a+b);
		
		assertThat(result.toList().toBlocking().single(),equalTo(ListX.of(30,50)));
	}
	
}
