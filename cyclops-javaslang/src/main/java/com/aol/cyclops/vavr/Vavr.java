package com.aol.cyclops.vavr;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;


import com.aol.cyclops.vavr.VavrWitness.*;
import com.aol.cyclops2.types.anyM.AnyMSeq;
import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.async.Future;
import cyclops.async.Queue;
import cyclops.control.Eval;
import cyclops.control.Maybe;
import cyclops.monads.AnyM;
import cyclops.monads.AnyM2;
import javaslang.Lazy;
import javaslang.Value;
import javaslang.collection.*;
import javaslang.control.Either;
import javaslang.control.Either.LeftProjection;
import javaslang.control.Either.RightProjection;
import javaslang.control.Option;
import javaslang.control.Try;

public class Vavr {
    




    public static <T> AnyMValue<tryType,T> tryM(Try<T> tryM) {
        return AnyM.ofValue(tryM, tryType.INSTANCE);
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
    public static <T> AnyMSeq<vector,T> vector(Vector<T> vector) {
        return AnyM.ofSeq(vector, VavrWitness.vector.INSTANCE);
    }
    public static <T> AnyMSeq<queue,T> queue(Queue<T> queue) {
        return AnyM.ofSeq(queue, VavrWitness.queue.INSTANCE);
    }

    public static <T> AnyMSeq<charSeq,T> charSeq(CharSeq charSeq) {
        return AnyM.ofSeq(charSeq, VavrWitness.charSeq.INSTANCE);
    }
    public static <T> AnyMSeq<hashSet,T> hashSet(HashSet set) {
        return AnyM.ofSeq(set, hashSet.INSTANCE);
    }
    public static <T> AnyMValue<future,T> option(Future<T> option) {
        return AnyM.ofValue(option, VavrWitness.future.INSTANCE);
    }
    public static <T> AnyMValue<option,T> option(Option<T> option) {
        return AnyM.ofValue(option, VavrWitness.option.INSTANCE);
    }

    public static <R> AnyM<future,R> future(javaslang.concurrent.Future<R> res) {
        return AnyM.ofValue(res, future.INSTANCE);
    }

}
