package cyclops.conversion.vavr;

import java.util.stream.Stream;


import com.oath.cyclops.types.MonadicValue;
import com.oath.cyclops.types.Value;
import cyclops.control.Eval;
import cyclops.control.Either;
import io.vavr.*;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;


public class FromCyclopsReact {
    public static <T> Lazy<T> eval(Eval<T> opt){
        return Lazy.of(opt);
    }
    public static <T> Future<T> future(cyclops.async.Future<T> future){
        Promise<T> result =  Promise.make();

        future.peek(n->result.complete(Try.success(n)));
        return result.future();
    }
    public static <T> io.vavr.collection.Stream<T> fromStream(Stream<T> s) {
        return io.vavr.collection.Stream.ofAll(() -> s.iterator());
    }
  public static <T,X extends Throwable> Try<T> toTry(cyclops.control.Try<T,X> value) {
    return value
      .visit(s -> Try.success(s), f -> Try.failure(f));

  }
    public static <T> Try<T> toTry(MonadicValue<T> value) {
        return value.toTry()
                    .visit(s -> Try.success(s), f -> Try.failure(f));

    }

    public static <T> Future<T> future(MonadicValue<T> value) {
        return Future.of(() -> value.orElse(null));
    }
    public static <T> Lazy<T> lazy(Value<T> value){
        return Lazy.of(()->value.orElse(null));
    }

    public static <T> Option<T> option(Value<T> value){
        return value.visit(Option::some, Option::none);
     }


    public static <L, R> io.vavr.control.Either<L, R> either(cyclops.control.Either<L, R> xor) {

        return xor.visit(l -> io.vavr.control.Either.left(l), r -> io.vavr.control.Either.right(r));
    }

    public static <L, R> Validation<L, R> validation(cyclops.control.Either<L, R> value) {
        return Validation.fromEither(either(value));
    }

  public static <T, R> Function1<T, R> f1(cyclops.function.Function1<T, R> fn) {
    return (t) -> fn.apply(t);
  }

  public static <T, X, R> Function2<T, X, R> f2(cyclops.function.Function2<T, X, R> fn) {
    return (t, x) -> fn.apply(t, x);
  }

  public static <T1, T2, T3, R> Function3<T1, T2, T3, R> f3(cyclops.function.Function3<T1, T2, T3, R> fn) {
    return (t1, t2, t3) -> fn.apply(t1, t2, t3);
  }

  public static <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> f4(
    cyclops.function.Function4<T1, T2, T3, T4, R> fn) {
    return (t1, t2, t3, t4) -> fn.apply(t1, t2, t3, t4);
  }

  public static <T1, T2, T3, T4, T5, R> Function5<T1, T2, T3, T4, T5, R> f5(
    cyclops.function.Function5<T1, T2, T3, T4, T5, R> fn) {
    return (t1, t2, t3, t4, t5) -> fn.apply(t1, t2, t3, t4, t5);
  }

  public static <T1, T2, T3, T4, T5, T6, R> Function6<T1, T2, T3, T4, T5, T6, R> f6(
    cyclops.function.Function6<T1, T2, T3, T4, T5, T6, R> fn) {
    return (t1, t2, t3, t4, t5, t6) -> fn.apply(t1, t2, t3, t4, t5, t6);
  }

  public static <T1, T2, T3, T4, T5, T6, T7, R> Function7<T1, T2, T3, T4, T5, T6, T7, R> f7(
    cyclops.function.Function7<T1, T2, T3, T4, T5, T6, T7, R> fn) {
    return (t1, t2, t3, t4, t5, t6, t7) -> fn.apply(t1, t2, t3, t4, t5, t6, t7);
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> f8(
    cyclops.function.Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> fn) {
    return (t1, t2, t3, t4, t5, t6, t7, t8) -> fn.apply(t1, t2, t3, t4, t5, t6, t7, t8);
  }

  public static Tuple0 tuple(cyclops.data.tuple.Tuple0 t) {
    return Tuple0.instance();
  }

  public static <T1> Tuple1<T1> tuple(cyclops.data.tuple.Tuple1<T1> t) {
    return new Tuple1<>(t._1());
  }

  public static <T1, T2> Tuple2<T1, T2> tuple(cyclops.data.tuple.Tuple2<T1, T2> t) {
    return new Tuple2<>(t._1(), t._2());
  }

  public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(cyclops.data.tuple.Tuple3<T1, T2, T3> t) {
    return new Tuple3<>(t._1(), t._2(), t._3());
  }

  public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tuple(cyclops.data.tuple.Tuple4<T1, T2, T3, T4> t) {
    return new Tuple4<>(t._1(), t._2(), t._3(), t._4());
  }

  public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> tuple(
    cyclops.data.tuple.Tuple5<T1, T2, T3, T4, T5> t) {
    return new Tuple5<>(t._1(), t._2(), t._3(), t._4(), t._5());
  }

  public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> tuple(
    cyclops.data.tuple.Tuple6<T1, T2, T3, T4, T5, T6> t) {
    return new Tuple6<>(t._1(), t._2(), t._3(), t._4(), t._5(), t._6());
  }

  public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple(
    cyclops.data.tuple.Tuple7<T1, T2, T3, T4, T5, T6, T7> t) {
    return new Tuple7<>(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7());
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> tuple(
    cyclops.data.tuple.Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> t) {
    return new Tuple8<>(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7(), t._8());
  }


}
