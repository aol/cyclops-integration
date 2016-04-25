package com.aol.cyclops.functionaljava;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.*;
import static  com.aol.cyclops.functionaljava.FJ.ForWriter;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.aol.cyclops.control.For;

import fj.Monoid;
import fj.data.List;
import fj.data.Option;
import fj.data.Writer;

public class ComprehensionTest {

	@Test
	public void cfList(){
		CompletableFuture<String> future = CompletableFuture.supplyAsync(this::loadData);
		CompletableFuture<List<String>> results1 = For.future(future)
 														.anyM(a->FJ.list(List.list("first","second")))
 														.yield( loadedData -> localData-> loadedData + ":" + localData )
 														.unwrap();
		
		System.out.println(results1.join());
	}
	private String loadData(){
		return "loaded";
	}
	
	@Test
	public void optionTest(){
		assertFalse(FJ.ForOption.each2(Option.some(10), a->Option.none(), (a,b)->"failed").isSome());
	}
	@Test
	public void option2Test(){
		
		assertThat(FJ.ForOption.each2(Option.some(10), a-> Option.<Integer>some(a+20), (a,b)->a+b).option(-1, f->f),equalTo(40));
	}
	
	@Test
	public void writerTest(){
		assertThat(ForWriter.each2(Writer.unit("lower", "", Monoid.stringMonoid),
							a->Writer.unit(a+"hello",Monoid.stringMonoid),(a,b)->a.toUpperCase() + b).value(),equalTo("LOWERlowerhello"));
						
	}
}
