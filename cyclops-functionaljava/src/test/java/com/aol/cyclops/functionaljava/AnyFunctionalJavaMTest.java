package com.aol.cyclops.functionaljava;

import static com.aol.cyclops.functionaljava.FJ.stream;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.data.collections.extensions.standard.ListX;

import fj.Monoid;
import fj.control.Trampoline;
import fj.data.Either;
import fj.data.IOFunctions;
import fj.data.IterableW;
import fj.data.List;
import fj.data.Option;
import fj.data.Reader;
import fj.data.State;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.Writer;

public class AnyFunctionalJavaMTest {
	@Rule
    public final SystemOutRule sout = new SystemOutRule().enableLog();
    private static final String SEP = System.getProperty("line.separator");

	private String success(){
		return "hello world";
		
	}
	private String exceptional(){
		
		throw new RuntimeException();
	}
	@Test
	public void streamSchedule(){
		Executor ex;
		
		stream(Stream.stream(1,2,3)).schedule("* * * * * ?", Executors.newScheduledThreadPool(1))
									.connect()
									.forEach(System.out::println);
	}
	
	@Test @Ignore
	public void flatMapCrossTypeNotCollectionUnwrap(){
		assertThat(FJ.option(Option.some(1))
							.flatMapFirst(i->FJ.stream(Stream.stream(i+2)))
							.unwrap(),equalTo(Option.some(Arrays.asList(3))));
	}
	@Test @Ignore
	public void flatMapCrossTypeNotCollection(){
		
		assertThat(FJ.option(Option.some(1)).flatMapFirst(i->FJ.stream(Stream.stream(i+2))).stream().toList(),equalTo(Arrays.asList(Arrays.asList(3))));
	}
	@Test
	public void eitherTest(){
		assertThat(FJ.either(Either.right("hello world"))
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void eitherLeftTest(){
		assertThat(FJ.either(Either.<String,String>left("hello world"))
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList()));
	}
	@Test
	public void eitherFlatMapTest(){
		assertThat(FJ.either(Either.right("hello world"))
			.map(String::toUpperCase)
			.flatMap(a->FJ.option(Option.some(a)))
			.stream()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void rightProjectionTest(){
		assertThat(FJ.right(Either.right("hello world").right())
			.map(String::toUpperCase)
			.stream()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void rightProjectionLeftTest(){
		assertThat(FJ.right(Either.<String,String>left("hello world").right())
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList()));
	}
	@Test
	public void rightProjectionFlatMapTest(){
		assertThat(FJ.right(Either.right("hello world").right())
			.map(String::toUpperCase)
			.flatMap(a->FJ.option(Option.some(a)))
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void leftProjectionTest(){
		assertThat(FJ.right(Either.<String,String>left("hello world").right())
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList()));
	}
	
	@Test
	public void leftProjectionLeftTest(){
		assertThat(FJ.left(Either.<String,String>left("hello world").left())
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void leftProjectionLeftFlatMapTest(){
		assertThat(FJ.left(Either.<String,String>left("hello world").left())
			.map(String::toUpperCase)
			.flatMap(a->FJ.option(Option.some(a)))
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void optionTest(){
		assertThat(FJ.option(Option.some("hello world"))
				.map(String::toUpperCase)
				.toSequence()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void optionFlatMapTest(){
		assertThat(FJ.option(Option.some("hello world"))
				.map(String::toUpperCase)
				.flatMap(a->FJ.option(Option.some(a)))
				.toSequence()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void optionEmptyTest(){
		assertThat(FJ.option(Option.<String>none())
				.map(String::toUpperCase)
				.toSequence()
				.toList(),equalTo(Arrays.asList()));
	}
	@Test
	public void streamTest(){
		assertThat(FJ.stream(Stream.stream("hello world"))
				.map(String::toUpperCase)
				.stream()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void listTest(){
		assertThat(FJ.list(List.list("hello world"))
				.map(String::toUpperCase)
				.stream()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void iterableWTest(){
		assertThat(FJ.iterableW(IterableW.wrap(Arrays.asList("hello world")))
				.map(String::toUpperCase)
				.stream()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void streamFlatMapTest(){
		assertThat(FJ.stream(Stream.stream("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->FJ.stream(Stream.stream(i)))
				.stream()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void streamFlatMapTestJDK(){
		assertThat(FJ.stream(Stream.stream("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->AnyM.fromStream(java.util.stream.Stream.of(i)))
				.stream()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void JDKstreamFlatMapTest(){
		assertThat(AnyM.fromStream(java.util.stream.Stream.of("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->FJ.stream(Stream.stream(i)))
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void JDKstreamListFlatMapTest(){
		assertThat(AnyM.fromStream(java.util.stream.Stream.of("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->FJ.list(List.list(i)))
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void JDKstreamIterableWFlatMapTest(){
		assertThat(AnyM.fromStream(java.util.stream.Stream.of("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->FJ.iterableW(IterableW.wrap(ListX.of(i))))
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void JDKListFlatMapTest(){
		assertThat(AnyM.fromList(ListX.of("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->FJ.stream(Stream.stream(i)))
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void JDKOptionFlatMapTest(){
		assertThat(AnyM.fromStream(java.util.stream.Stream.of("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->FJ.option(Option.some(i)))
				.stream()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test
	public void JDKOptionEmptyFlatMapTest(){
		assertThat(AnyM.fromStream(java.util.stream.Stream.of("hello world"))
				.map(String::toUpperCase)
				.flatMap(i->FJ.option(Option.none()))
				.stream()
				.toList(),equalTo(Arrays.asList()));
	}
	public String finalStage(){
		return "hello world";
	}
	@Test
	public void trampolineTest(){
		assertThat(FJ.trampoline(FJ.Trampoline8.suspend(()-> Trampoline.pure(finalStage())))
				.map(String::toUpperCase)
				.stream()
				.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test
	public void readerTest(){
		
		FJ.reader(Reader.unit( (Integer a) -> "hello "+a ))
			.map(String::toUpperCase);
		
		
		assertThat(FJ.unwrapReader(FJ.reader(Reader.unit( (Integer a) -> "hello "+a ))
						.map(String::toUpperCase)).f(10),equalTo("HELLO 10"));
	}
	
	
	@Test
	public void validateTest(){
		assertThat(FJ.validation(Validation.success(success()))
			.map(String::toUpperCase)
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	
	@Test//(expected=javaslang.control.Failure.NonFatal.class)
	public void validationTestFailure(){
		
		FJ.validation(Validation.fail(new RuntimeException()))
			.toSequence()
			.forEach(System.out::println);
		
	}
	
	@Test
	public void validateTestFailureProcess(){
		
		Exception e = new RuntimeException();
		assertThat(FJ.validation(Validation.fail(e))
				.toSequence()
				.toList(),equalTo(Arrays.asList()));
		
	}
	
	@Test
	public void tryFlatMapTest(){
		assertThat(FJ.validation(Validation.success(success()))
			.map(String::toUpperCase)
			.flatMap(a->FJ.option(Option.some(a)))
			.toSequence()
			.toList(),equalTo(Arrays.asList("HELLO WORLD")));
	}
	@Test @Ignore
	public void writerFailingTest(){
		//even with types the same Writer isn't fully typesafe
		
		//failing FJ code
		Writer.unit("lower", "", Monoid.stringMonoid).map(a->a.length());
		
		
		//see -> can map from String to Int, breaking it
		System.out.println(FJ.writer(Writer.unit("lower", "", Monoid.stringMonoid))
				.map(a->a.length()).<Writer<String,String>>unwrap().value());
				
		
	}
	@Test 
	public void writerUnwrapTest(){
		
		
		Writer<String,String> writer = Writer.unit("lower", "", Monoid.stringMonoid);
		assertThat(FJ.unwrapWriter(FJ.writer(writer)
				.map(String::toUpperCase),writer).value(),equalTo("LOWER"));
				
		
	}
	@Test 
	public void writerUpperCaseTest(){
		
		
	
		assertThat(FJ.writer(Writer.unit("lower", "", Monoid.stringMonoid))
				.map(String::toUpperCase).<Writer<String,String>>unwrap().value(),equalTo("LOWER"));
				
		
	}
	@Test 
	public void writerFlatMapTest(){
		
		
	
		assertThat(FJ.writer(Writer.unit("lower", "", Monoid.stringMonoid))
				.flatMap(a->FJ.writer(Writer.unit("hello",Monoid.stringMonoid)))
				.map(String::toUpperCase)
				.<Writer<String,String>>unwrap().value(),equalTo("HELLO"));
				
		
	}
	
	@Test
	public void stateTest(){
	
		assertThat(FJ.unwrapState(FJ.state(State.constant("hello"))
			.map(String::toUpperCase)).run("")._2()
				,equalTo("HELLO"));
	}
	@Test
	public void stateFlatMapTest(){
	
		
		assertThat(FJ.unwrapState(FJ.state(State.constant("hello"))
				.flatMap(s->FJ.state(State.constant(s.toUpperCase() )))
			
					).run("")._2()
				,equalTo("HELLO"));
	}
	
	
	@Test
	public void ioTest() throws IOException{
		
		
		FJ.unwrapIO( 
				FJ.io(IOFunctions.lazy(a->{ System.out.println("hello world"); return a;}))
				.map(a-> {System.out.println("hello world2"); return a;})   ).run();
		  assertThat(
                  "hello world" + SEP +
                  "hello world2" + SEP ,equalTo( sout.getLog()));
	}
}
