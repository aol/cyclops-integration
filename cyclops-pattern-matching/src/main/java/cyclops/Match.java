package cyclops;


import com.aol.cyclops2.types.anyM.AnyMSeq;
import com.aol.cyclops2.types.anyM.AnyMValue;
import cyclops.async.Future;
import cyclops.async.adapters.Adapter;
import cyclops.async.adapters.Queue;
import cyclops.async.adapters.Topic;
import cyclops.collections.adt.Seq;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Xor;
import cyclops.control.lazy.Either;
import cyclops.monads.AnyM;
import cyclops.monads.WitnessType;
import cyclops.patterns.*;
import org.jooq.lambda.tuple.Tuple;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Match {

    static <T> CaseClass1<T> on(T one){
        return ()->Tuple.tuple(one);
    }

    static <T1,T2> CaseClass2<T1,T2> on(T1 one,T2 two){
        return ()->Tuple.tuple(one,two);
    }

    static <T1,T2,T3> CaseClass3<T1,T2,T3> on(T1 one,T2 two,T3 three){
        return ()->Tuple.tuple(one,two,three);
    }

    static <T1,T2,T3,T4> CaseClass4<T1,T2,T3,T4> on(T1 one,T2 two,T3 three, T4 four){
        return ()->Tuple.tuple(one,two,three,four);
    }

    static <T1,T2,T3,T4,T5> CaseClass5<T1,T2,T3,T4,T5> on(T1 one,T2 two,T3 three, T4 four,T5 five){
        return ()->Tuple.tuple(one,two,three,four,five);
    }

    static <T> Sealed1Or<T> nullable(T opt){

        return new Sealed1Or<T>() {
            @Override
            public <R> R match(Function<? super T, ? extends R> fn1, Supplier<? extends R> s) {
                Optional<R> o = Optional.ofNullable(opt).map(fn1);
                return o.orElseGet(s);
            }
        };
    }
    static <T> Sealed1Or<T> optional(Optional<T> opt){

        return new Sealed1Or<T>() {
            @Override
            public <R> R match(Function<? super T, ? extends R> fn1, Supplier<? extends R> s) {
                Optional<R> o = opt.map(fn1);
              return o.orElseGet(s);
            }
        };
    }
    static <T> Sealed1Or<T> maybe(Maybe<T> maybe){
        return new Sealed1Or<T>() {
            @Override
            public <R> R match(Function<? super T, ? extends R> fn1, Supplier<? extends R> s) {
                return maybe.visit(fn1,s);
            }
        };
    }
    static <T> Sealed2<T,Throwable> completableFuture(CompletableFuture<T> future){
        return new Sealed2<T, Throwable>() {
            @Override
            public <R> R match(Function<? super T, ? extends R> fn1, Function<? super Throwable, ? extends R> fn2) {
                return Future.of(future).visit((Function<T,R>)fn1,(Function<Throwable,R>)fn2);
            }
        };
    }
    static <T> Sealed2<T,Throwable> future(Future<T> future){
        return new Sealed2<T, Throwable>() {
            @Override
            public <R> R match(Function<? super T, ? extends R> fn1, Function<? super Throwable, ? extends R> fn2) {
                return future.visit((Function<T,R>)fn1,(Function<Throwable,R>)fn2);
            }
        };
    }

    static <T, X extends Throwable> Sealed2<T,X> tryResult(Try<T,X> t){
        return new Sealed2<T, X>() {
            @Override
            public <R> R match(Function<? super T, ? extends R> fn1, Function<? super X, ? extends R> fn2) {
                return t.visit(fn1,fn2);
            }
        };
    }
    static <L,R> Sealed2<L,R> xor(Xor<L,R> xor){
        return new Sealed2<L, R>() {
            @Override
            public <R1> R1 match(Function<? super L, ? extends R1> fn1, Function<? super R, ? extends R1> fn2) {
                return xor.visit(fn1,fn2);
            }
        };
    }
    static <L,R> Sealed2<L,R> either(Either<L,R> xor){
        return new Sealed2<L, R>() {
            @Override
            public <R1> R1 match(Function<? super L, ? extends R1> fn1, Function<? super R, ? extends R1> fn2) {
                return xor.visit(fn1,fn2);
            }
        };
    }
    public static <T> Sealed2<Queue<T>, Topic<T>> adapter(final Adapter<T> adapter) {
        return  new Sealed2<Queue<T>, Topic<T>>() {
            @Override
            public <R> R match(Function<? super Queue<T>, ? extends R> fn1, Function<? super Topic<T>, ? extends R> fn2) {
                return adapter.visit(fn1,fn2);
            }
        };
    }
    public static <X extends Throwable> CaseClass4<Class, String, Throwable, Seq<StackTraceElement>> throwable(final X t) {
        return () -> Tuple.tuple(t.getClass(), t.getMessage(), t.getCause(), Seq.of(t.getStackTrace()));
    }

    public static <W extends WitnessType<W>,T> Sealed2<AnyMValue<W,T>, AnyMSeq<W,T>> anyM(final AnyM<W,T> anyM) {
        return new Sealed2<AnyMValue<W, T>, AnyMSeq<W, T>>() {
            @Override
            public <R> R match(Function<? super AnyMValue<W, T>, ? extends R> fn1, Function<? super AnyMSeq<W, T>, ? extends R> fn2) {
                return anyM.matchable().visit(fn1,fn2);
            }
        };

    }
    public static CaseClass5<String, String, Integer, String, String> url(final URL url) {
        return ()->Tuple.tuple(url.getProtocol(), url.getHost(),  url.getPort(),  url.getPath(), url.getQuery());
    }
    public static Seq<String> words(final CharSequence seq) {
        return Seq.of(seq.toString()
                .split(" "));
    }

    public static Seq<Character> chars(final CharSequence chars) {
        final Iterable<Character> it = () -> chars.chars()
                .boxed()
                .map(i -> Character.toChars(i)[0])
                .iterator();
        return Seq.fromIterator(it.iterator());
    }


    public static CaseClass3<Integer, Integer, Integer> dateDDMMYYYY(final Date date) {
        final Date input = new Date();
        final LocalDate local = input.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDateDDMMYYYY(local);
    }

    public static CaseClass3<Integer, Integer, Integer> dateMMDDYYYY(final Date date) {
        final Date input = new Date();
        final LocalDate local = input.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDateMMDDYYYY(local);
    }

    public static CaseClass3<Integer, Integer, Integer> localDateDDMMYYYY(final LocalDate date) {
        return ()->Tuple.tuple(date.getDayOfMonth(), date.getMonth().getValue(), date.getYear());
    }

    public static CaseClass3<Integer, Integer, Integer> localDateMMDDYYYY(final LocalDate date) {
        return ()->Tuple.tuple(date.getMonth().getValue(), date.getDayOfMonth(),date.getYear());
    }

    /**
     * Structural pattern matching on a Date's hour minutes and seconds component
     *
     * @param date Date to match on
     * @return Structural pattern matcher for hours / minutes / seconds
     */
    public static CaseClass3<Integer, Integer, Integer> dateHMS(final Date date) {
        final Date input = new Date();
        final LocalTime local = input.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
        return localTimeHMS(local);
    }

    public static CaseClass3<Integer, Integer, Integer> localTimeHMS(final LocalTime time) {
        return ()->Tuple.tuple(time.getHour(), time.getMinute(), time.getSecond());
    }

    public static <T> Sealed2<BlockingQueue<T>, java.util.Queue<T>> blocking(final java.util.Queue<T> queue) {

        return new Sealed2<BlockingQueue<T>, java.util.Queue<T>>() {
            @Override
            public <R> R match(Function<? super BlockingQueue<T>, ? extends R> fn1, Function<? super java.util.Queue<T>, ? extends R> fn2) {
                return queue instanceof BlockingQueue ? fn1.apply((BlockingQueue<T>) queue) : fn2.apply(queue);
            }
        };
    }
}
