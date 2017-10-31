package cyclops.streams.observables;


import cyclops.companion.rx2.Observables;
import cyclops.reactive.ReactiveSeq;
import cyclops.stream.Spouts;
import org.jooq.lambda.Collectable;

public class AsyncCollectableTest extends CollectableTest {


    public <T> Collectable<T> of(T... values){

        ReactiveSeq<T> seq = Spouts.<T>async(s->{
            Thread t = new Thread(()-> {
                for (T next : values) {
                    s.onNext(next);
                }
                s.onComplete();
            });
            t.start();
        });

        return Observables.reactiveSeq(Observables.observableFrom(seq)).collectors();
    }

}
