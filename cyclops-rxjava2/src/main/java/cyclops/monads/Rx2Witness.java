package cyclops.monads;



import com.aol.cyclops.rx2.adapter.FlowableReactiveSeq;
import com.aol.cyclops.rx2.adapter.ObservableAdapter;
import com.aol.cyclops.rx2.adapter.ObservableReactiveSeq;
import com.aol.cyclops2.types.extensability.FunctionalAdapter;
import io.reactivex.Flowable;
import io.reactivex.Observable;


public interface Rx2Witness {

    public static <T> Flowable<T> flowable(AnyM<flowable, ? extends T> anyM){
        FlowableReactiveSeq<T> obs = anyM.unwrap();
        return obs.getFlowable();
    }

    static interface FlowableWitness<W extends FlowableWitness<W>>  extends WitnessType<W> {

    }
    public static enum flowable implements ObservableWitness<flowable> {
        INSTANCE;

        @Override
        public FunctionalAdapter<flowable> adapter() {
            return new FlowableAdapter();
        }

    }

    public static <T> Observable<T> observable(AnyM<obsvervable, ? extends T> anyM){
        ObservableReactiveSeq<T> obs = anyM.unwrap();
        return obs.getObservable();
    }

    static interface ObservableWitness<W extends ObservableWitness<W>>  extends WitnessType<W> {

    }
    public static enum obsvervable implements ObservableWitness<obsvervable> {
        INSTANCE;

        @Override
        public FunctionalAdapter<obsvervable> adapter() {
            return new ObservableAdapter();
        }

    }

}
