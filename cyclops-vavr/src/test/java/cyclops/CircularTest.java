package cyclops;


import cyclops.control.Eval;
import cyclops.control.State;
import cyclops.data.tuple.Tuple2;
import cyclops.reactive.collections.mutable.ListX;
import lombok.Value;
import org.junit.Test;

import static cyclops.CircularTest.TotalAndLength.totalAndLength;
import static cyclops.data.tuple.Tuple.tuple;
import static cyclops.reactive.collections.mutable.ListX.empty;

/**
 * Created by johnmcclean on 27/07/2017.
 */
public class CircularTest {

  @Test
  public void test() {
    System.out.println(minumum(ListX.of(1, 2, 3, 4, 5, 6, 7).shuffle()));
    System.out.println(minimumMutable(ListX.of(1, 2, 3, 4, 5, 6, 7).shuffle()));
    System.out.println(average(ListX.of(1, 2, 3, 4, 5, 6, 7)));

    System.out.println(subAverage(ListX.of(1, 2, 3, 4, 5, 6, 7)));

    System.out.println(ListX.of(1, 2, 3, 4, 5, 6, 7).doubleStats(d -> d).getAverage());
  }

  static class Unit {

  }

  public ListX<Integer> minimumMutable(ListX<Integer> ints) {
    State<Unit, Integer> state[] = new State[1];
    state[0] = State.constant(Integer.MAX_VALUE);

    state[0].map(s -> s + 1);

    return ints.map(a -> {
      state[0] = state[0].map(b -> (a < b) ? a : b);
      return Eval.later(() -> state[0].run(new Unit())._2());
    }).map(s -> s.get());

  }

  public ListX<Integer> minumum(ListX<Integer> ints) {


    Tuple2<Integer, ListX<Eval<Integer>>> data[] = new Tuple2[1];

    Eval<Integer> minimum = Eval.later(() -> data[0]._1());

    data[0] = ints.map(i -> tuple(i, minimum)).foldLeft(tuple(Integer.MAX_VALUE, empty()), (a, b) ->
      a._1() < b._1() ? tuple(a._1(), a._2().plus(minimum)) : tuple(b._1(), a._2().plus(minimum))
    );
    return data[0]._2().map(Eval::get);
  }

  @Value
  static class TotalAndLength {
    final int total;
    final int length;

    public static TotalAndLength totalAndLength(int total, int length) {
      return new TotalAndLength(total, length);
    }
  }

  ;

  public ListX<Double> average(ListX<Integer> ints) {

    Tuple2<TotalAndLength, ListX<Eval<Double>>> data[] = new Tuple2[1];

    Eval<Double> average = Eval.later(() -> (double) data[0]._1().total / data[0]._1().length);

    data[0] = ints.map(i -> tuple(totalAndLength(i, 1), average)).foldLeft(tuple(totalAndLength(0, 0), empty()), (a, b) ->
      tuple(totalAndLength(a._1().total + b._1().total, a._1().length + 1), a._2().plus(average))
    );
    return data[0]._2().map(Eval::get);
  }

  public ListX<Double> subAverage(ListX<Integer> ints) {

    Tuple2<TotalAndLength, ListX<Eval<Double>>> data[] = new Tuple2[1];

    Eval<Double> average = Eval.later(() -> (double) data[0]._1().total / data[0]._1().length);


    data[0] = ints.map(i -> tuple(totalAndLength(i, 1), average)).foldLeft(tuple(totalAndLength(0, 0), empty()),
      (acc, current) ->
        tuple(totalAndLength(acc._1().total + current._1().total, acc._1().length + 1),
          acc._2().plus(average.map(avgValue -> current._1().total - avgValue)))
    );
    return data[0]._2().map(Eval::get);
  }

}
