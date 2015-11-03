package com.aol.cyclops.lambda.monads;

import java.io.BufferedReader;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import com.aol.cyclops.internal.AsGenericMonad;
import com.aol.cyclops.internal.Monad;
import com.aol.cyclops.lambda.api.AsAnyM;
import com.aol.cyclops.monad.AnyM;
import com.aol.cyclops.sequence.Monoid;
import com.aol.cyclops.sequence.SequenceM;
import com.aol.cyclops.sequence.streamable.Streamable;

/**
 * 
 * Wrapper for Any Monad type
 * @see AnyMonads companion class for static helper methods
 * 
 * @author johnmcclean
 *
 * @param <T>
 */
@AllArgsConstructor(access=AccessLevel.PROTECTED)
public class AnyMImpl<T> implements AnyM<T>{
	
	private final Monad<Object,T> monad;
	
	
	public final <R> R unwrap(){
		return (R)monad.unwrap();
	}
	

	public final Monad monad(){
		return (Monad)monad;
	}
	
	public final   AnyM<T>  filter(Predicate<? super T> fn){
		return monad.filter(fn).anyM();
	}
	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Functor#map(java.util.function.Function)
	 */
	public final  <R> AnyM<R> map(Function<? super T,? extends R> fn){
		return monad.map(fn).anyM();
	}
	/* (non-Javadoc)
	 * @see com.aol.cyclops.lambda.monads.Functor#peek(java.util.function.Consumer)
	 */
	public final   AnyM<T>  peek(Consumer<? super T> c) {
		return monad.peek(c).anyM();
	}
	
	
	/**
	 * Perform a looser typed flatMap / bind operation
	 * The return type can be another type other than the host type
	 * 
	 * @param fn flatMap function
	 * @return flatMapped monad
	*/
	public final <R> AnyM<R> bind(Function<? super T,?> fn){
		return monad.bind(fn).anyM();
	
	} 
	/**
	 * Perform a bind operation (@see #bind) but also lift the return value into a Monad using configured
	 * MonadicConverters
	 * 
	 * @param fn flatMap function
	 * @return flatMapped monad
	 */
	public final <R> AnyM<R> liftAndBind(Function<? super T,?> fn){
		return monad.liftAndBind(fn).anyM();
	
	}
	/**
	 * Perform a flatMap operation where the result will be a flattened stream of Characters
	 * from the CharSequence returned by the supplied function.
	 * 
	 * <pre>
	 * {@code
	 * List<Character> result = anyM("input.file")
								.liftAndBindCharSequence(i->"hello world")
								.asSequence()
								.toList();
		
		assertThat(result,equalTo(Arrays.asList('h','e','l','l','o',' ','w','o','r','l','d')));
	 * 
	 * }</pre>
	 * 
	 * 
	 * 
	 * @param fn
	 * @return
	 */
	public final  AnyM<Character> flatMapCharSequence(Function<? super T,CharSequence> fn) {
		return monad.liftAndBind(fn).anyM();
	}
	/**
	 *  Perform a flatMap operation where the result will be a flattened stream of Strings
	 * from the text loaded from the supplied files.
	 * 
	 * <pre>
	 * {@code
	 * 		List<String> result = anyM("input.file")
								.map(getClass().getClassLoader()::getResource)
								.peek(System.out::println)
								.map(URL::getFile)
								.liftAndBindFile(File::new)
								.asSequence()
								.toList();
		
		assertThat(result,equalTo(Arrays.asList("hello","world")));
	 * 
	 * }
	 * 
	 * </pre>
	 * 
	 * @param fn
	 * @return
	 */
	public final  AnyM<String> flatMapFile(Function<? super T,File> fn) {
		return monad.liftAndBind(fn).anyM();
	}
	/**
	 *  Perform a flatMap operation where the result will be a flattened stream of Strings
	 * from the text loaded from the supplied URLs 
	 * <pre>
	 * {@code 
	 * List<String> result = anyM("input.file")
								.liftAndBindURL(getClass().getClassLoader()::getResource)
								.asSequence()
								.toList();
		
		assertThat(result,equalTo(Arrays.asList("hello","world")));
	 * 
	 * }
	 * 
	 * </pre>
	 * 
	 * 
	 * @param fn
	 * @return
	 */
	public final  AnyM<String> flatMapURL(Function<? super T, URL> fn) {
		return monad.liftAndBind(fn).anyM();
	}
	/**
	  *  Perform a flatMap operation where the result will be a flattened stream of Strings
	 * from the text loaded from the supplied BufferedReaders
	 * 
	 * <pre>
	 * {@code
	 * List<String> result = anyM("input.file")
								.map(getClass().getClassLoader()::getResourceAsStream)
								.map(InputStreamReader::new)
								.liftAndBindBufferedReader(BufferedReader::new)
								.asSequence()
								.toList();
		
		assertThat(result,equalTo(Arrays.asList("hello","world")));
	 * 
	 * }
	 * 
	 * 
	 * 
	 * @param fn
	 * @return
	 */
	public final  AnyM<String> flatMapBufferedReader(Function<? super T,BufferedReader> fn) {
		return monad.liftAndBind(fn).anyM();
	}
	
	/**
	 * join / flatten one level of a nested hierarchy
	 * 
	 * @return Flattened / joined one level
	 */
	public final <T1> AnyM<T1> flatten(){
		return monad.flatten().anyM();
		
	}
	
	/**
	 * Aggregate the contents of this Monad and the supplied Monad 
	 * 
	 * <pre>{@code 
	 * 
	 * List<Integer> result = anyM(Stream.of(1,2,3,4))
	 * 							.aggregate(anyM(Optional.of(5)))
	 * 							.asSequence()
	 * 							.toList();
		
		assertThat(result,equalTo(Arrays.asList(1,2,3,4,5)));
		}</pre>
	 * 
	 * @param next Monad to aggregate content with
	 * @return Aggregated Monad
	 */
	public final  AnyM<T> aggregate(AnyM<T> next){
		return monad.aggregate(next.monad()).anyM();
	}
	public final  <R> AnyM<List<R>> aggregateUntyped(AnyM<?> next){
		return monad.aggregate(next.monad()).anyM();
	}
	@Deprecated //to be removed in 6.0.0
	public void forEach(Consumer<? super T> action) {
		asSequence().forEach(action);	
	}
	
	/**
	 * flatMap operation
	 * 
	 * @param fn
	 * @return 
	 */
	public final <R> AnyM<R> flatMap(Function<? super T,AnyM<? extends R>> fn) {
		return monad.flatMap(in -> fn.apply(in).unwrap()).anyM();
	}
	
	/**
	 * Convenience method to allow method reference support, when flatMap return type is a Stream
	 * 
	 * @param fn
	 * @return
	 */
	public final <R> AnyM<R> flatMapStream(Function<? super T,BaseStream<? extends R,?>> fn) {
		return monad.flatMap(in -> fn.apply(in)).anyM();
	}
	/**
	 * Convenience method to allow method reference support, when flatMap return type is a Streamable
	 * 
	 * @param fn
	 * @return
	 */
	public final <R> AnyM<R> flatMapStreamable(Function<? super T,Streamable<R>> fn) {
		return monad.flatMap(in -> fn.apply(in)).anyM();
	}
	/**
	 * flatMapping to a Stream will result in the Stream being converted to a List, if the host Monad
	 * type is not a Stream (or Stream like type). (i.e.
	 *  <pre>
	 *  {@code  
	 *   AnyM<Integer> opt = anyM(Optional.of(20));
	 *   Optional<List<Integer>> optionalList = opt.flatMap( i -> anyM(Stream.of(1,2,i))).unwrap();  
	 *   
	 *   //Optional [1,2,20]
	 *  }</pre>
	 *  
	 *  In such cases using Arrays.asList would be more performant
	 *  <pre>
	 *  {@code  
	 *   AnyM<Integer> opt = anyM(Optional.of(20));
	 *   Optional<List<Integer>> optionalList = opt.flatMapCollection( i -> asList(1,2,i))).unwrap();  
	 *   
	 *   //Optional [1,2,20]
	 *  }</pre>
	 * @param fn
	 * @return
	 */
	public final <R> AnyM<R> flatMapCollection(Function<? super T,Collection<? extends R>> fn) {
		return monad.flatMap(in -> fn.apply(in)).anyM();
	}
	/**
	 * Convenience method to allow method reference support, when flatMap return type is a Optional
	 * 
	 * @param fn
	 * @return
	 */
	public final <R> AnyM<R> flatMapOptional(Function<? super T,Optional<? extends R>> fn) {
		return monad.flatMap(in -> fn.apply(in)).anyM();
	}
	public final <R> AnyM<R> flatMapCompletableFuture(Function<? super T,CompletableFuture<? extends R>> fn) {
		return monad.flatMap(in -> fn.apply(in)).anyM();
	}
	
	public final <R> AnyM<R> flatMapSequenceM(Function<? super T,SequenceM<? extends R>> fn) {
		return monad.flatMap(in -> fn.apply(in).unwrap()).anyM();
	}
	
	
	
	
	/**
	 * Sequence the contents of a Monad.  e.g.
	 * Turn an <pre>
	 * 	{@code Optional<List<Integer>>  into Stream<Integer> }</pre>
	 * 
	 * <pre>{@code
	 * List<Integer> list = anyM(Optional.of(Arrays.asList(1,2,3,4,5,6)))
											.<Integer>toSequence(c->c.stream())
											.collect(Collectors.toList());
		
		
		assertThat(list,hasItems(1,2,3,4,5,6));
		
	 * 
	 * }</pre>
	 * 
	 * @return A Sequence that wraps a Stream
	 */
	public final <NT> SequenceM<NT> toSequence(Function<T,Stream<NT>> fn){
		return monad.flatMapToStream((Function)fn)
					.sequence();
	}
	/**
	 *  <pre>{@code Optional<List<Integer>>  into Stream<Integer> }</pre>
	 * Less type safe equivalent, but may be more accessible than toSequence(fn) i.e. 
	 * <pre>
	 * {@code 
	 *    toSequence(Function<T,Stream<NT>> fn)
	 *   }
	 *   </pre>
	 *  <pre>{@code
	 * List<Integer> list = anyM(Optional.of(Arrays.asList(1,2,3,4,5,6)))
											.<Integer>toSequence()
											.collect(Collectors.toList());
		
		
		
	 * 
	 * }</pre>
	
	 * @return A Sequence that wraps a Stream
	 */
	public final <T> SequenceM<T> toSequence(){
		return monad.streamedMonad().sequence();
	}
	
	
	/**
	 * Wrap this Monad's contents as a Sequence without disaggreating it. .e.
	 *  <pre>{@code Optional<List<Integer>>  into Stream<List<Integer>> }</pre>
	 * If the underlying monad is a Stream it is returned
	 * Otherwise we flatMap the underlying monad to a Stream type
	 */
	public final SequenceM<T> asSequence(){
		return monad.sequence();
		
	}
	
	
		
	

	/**
	 * Apply function/s inside supplied Monad to data in current Monad
	 * 
	 * e.g. with Streams
	 * <pre>{@code 
	 * 
	 * AnyM<Integer> applied =anyM(Stream.of(1,2,3)).applyM(anyM(Streamable.of( (Integer a)->a+1 ,(Integer a) -> a*2))).simplex();
	
	 	assertThat(applied.toList(),equalTo(Arrays.asList(2, 2, 3, 4, 4, 6)));
	 }</pre>
	 * 
	 * with Optionals 
	 * <pre>{@code
	 * 
	 *  Any<Integer> applied =anyM(Optional.of(2)).applyM(anyM(Optional.of( (Integer a)->a+1)) );
		assertThat(applied.toList(),equalTo(Arrays.asList(3)));}</pre>
	 * 
	 * @param fn
	 * @return
	 */
	public final <R> AnyM<R> applyM(AnyM<Function<? super T,? extends R>> fn){
		return monad.applyM(fn.monad()).anyM();
		
	}
	/**
	 * Filter current monad by each element in supplied Monad
	 * 
	 * e.g.
	 * 
	 * <pre>{@code
	 *  AnyM<AnyM<Integer>> applied = anyM(Stream.of(1,2,3))
	 *    									.filterM(anyM(Streamable.of( (Integer a)->a>5 ,(Integer a) -> a<3)))
	 *    									.simplex();
	 * 
	 *  //results in AnyM((AnyM(1),AnyM(2),AnyM(())
	 * //or in terms of the underlying monad as Stream.of(Stream.of(1),Stream.of(2),Stream.of(())
	 * }</pre>
	 * 
	 * @param fn
	 * @return
	 */
	public final   AnyM<AnyM<T>> simpleFilter(AnyM<Predicate<? super T>> fn){
		return  monad.simpleFilter(fn.monad()).anyM().map(t->AsAnyM.notTypeSafeAnyM(t));
			
	
	//	filterM((a: Int) => List(a > 2, a % 2 == 0), List(1, 2, 3), ListMonad),
	//List(List(3), Nil, List(2, 3), List(2), List(3),
	//	  Nil, List(2, 3), List(2))												
	}
	public final   AnyM<Stream<T>> simpleFilter(Stream<Predicate<? super T>> fn){
		return  monad.simpleFilter(AsGenericMonad.asMonad(fn)).anyM();
													
	}
	public final   AnyM<Stream<T>> simpleFilter(Streamable<Predicate<? super T>> fn){
		return  monad.simpleFilter(AsGenericMonad.asMonad(fn)).anyM();
													
	}
	public final   AnyM<Optional<T>> simpleFilter(Optional<Predicate<? super T>> fn){
		return  monad.simpleFilter(AsGenericMonad.asMonad(fn)).anyM();
													
	}
	public final   AnyM<CompletableFuture<T>> simpleFilter(CompletableFuture<Predicate<? super T>> fn){
		return  monad.simpleFilter(AsGenericMonad.asMonad(fn)).anyM();
													
	}
	
	public <T> AnyM<T> unit(T value){
		return AsAnyM.notTypeSafeAnyM(monad.unit(value));
	}
	public <T> AnyM<T> empty(){
		return (AnyMImpl)unit(null).filter(t->false);
	}
	/**
	 * 
	 * Replicate given Monad
	 * 
	 * <pre>{@code 
	 * 	
	 *   AnyM<Optional<Integer>> applied =monad(Optional.of(2)).replicateM(5).simplex();
		 assertThat(applied.unwrap(),equalTo(Optional.of(Arrays.asList(2,2,2,2,2))));
		 
		 }</pre>
	 * 
	 * 
	 * @param times number of times to replicate
	 * @return Replicated Monad
	 */
	public final AnyM<List<T>> replicateM(int times){
		
		return monad.replicateM(times).anyM();		
	}
	/**
	 * Perform a reduction where NT is a (native) Monad type
	 * e.g. 
	 * <pre>{@code 
	 * Monoid<Optional<Integer>> optionalAdd = Monoid.of(Optional.of(0), (a,b)-> Optional.of(a.get()+b.get()));
		
		assertThat(monad(Stream.of(2,8,3,1)).reduceM(optionalAdd).unwrap(),equalTo(Optional.of(14)));
		}</pre>
	 * 
	 * 
	 * @param reducer
	 * @return
	 */
	public final   AnyM<T> reduceMOptional(Monoid<Optional<T>> reducer){
		return monad.reduceM(reducer).anyM();
	}
	public final   AnyM<T> reduceMStream(Monoid<Stream<T>> reducer){
		return monad.reduceM(reducer).anyM();
	}
	public final   AnyM<T> reduceMStreamable(Monoid<Streamable<T>> reducer){
		return monad.reduceM(reducer).anyM();
	}
	public final   AnyM<T> reduceMCompletableFuture(Monoid<CompletableFuture<T>> reducer){
		return monad.reduceM(reducer).anyM();
	}
	
	public final   AnyM<T> reduceM(Monoid<AnyM<T>> reducer){
	//	List(2, 8, 3, 1).foldLeftM(0) {binSmalls} -> Optional(14)
	//	convert to list Optionals
		
		
	
		return monad.reduceM(Monoid.of(reducer.zero().unwrap(), (a,b)-> reducer.combiner().apply(AsAnyM.notTypeSafeAnyM(a), 
				AsAnyM.notTypeSafeAnyM(b)))).anyM();		
	}
	
	
	@Override
    public String toString() {
        return String.format("AnyM(%s)", monad );
    }
	
}