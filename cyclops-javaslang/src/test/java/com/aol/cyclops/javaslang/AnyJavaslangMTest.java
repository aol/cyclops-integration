package com.aol.cyclops.javaslang;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import com.aol.cyclops.control.AnyM;

import javaslang.Lazy;
import javaslang.collection.Array;
import javaslang.collection.CharSeq;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Queue;
import javaslang.collection.Stream;
import javaslang.collection.Vector;
import javaslang.concurrent.Future;
import javaslang.control.Either;
import javaslang.control.Option;
import javaslang.control.Try;
import javaslang.control.Try.NonFatalException;

public class AnyJavaslangMTest {

	@Test
	public void testToList(){
		
		assertThat(Javaslang.traversable(List.of(1,2,3)).toList(), equalTo(Arrays.asList(1,2,3)));
	}
	@Test
	public void monadTest(){
		assertThat(Javaslang.value(Try.of(this::success))
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void tryTest(){
		assertThat(Javaslang.tryM(Try.of(this::success))
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test(expected=NonFatalException.class)
	public void tryTestFailure(){
		
		Javaslang.tryM(Try.failure(new RuntimeException()))
			.toSequence()
			.forEach(System.out::println);
		
	}
	@Test
	public void tryTestFailureProcess(){
		
		Exception e = new RuntimeException();
		Javaslang.tryM(Try.failure(e));
		System.out.println("hello!");
		assertThat(Javaslang.tryM(Try.failure(e))
				.stream()
				.toList(),equalTo(Arrays.asList()));
		
	}
	@Test
	public void whenSuccessFailureProcessDoesNothing(){
		
		assertThat(Javaslang.tryM(Try.success("hello world"))
											.toSequence()
											.toList(),equalTo(Arrays.asList("hello world")));
			
		
	}
	@Test
	public void tryFlatMapTest(){
		assertThat(Javaslang.tryM(Try.of(this::success))
			.map(String::toUpperCase)
			.flatMap(AnyM::ofNullable)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}

	private String success(){
		return "hello world";
		
	}
	private String exceptional(){
		
		throw new RuntimeException();
	}
	@Test
	public void eitherTest(){
		assertThat(Javaslang.either(Either.right("hello world"))
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void eitherLeftTest(){
		assertThat(Javaslang.either(Either.<String,String>left("hello world"))
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList()));
	}
	@Test
	public void eitherFlatMapTest(){
		assertThat(Javaslang.either(Either.<Object,String>right("hello world"))
			.map(String::toUpperCase)
			.flatMap(AnyM::ofNullable)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void rightProjectionTest(){
		assertThat(Javaslang.right(Either.<Object,String>right("hello world").right())
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void rightProjectionLeftTest(){
		assertThat(Javaslang.right(Either.<String,String>left("hello world").right())
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList()));
	}
	@Test
	public void rightProjectionFlatMapTest(){
		assertThat(Javaslang.right(Either.<Object,String>right("hello world").right())
			.map(String::toUpperCase)
			.flatMap(AnyM::ofNullable)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void leftProjectionTest(){
		assertThat(Javaslang.right(Either.<String,String>left("hello world").right())
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList()));
	}
	
	@Test
	public void leftProjectionLeftTest(){
		assertThat(Javaslang.left(Either.<String,String>left("hello world").left())
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void leftProjectionLeftFlatMapTest(){
		assertThat(Javaslang.left(Either.<String,String>left("hello world").left())
			.map(String::toUpperCase)
			.flatMap(AnyM::ofNullable)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void optionTest(){
		assertThat(Javaslang.option(Option.of("hello world"))
				.map(String::toUpperCase)
				.toSequence()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void optionFlatMapTest(){
		assertThat(Javaslang.option(Option.of("hello world"))
				.map(String::toUpperCase)
				.flatMap(AnyM::ofNullable)
				.toSequence()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void optionEmptyTest(){
		assertThat(Javaslang.option(Option.<String>none())
				.map(String::toUpperCase)
				.toSequence()
				.toList(),equalTo(Arrays.asList()));
	}
	@Test
	public void futureTest(){
		assertThat(Javaslang.value(Future.of(()->"hello world"))
				.map(String::toUpperCase)
				.toSequence()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void futureFlatMapTest(){
		assertThat(Javaslang.value(Future.of(()->"hello world"))
				.map(String::toUpperCase)
				.flatMap(AnyM::ofNullable)
				.toSequence()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void lazyTest(){
		assertThat(Javaslang.value(Lazy.of(()->"hello world"))
				.map(String::toUpperCase)
				.toSequence()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void streamTest(){
		assertThat(Javaslang.traversable(Stream.of("hello world"))
				.map(String::toUpperCase)
				.stream()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void listTest(){
		assertThat(Javaslang.traversable(List.of("hello world"))
				.map(String::toUpperCase)
				.stream()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void streamFlatMapTest(){
		assertThat(Javaslang.traversable(Stream.of("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->Javaslang.traversable(List.of(i)))
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void streamFlatMapTestJDK(){
		assertThat(Javaslang.traversable(Stream.of("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->AnyM.fromStream(java.util.stream.Stream.of(i)))
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void arrayTest(){
		assertThat(Javaslang.traversable(Array.of("hello world"))
				.map(String::toUpperCase)
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void charSeqTest(){
		assertThat(Javaslang.traversable(CharSeq.of("hello world"))
				.map(c->c.toString().toUpperCase().charAt(0))
				.join(),equalTo("HELLO WORLD"));
	}
	@Test
	public void hashsetTest(){
		assertThat(Javaslang.traversable(HashSet.of("hello world"))
				.map(String::toUpperCase)
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void queueTest(){
		assertThat(Javaslang.traversable(Queue.of("hello world"))
				.map(String::toUpperCase)
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void vectorTest(){
		assertThat(Javaslang.traversable(Vector.of("hello world"))
				.map(String::toUpperCase)
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
}
