# RxJava Integration

v8.0.0 of cyclops-rx and above is built using v1.1.3 of RxJava

## Get cyclops-rx


* [![Maven Central : cyclops-reactor](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-rx/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aol.cyclops/cyclops-rx)
* [Javadoc for cyclops-rx](http://www.javadoc.io/doc/com.aol.cyclops/cyclops-rx)


# cyclops-rx features include

1. Native for comprehensions for RxJava Observables
2. Native Monad Tranformer for Observable. ObservableT also has native for comprehensions
3. Monad wrapping via AnyM / AnyMValue / AnyMSeq
4. Compatible with cyclops-react pattern matching
5. Ability to use Observables inside cyclops-react monad transformers (as the wrapping type, requires conversion to act as the nested type).
6. Observables and ObsverableTs companion classes for working with Observables and ObservableTransformers



Use Rx.observable to create wrapped RxJava Monads.


## Example for comprehensions with Observable

```java

import static com.aol.cyclops.rx.Observables.forEach;

Observable<Integer> result = forEach(Obserbable.just(10,20),a->Observable.<Integer>just(a+10)
                                                          ,(a,b)->a+b);
	
//Observable[30,50]
 ```

 
 ## observableT monad transformer
 
```java
import static com.aol.cyclops.rx.ObservableTs.observableT;

ObservableTSeq<Integer> nested = observableT(ReactiveSeq.of(Observable.just(1,2,3),Observable.just(10,20,30)));
ObservableTSeq<Integer> mapped = nested.map(i->i*3);

//mapped = [ReactiveSeq[Observable[3,6,9],Observable[30,60,90]]
```
