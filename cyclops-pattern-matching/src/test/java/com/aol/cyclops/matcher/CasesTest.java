package com.aol.cyclops.matcher;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import lombok.val;

import org.junit.Test;

import com.aol.cyclops.objects.Decomposable;


public class CasesTest {

	
	@Test
	public void ofPStack() {
		Cases cases = Cases.ofList((List)Arrays.asList(Case.of(input->true,input->"hello")));
		assertThat(cases.size(),is(1));
	}

	@Test
	public void ofVarargs() {
		val cases = Cases.of(Case.of(input->true,input->"hello"));
		assertThat(cases.size(),is(1));
	}

	@Test
	public void testZip() {
		Stream<Predicate<Integer>> predicates = Stream.of(i->true,i->false);
		Stream<Function<Integer,Integer>> functions = Stream.of(i->i+2,i->i*100);
		
		val cases = Cases.zip(predicates, functions);
		assertThat(cases.size(),is(2));
		assertThat(cases.match(100).get(),is(102));
	}

	@Test
	public void testUnzip() {
		val cases = Cases.of(Case.of(input->true,input->"hello"));
		
		val unzipped = cases.unzip();
		assertTrue(unzipped.v1.map(p->p.test(10)).allMatch(v->v));
		assertTrue(unzipped.v2.map(fn->fn.apply(10)).allMatch(v->"hello".equals(v)));
	}

	int found;
	@Test
	public void testForEach() {
		found = 0;
		Cases.of(Case.of(input->true,input->"hello")).forEach( cse -> found++);
		assertTrue(found==1);
	}

	@Test
	public void testSequential() {
		Set<Long> threads = new HashSet<>();
		val case1 = Case.of(input->true,input->{ threads.add(Thread.currentThread().getId());return "hello";});
		Cases.of(case1,case1,case1,case1).sequential().match(10);
		assertThat(threads.size(),is(1));
	}

	@Test @Ignore
	public void testParallel() {
		Set<Long> threads = new HashSet<>();
		val case1 = Case.of(input->true,input->{ threads.add(Thread.currentThread().getId());return "hello";});
		Cases.of(case1,case1,case1,case1).parallel().match(10);
		assertThat(threads.size(),greaterThan(1));
	}

	@Test
	public void testMerge() {
		val cases1 = Cases.of(Case.of(input->true,input->"hello"));
		val cases2 = Cases.of(Case.of(input->true,input->"hello"));
		
		val cases3 = cases1.merge(cases2);
		
		assertThat(cases3.size(),is(cases1.size()+cases2.size()));
	}

	@Test
	public void testFilter() {
		val cases = Cases.of(Case.of(input->true,input->"hello"),Case.of(input->false,input->"second"))
									.filter(p-> p.getPredicate().test(10));
		assertThat(cases.size(),is(1));
	}

	@Test
	public void testFilterPredicate() {
		val cases = Cases.of(Case.of(input->true,input->"hello"),Case.of(input->false,input->"second"))
				.filterPredicate(p-> p.test(10));
		assertThat(cases.size(),is(1));
	}

	@Test
	public void testFilterFunction() {
		val cases = Cases.of(Case.of(input->true,input->"hello"),Case.of(input->false,input->"second"))
				.filterFunction(fn-> fn.apply(10).equals("second"));
		assertThat(cases.size(),is(1));
	}

	@Test
	public void testMapPredicate() {
		List results = Cases.of(Case.of(input->true,input->"hello"),Case.of(input->false,input->"second"))
						.mapPredicate(p->input->true).matchMany(10).collect(Collectors.toList());
		
		assertThat(results.size(),is(2));
	}

	@Test
	public void testMapFunction() {
		List<String> results = Cases.of(Case.of(input->true,input->"hello"),Case.of(input->true,input->"second"))
				.mapFunction(fn->input->"prefix_"+fn.apply(input)).<String>matchMany(10).collect(Collectors.toList());
		
		assertThat(results.size(),is(2));
		assertTrue(results.stream().allMatch(s->s.startsWith("prefix_")));
		assertTrue(results.stream().anyMatch(s->s.startsWith("prefix_hello")));
		assertTrue(results.stream().anyMatch(s->s.startsWith("prefix_second")));
	}

	@Test
	public void testMap() {
		List<String> results = Cases.of(Case.of(input->true,input->"hello"),Case.of(input->false,input->"second"))
				.map(cse->Case.of(t->true,input->"prefix_"+cse.getAction().apply(input))).<String>matchMany(10).collect(Collectors.toList());
		
		assertThat(results.size(),is(2));
		assertTrue(results.stream().allMatch(s->s.startsWith("prefix_")));
		assertTrue(results.stream().anyMatch(s->s.startsWith("prefix_hello")));
		assertTrue(results.stream().anyMatch(s->s.startsWith("prefix_second")));
	}

	@AllArgsConstructor
	@Getter
	static class Person{
		String name;
		int age;
	}
	@Test
	public void flatMap(){
		Case<Object,Integer,Function<Object,Integer>> cse = Case.of(input-> input instanceof Person, input -> ((Person)input).getAge());
		
		 
		assertThat(Cases.of(cse).flatMap(c -> Cases.ofList((List)Arrays.asList(c.andThen(Case.of( age-> age<18,s->"minor")),
										c.andThen(Case.of( age->age>=18,s->"adult"))))).match(new Person("bob",21)).get(),is("adult"));
	}
	@Test
	public void testFlatMapAll() {
		val cases = Cases.of(Case.of(input->true,input->"hello"),Case.of(input->false,input->"second"))
						.flatMapAll(input-> (Cases)Cases.ofPStack(input.plus(Case.of(in->true,in->"new"))));
		
		assertThat(cases.size(),is(3));
	}

	@Test
	public void testAppend() {
		Cases<Integer,String,Function<Integer,String>> cases1 = Cases.of(Case.of((Integer input)->10==input,input->"hello"),Case.of(input->11==input,input->"world"));
		Case<Integer,String,Function<Integer,String>> caze = Case.of((Integer input)->11==input,input->"hello");
		
		val cases3 = cases1.append(1,caze);
		
		assertThat(cases3.size(),is(3));
		
		assertThat(cases3.match(11).get(),is("hello"));
	}

	

	@Test
	public void testAsUnwrappedFunction() {
		assertThat(Cases.of(Case.of(input->true,input->"hello")).asUnwrappedFunction().apply(10),is("hello"));
	}

	@Test
	public void testAsStreamFunction() {
		assertThat(Cases.of(Case.of(input->true,input->"hello")).asStreamFunction().apply(10).findFirst().get(),is("hello"));
	}

	@Test
	public void testApply() {
		assertThat(Cases.of(Case.of(input->true,input->"hello")).apply(10).get(),is("hello"));
	}

	@Test
	public void testMatchManyFromStream() {
		List<String> results = Cases.of(Case.of((Integer input)->10==input,input->"hello"),
											Case.of(input->11==input,input->"world"))
										.<String>matchManyFromStream(Stream.of(1,10,11))
										.collect(Collectors.toList());
		
		assertThat(results.size(),is(2));
		assertThat(results,hasItem("hello"));
		assertThat(results,hasItem("world"));
		
	}

	@Test
	public void testMatchManyFromStreamAsync() {
		List<String> results = Cases.of(Case.of((Integer input)->10==input,input->"hello"),
				Case.of(input->11==input,input->"world"))
			.<String>matchManyFromStreamAsync(ForkJoinPool.commonPool(),Stream.of(1,10,11))
			.join()
			.collect(Collectors.toList());

		assertThat(results.size(),is(2));
		assertThat(results,hasItem("hello"));
		assertThat(results,hasItem("world"));
	}

	@Test
	public void testMatchMany() {
		List<String> results =  Cases.of(Case.of((Integer input)->10==input,input->"hello"),
				Case.of(input->11==input,input->"world"),
				Case.of(input->10==input,input->"woo!"))
			.<String>matchMany(10).collect(Collectors.toList());
		
		assertThat(results.size(),is(2));
		assertThat(results,hasItem("hello"));
		assertThat(results,hasItem("woo!"));
	}

	@Test
	public void testMatchManyAsync() {
		List<String> results =  Cases.of(Case.of((Integer input)->10==input,input->"hello"),
				Case.of(input->11==input,input->"world"),
				Case.of(input->10==input,input->"woo!"))
			.<String>matchManyAsync(ForkJoinPool.commonPool(),10).join().collect(Collectors.toList());
		
		assertThat(results.size(),is(2));
		assertThat(results,hasItem("hello"));
		assertThat(results,hasItem("woo!"));
	}

	@Test
	public void testMatchFromStream() {
		List<String> results = Cases
				.of(Case.of((Integer input) -> 10 == input, input -> "hello"),
						Case.of((Integer input) -> 10 == input, input -> "ignored"),
						Case.of(input -> 11 == input, input -> "world"))
				.<String> matchFromStream(Stream.of(1, 11, 10)).collect(Collectors.toList());

		assertThat(results.size(), is(2));
		assertThat(results, hasItem("hello"));
		assertThat(results, hasItem("world"));
	}

	@Test
	public void testMatchFromStreamAsync() {
		List<String> results = Cases
				.of(Case.of((Integer input) -> 10 == input, input -> "hello"),
						Case.of((Integer input) -> 10 == input, input -> "ignored"),
						Case.of(input -> 11 == input, input -> "world"))
				.<String> matchFromStreamAsync(ForkJoinPool.commonPool(),Stream.of(1, 11, 10)).join().collect(Collectors.toList());

		assertThat(results.size(), is(2));
		assertThat(results, hasItem("hello"));
		assertThat(results, hasItem("world"));
	}

	@Test
	public void testMatchObjectArray() {
		assertThat(Cases.of(Case.of((List<Integer> input) -> input.size()==3, input -> "hello"),
				Case.of((List<Integer> input) -> input.size()==2, input -> "ignored"),
				Case.of((List<Integer> input) -> input.size()==1, input -> "world")).match(1,2,3).get(),is("hello"));
	}

	@Test
	public void testMatchAsync() {
		assertThat(Cases.of(Case.of((List<Integer> input) -> input.size()==3, input -> "hello"),
				Case.of((List<Integer> input) -> input.size()==2, input -> "ignored"),
				Case.of((List<Integer> input) -> input.size()==1, input -> "world"))
				.matchAsync(ForkJoinPool.commonPool(),1,2,3).join().get(),is("hello"));

	}

	@Test
	public void testUnapply() {
		assertThat(Cases.of(Case.of((List input) -> input.size()==3, input -> "hello"),
				Case.of((List input) -> input.size()==2, input -> "ignored"),
				Case.of((List input) -> input.size()==1, input -> "world"))
				.unapply(new MyClass(1,"hello")).get(),is("ignored"));
	}
	@AllArgsConstructor
	static class MyClass implements Decomposable{
		int value;
		String name;
	}
	
	

	

}
