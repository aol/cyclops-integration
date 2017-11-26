package cyclops.streams;

import cyclops.companion.rx.Observables;
import cyclops.reactive.ReactiveSeq;
import rx.Observable;
import rx.Subscriber;

import java.util.function.Function;

/*
 * Extensions for leveraging Rx Observable operators with cyclops-react ReactiveSeq instances
 *
 * <pre>
 * {@code
 *   ReactiveSeq.of(1,2,3)
                .to(lift(new Observable.Operator<Integer,Integer>(){


                    @Override
                    public Subscriber<? super Integer> call(Subscriber<? super Integer> subscriber) {
                        return subscriber; // operator code
                    }
                            }))
                   .map(i->i+1)
                   .to(observable(o->o.buffer(10)));

   }
 * </pre>
 *
 */
public class RxOperators {

    public static <T,R> Function<ReactiveSeq<T>,ReactiveSeq<R>> lift(final Observable.Operator<? extends R, ? super T> operator){
        return s->Observables.reactiveSeq(Observables.observableFrom(s).lift(operator));
    }
    public static <T,R> Function<ReactiveSeq<T>,ReactiveSeq<R>> observable(final Function<? super Observable<? super T>,? extends Observable<? extends R>> fn){
        return s->Observables.<R>reactiveSeq(Observables.narrow(fn.apply(Observables.observableFrom(s))));
    }
    public static <T,R> Function<Observable<T>,Observable<R>> seq(final Function<? super ReactiveSeq<? super T>,? extends ReactiveSeq<? extends R>> fn){
        return s-> Observables.observableFrom((ReactiveSeq<R>)fn.apply(Observables.reactiveSeq(s)));
    }
}
