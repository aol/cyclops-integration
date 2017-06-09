package cyclops.monads;



import com.aol.cyclops.rx.adapter.ObservableAdapter;
import com.aol.cyclops.rx.adapter.ObservableReactiveSeq;
import com.aol.cyclops2.types.extensability.FunctionalAdapter;
import rx.Observable;


public interface RxWitness {

    public static <T> Observable<T> observable(AnyM<obsvervable,? extends T> anyM){
        ObservableReactiveSeq<T> obs = anyM.unwrap();
        return obs.getObservable();
    }

    static interface ObservableWitness<W extends RxWitness.ObservableWitness<W>>  extends WitnessType<W> {

    }
    public static enum obsvervable implements ObservableWitness<obsvervable> {
        INSTANCE;

        @Override
        public FunctionalAdapter<obsvervable> adapter() {
            return new ObservableAdapter();
        }

    }

}
