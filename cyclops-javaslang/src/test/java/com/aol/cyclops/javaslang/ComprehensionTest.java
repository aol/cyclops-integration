package com.aol.cyclops.javaslang;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.jooq.lambda.tuple.Tuple;
import org.junit.Test;

import com.aol.cyclops.control.For;
import com.aol.cyclops.types.Traversable;

import javaslang.collection.List;
import javaslang.collection.Stream;
import javaslang.control.Option;

public class ComprehensionTest {
	@Test
	public void optionalTest(){
		
		assertTrue(Javaslang.ForValue.each2(Option.of(10), a->Option.none(), (a,b)->"failed").isEmpty());
	}
	@Test
	public void option2Test(){
		
		assertThat(Javaslang.ForValue.each2(Option.of(10), a-> Option.<Integer>of(a+20), (a,b)->a+b).get(),equalTo(40));
	}
	@Test
	public void generate(){

		
		String s = Javaslang.ForTraversable.each2(List.of(1,2,3),
												a->List.<Integer>of(a+10), 
												Tuple::tuple).toString();
	
		assertThat(s,equalTo("List((1, 11), (2, 12), (3, 13))"));
	}
	
	@Test
	public void generateStream(){
		
		String s = Javaslang.ForTraversable.each2(Stream.of(1,2,3),
												a->Stream.<Integer>of(a+10), 
												Tuple::tuple)
												.toString();
		
		assertThat(s,equalTo("Stream((1, 11), ?)"));
	}
	
	@Test
	public void generateListStream(){

		
		String s = Javaslang.ForTraversable.each2(List.of(1,2,3),
												a->Stream.<Integer>of(a+10), 
												Tuple::tuple).toString();
	
		assertThat(s,equalTo("List((1, 11), (2, 12), (3, 13))"));
	}
	@Test
	public void cfList(){
		
		CompletableFuture<String> future = CompletableFuture.supplyAsync(this::loadData);
		CompletableFuture<List<String>> results1 = For.future(future)
 														.iterable(a->List.of("first","second"))
 														.yield( loadedData -> localData-> loadedData + ":" + localData )
 														.unwrap();
		
		System.out.println(results1.join());
		
	}
	private String loadData(){
		return "loaded";
	}
	/**
	 * 
def prepareCappuccino(): Try[Cappuccino] = for {
  ground <- Try(grind("arabica beans"))
  water <- Try(heatWater(Water(25)))
  espresso <- Try(brew(ground, water))
  foam <- Try(frothMilk("milk"))
} yield combine(espresso, foam)
	 */
	@Test
	public void futureTest(){
		
		CompletableFuture<String> result = 	For.future(grind("arabica beans"))
							  				 .future(ground->heatWater(new Water(25)))
							  				 .future(ground -> water -> brew(ground,water))
							  				 .future(a->b->c->frothMilk("milk"))
							  				 .yield(ground ->water -> espresso->foam-> combine(espresso,foam))
							  				 .unwrap();
		
		System.out.println(result.join());
	}
	
	
	CompletableFuture<String> grind(String beans) {
		 return CompletableFuture.completedFuture("ground coffee of "+ beans);
	}

	CompletableFuture<Water> heatWater(Water water){
		 return CompletableFuture.supplyAsync((Supplier) ()->water.withTemperature(85));
		  
	}

	CompletableFuture<String> frothMilk(String milk) {
		 return CompletableFuture.completedFuture("frothed " + milk);
	}

	CompletableFuture<String>	brew(String coffee, Water heatedWater){
		  return CompletableFuture.completedFuture("espresso");
	}
	String combine(String espresso ,String frothedMilk) {
		return "cappuccino";
	}
}
