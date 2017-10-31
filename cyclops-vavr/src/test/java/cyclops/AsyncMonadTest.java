package cyclops;

import com.aol.cyclops.vavr.hkt.FutureKind;
import com.aol.cyclops.vavr.hkt.TryKind;
import com.oath.cyclops.hkt.Higher;
import cyclops.companion.vavr.Futures;
import cyclops.companion.vavr.Trys;
import cyclops.monads.VavrWitness.future;
import cyclops.monads.VavrWitness.tryType;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.monad.Monad;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.testng.annotations.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cyclops.companion.vavr.Futures.Instances.monad;
import static cyclops.companion.vavr.Futures.allTypeclasses;
import static cyclops.companion.vavr.Futures.anyM;
import static org.junit.Assert.assertTrue;


public class AsyncMonadTest {




    @Test
    public void syncAndAsync(){


        System.out.println("Run asynchronously..");
        Capitalizer<future> processorAsync = new Capitalizer<>(monad(),new AsyncWork());
        assertTrue(FutureKind.narrowK(processorAsync.process()).get());

        System.out.println("Run synchronously..");
        Capitalizer<tryType> processorSync = new Capitalizer<>(Trys.Instances.monad(),new SyncWork());
        assertTrue(TryKind.narrowK(processorSync.process()).get());



    }
    static interface GenericWork<W>{
         Higher<W,String> get();
         Higher<W,Boolean> save(String data);
    }

    @AllArgsConstructor
    static class Capitalizer<W>{
        Monad<W> monad;
        GenericWork<W> worker;


        public  Higher<W,Boolean> process(){
           return monad.peek(System.out::println,
                     monad.flatMap(worker::save,
                        monad.peek(System.out::println,
                                monad.map(s->s.toString(),worker.get()))));


        }
    }

    static class AsyncWork implements GenericWork<future>{

        private ExecutorService ex = Executors.newFixedThreadPool(2);

        @Override
        public FutureKind<String> get(){
            return FutureKind.widen(Future.ofSupplier(ex, () -> "load data asynchronously"));
        }

        @Override
        public FutureKind<Boolean> save(String data){
            return FutureKind.widen((Future.ofSupplier(ex,()->true)));
        }
    }
    static class SyncWork implements GenericWork<tryType>{

        @Override
        public TryKind<String> get() {
                return TryKind.successful("load data synchronously");
        }

        @Override
        public TryKind<Boolean> save(String data) {
            return TryKind.successful(true);
        }
    }

}

