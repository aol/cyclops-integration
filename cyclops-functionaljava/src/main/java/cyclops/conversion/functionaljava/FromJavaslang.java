package cyclops.conversion.functionaljava;

import java.util.stream.Stream;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import io.vavr.Function4;
import io.vavr.Function5;
import io.vavr.Function6;
import io.vavr.Function7;
import io.vavr.Function8;
import io.vavr.Tuple1;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import io.vavr.Tuple5;
import io.vavr.Tuple6;
import io.vavr.Tuple7;
import io.vavr.Tuple8;
import io.vavr.control.Either;
import io.vavr.control.Option;
import fj.F;
import fj.F2;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.F7;
import fj.F8;
import fj.P;
import fj.P1;
import fj.P2;
import fj.P3;
import fj.P4;
import fj.P5;
import fj.P6;
import fj.P7;
import fj.P8;

public class FromJavaslang {
    public static <T, R> F<T, R> f1(Function1<T, R> fn) {
        return (t) -> fn.apply(t);
    }

    public static <T, X, R> F2<T, X, R> f2(Function2<T, X, R> fn) {
        return (t, x) -> fn.apply(t, x);
    }

    public static <T1, T2, T3, R> F3<T1, T2, T3, R> f3(Function3<T1, T2, T3, R> fn) {
        return (t1, t2, t3) -> fn.apply(t1, t2, t3);
    }

    public static <T1, T2, T3, T4, R> F4<T1, T2, T3, T4, R> f4(Function4<T1, T2, T3, T4, R> fn) {
        return (t1, t2, t3, t4) -> fn.apply(t1, t2, t3, t4);
    }

    public static <T1, T2, T3, T4, T5, R> F5<T1, T2, T3, T4, T5, R> f5(Function5<T1, T2, T3, T4, T5, R> fn) {
        return (t1, t2, t3, t4, t5) -> fn.apply(t1, t2, t3, t4, t5);
    }

    public static <T1, T2, T3, T4, T5, T6, R> F6<T1, T2, T3, T4, T5, T6, R> f6(
            Function6<T1, T2, T3, T4, T5, T6, R> fn) {
        return (t1, t2, t3, t4, t5, t6) -> fn.apply(t1, t2, t3, t4, t5, t6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, R> F7<T1, T2, T3, T4, T5, T6, T7, R> f7(
            Function7<T1, T2, T3, T4, T5, T6, T7, R> fn) {
        return (t1, t2, t3, t4, t5, t6, t7) -> fn.apply(t1, t2, t3, t4, t5, t6, t7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> F8<T1, T2, T3, T4, T5, T6, T7, T8, R> f8(
            Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> fn) {
        return (t1, t2, t3, t4, t5, t6, t7, t8) -> fn.apply(t1, t2, t3, t4, t5, t6, t7, t8);
    }

    public static <T> fj.data.Option<T> option(Option<T> o) {
        if (o.isEmpty())
            return fj.data.Option.none();
        return fj.data.Option.some(o.get());
    }

    public static <L, R> fj.data.Either<L, R> either(Either<L, R> e) {
        if (e.isLeft())
            return fj.data.Either.left(e.left()
                                        .get());
        else
            return fj.data.Either.right(e.right()
                                         .get());
    }

    public static <T1> P1<T1> tuple(Tuple1<T1> t) {
        return P.p(t._1);
    }

    public static <T1, T2> P2<T1, T2> tuple(Tuple2<T1, T2> t) {
        return P.p(t._1, t._2);
    }

    public static <T1, T2, T3> P3<T1, T2, T3> tuple(Tuple3<T1, T2, T3> t) {
        return P.p(t._1, t._2, t._3);
    }

    public static <T1, T2, T3, T4> P4<T1, T2, T3, T4> tuple(Tuple4<T1, T2, T3, T4> t) {
        return P.p(t._1, t._2, t._3, t._4);
    }

    public static <T1, T2, T3, T4, T5> P5<T1, T2, T3, T4, T5> tuple(Tuple5<T1, T2, T3, T4, T5> t) {
        return P.p(t._1, t._2, t._3, t._4, t._5);
    }

    public static <T1, T2, T3, T4, T5, T6> P6<T1, T2, T3, T4, T5, T6> tuple(Tuple6<T1, T2, T3, T4, T5, T6> t) {
        return P.p(t._1, t._2, t._3, t._4, t._5, t._6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> P7<T1, T2, T3, T4, T5, T6, T7> tuple(
            Tuple7<T1, T2, T3, T4, T5, T6, T7> t) {
        return P.p(t._1, t._2, t._3, t._4, t._5, t._6, t._7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> P8<T1, T2, T3, T4, T5, T6, T7, T8> tuple(
            Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> t) {
        return P.p(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8);
    }

}