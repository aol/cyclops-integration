package cyclops.monads;


import com.oath.cyclops.types.anyM.AnyMSeq;
import com.oath.cyclops.types.anyM.AnyMValue;
import cyclops.async.Future;

import io.vavr.collection.*;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public class Vavr {





    public static <T> AnyMValue<VavrWitness.tryType,T> tryM(Try<T> tryM) {
        return AnyM.ofValue(tryM, VavrWitness.tryType.INSTANCE);
    }

    public static <R> AnyM<VavrWitness.either,R> either(Either<?,R> either) {
        return AnyM.ofValue(either, VavrWitness.either.INSTANCE);
    }
    public static <T> AnyMSeq<VavrWitness.stream,T> stream(Stream<T> stream) {
        return AnyM.ofSeq(stream, VavrWitness.stream.INSTANCE);
    }
    public static <T> AnyMSeq<VavrWitness.list,T> list(List<T> list) {
        return AnyM.ofSeq(list, VavrWitness.list.INSTANCE);
    }
    public static <T> AnyMSeq<VavrWitness.array,T> array(Array<T> array) {
        return AnyM.ofSeq(array, VavrWitness.array.INSTANCE);
    }
    public static <T> AnyMSeq<VavrWitness.vector,T> vector(Vector<T> vector) {
        return AnyM.ofSeq(vector, VavrWitness.vector.INSTANCE);
    }
    public static <T> AnyMSeq<VavrWitness.queue,T> queue(Queue<T> queue) {
        return AnyM.ofSeq(queue, VavrWitness.queue.INSTANCE);
    }

    public static <T> AnyMSeq<VavrWitness.charSeq,T> charSeq(CharSeq charSeq) {
        return AnyM.ofSeq(charSeq, VavrWitness.charSeq.INSTANCE);
    }
    public static <T> AnyMSeq<VavrWitness.hashSet,T> hashSet(HashSet<T> set) {
        return AnyM.ofSeq(set, VavrWitness.hashSet.INSTANCE);
    }
    public static <T> AnyMValue<VavrWitness.future,T> option(Future<T> option) {
        return AnyM.ofValue(option, VavrWitness.future.INSTANCE);
    }
    public static <T> AnyMValue<VavrWitness.option,T> option(Option<T> option) {
        return AnyM.ofValue(option, VavrWitness.option.INSTANCE);
    }

    public static <R> AnyMValue<VavrWitness.future,R> future(io.vavr.concurrent.Future<R> res) {
        return AnyM.ofValue(res, VavrWitness.future.INSTANCE);
    }

}
