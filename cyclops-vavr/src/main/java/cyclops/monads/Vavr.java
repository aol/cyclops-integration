package cyclops.monads;


import com.oath.anym.AnyMSeq;
import com.oath.anym.AnyMValue;


import cyclops.control.Future;
import cyclops.monads.VavrWitness.*;
import cyclops.monads.VavrWitness.hashSet;
import cyclops.monads.VavrWitness.option;
import io.vavr.collection.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class Vavr {





    public static <T> AnyMValue<tryType,T> tryM(Try<T> tryM) {
        return AnyM.ofValue(tryM, VavrWitness.tryType.INSTANCE);
    }

    public static <R> AnyM<either,R> either(Either<?,R> either) {
        return AnyM.ofValue(either, VavrWitness.either.INSTANCE);
    }
    public static <T> AnyMSeq<stream,T> stream(Stream<T> stream) {
        return AnyM.ofSeq(stream, VavrWitness.stream.INSTANCE);
    }
    public static <T> AnyMSeq<list,T> list(List<T> list) {
        return AnyM.ofSeq(list, VavrWitness.list.INSTANCE);
    }
    public static <T> AnyMSeq<array,T> array(Array<T> array) {
        return AnyM.ofSeq(array, VavrWitness.array.INSTANCE);
    }
    public static <T> AnyMSeq<vector,T> vector(Vector<T> vector) {
        return AnyM.ofSeq(vector, VavrWitness.vector.INSTANCE);
    }
    public static <T> AnyMSeq<queue,T> queue(Queue<T> queue) {
        return AnyM.ofSeq(queue, VavrWitness.queue.INSTANCE);
    }

    public static <T> AnyMSeq<charSeq,T> charSeq(CharSeq charSeq) {
        return AnyM.ofSeq(charSeq, VavrWitness.charSeq.INSTANCE);
    }
    public static <T> AnyMSeq<hashSet,T> hashSet(HashSet<T> set) {
        return AnyM.ofSeq(set, hashSet.INSTANCE);
    }
    public static <T> AnyMValue<future,T> future(Future<T> option) {
        return AnyM.ofValue(option, future.INSTANCE);
    }
    public static <T> AnyMValue<option,T> option(Option<T> option) {
        return AnyM.ofValue(option, VavrWitness.option.INSTANCE);
    }

    public static <R> AnyMValue<future,R> future(io.vavr.concurrent.Future<R> res) {
        return AnyM.ofValue(res, future.INSTANCE);
    }

}
