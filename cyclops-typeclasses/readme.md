# Cyclops Type Classes


* [![Maven Central : cyclops-typeclasses](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-typeclassess/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-typeclassess)   [![javadoc.io](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-typeclasses/badge.svg)](https://javadocio-badges.herokuapp.com/com.aol.cyclops/cyclops-typeclasses)


A concise set of type classes designed for use with HKT encoded types. The goal is to provide an easy to use, powerful core set of classes for working with types from JDK, cyclops-react, jooλ , javaslang, Reactor, FunctionalJava & RxJava in a generic way. Integration modules will provide specific instances for library specific types.

The goal is not to replace the larger set available in HighJ, but to provide an easy to use and distil set of the most commonly required pieces of functionality.


Type classes included

* Functor
* Applicative
* Monad
* Comonad
* Foldable
* Traversable
* Monadplus

Implementations to be included

* General purpose implementation. Instances are instantiated by providing user defined Lambda's and HKT types on creation

* JDK Types : List [Monad, Traverse, Foldable, MonadPlus]
			: Deque [Monad, Traverse, Foldable, MonadPlus]
			: Stream [Monad, Traverse, Foldable, MonadPlus]
			: CompletableFuture [Monad, Traverse, MonadPlus]
			: Optional [Monad, Traverse, MonadPlus]
           
            
* cyclops-react types  : Maybe [Monad, Traverse,  MonadPlus]   
                       : Try [Monad, Traverse,  MonadPlus]  
                       : Eval [Monad, Traverse]
				       : Xor [Monad, Traverse, MonadPlus]   
				       : Ior [Monad, Traverse, MonadPlus]   

ReactiveSeq and LazyFutureStream supported by Stream.                                
            
            
