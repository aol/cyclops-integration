package com.aol.cyclops.reactor;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.aol.cyclops.data.collections.extensions.standard.ListX;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactorTest {

	@Test
	public void flux() {
		assertThat(Reactor.flux(Flux.just(1,2,3)).toListX(),equalTo(ListX.of(1,2,3)));
	}
	@Test
	public void mono() {
		assertThat(Reactor.mono(Mono.just(1)).toListX(),equalTo(ListX.of(1)));
	}
	@Test
	public void fluxT() {
		assertThat(Reactor.fluxT(Flux.just(Flux.just(1,2,3),Flux.just(10,20,30))).toListX(),equalTo(ListX.of(1,2,3,10,20,30)));
	}
	@Test
	public void monoT() {
		assertThat(Reactor.monoT(Flux.just(Mono.just(1),Mono.just(10))).toListX(),equalTo(ListX.of(1,10)));
	}

	@Test
	public void fluxComp(){
		Flux<Integer> result = Reactor.ForFlux.each2(Flux.just(10,20),a->Flux.<Integer>just(a+10),(a,b)->a+b);
		assertThat(result.toList().get(),equalTo(ListX.of(30,50)));
	}
	@Test
	public void monoComp(){
		Mono<Integer> result = Reactor.ForMono.each2(Mono.just(10),a->Mono.<Integer>just(a+10),(a,b)->a+b);
		assertThat(result.get(),equalTo(30));
	}
}
