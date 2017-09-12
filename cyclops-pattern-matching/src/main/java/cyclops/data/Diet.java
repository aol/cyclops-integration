package cyclops.data;

import cyclops.control.Maybe;
import cyclops.control.Trampoline;
import cyclops.patterns.CaseClass3;
import cyclops.patterns.Sealed2;
import cyclops.typeclasses.Enumeration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;


import java.util.Comparator;
import java.util.function.Function;

import static cyclops.control.Trampoline.done;
import static cyclops.control.Trampoline.more;
import static org.jooq.lambda.tuple.Tuple.tuple;

//Discrete Interval Encoded Tree
public interface Diet<T> extends Sealed2<Diet.Node<T>,Diet.Nil<T>> {

    public static <T> Diet<T> empty(){
        return Nil.INSTANCE;
    }
    public static <T> Diet<T> cons(Range<T> focus){
        return cons(empty(),focus,empty());
    }
    public static <T> Diet<T> cons(Diet<T> left,Range<T> focus,Diet<T> right){
        return new Node(left,focus,right);
    }
    default boolean contains(T value){
        return containsRec(value).result();
    }
    default boolean contains(Range<T> range){
        return containsRec(range).result();
    }
    default Diet<T> add(T value, Enumeration<T> enm, Comparator<? super T> comp){
        return add(Range.range(value,enm.succ(value).get(),enm,comp));
    }
    Trampoline<Boolean> containsRec(T value);
    Trampoline<Boolean> containsRec(Range<T> range);

    Diet<T> add(Range<T> range);


    boolean isEmpty();
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Node<T> implements Diet<T>,CaseClass3<Diet<T>,Range<T>,Diet<T>>{
        private final Diet<T> left;
        private final Range<T> focus;
        private final Diet<T> right;

        @Override
        public Trampoline<Boolean> containsRec(T value) {
            if(focus.ordering().isLessThan(value,focus.start))
                return left.containsRec(value);
            if(focus.ordering().isGreaterThan(value,focus.end))
                return right.containsRec(value);
            return done(focus.contains(value));

        }

        @Override
        public Trampoline<Boolean> containsRec(Range<T> range) {
            if(focus.contains(range))
                 return done(true);
            if(focus.ordering().isLessThan(range.start,focus.start))
                return left.containsRec(range);
            return right.containsRec(range);
        }
        private Trampoline<Tuple2<Diet<T>,T>> findRightAndEndingPoint(T value){
            //            value
            //                    start         (This is right Diet, end at old start)
            if(focus.ordering().isLessThan(value,focus.start)) {
                return done(tuple(this, value));
            }
            //            value
            //                   end
            //    start                 (right is right diet, end new at old end)
            if(focus.ordering().isLessThanOrEqual(value,focus.end))
                return done(tuple(right,focus.end));
            //             value
            //                                  end
            //                    start              (split rightwad diet recursively)
            return left.match(p->p.findRightAndEndingPoint(value),leftNil->done(tuple(leftNil,value)));


        }

        private Trampoline<Tuple2<Diet<T>,T>> findLeftAndStartingPoint(T value){
               //            value
               //       end         (This is leftward Diet, start new at end)
               if(focus.ordering().isGreaterThan(value,focus.end)) {
                   return done(tuple(this, value));
               }
               //                        value
               //                                 end
               //<-- left  -->    start                 (Left is leftward diet, start new at start)
               if(focus.ordering().isGreaterThanOrEqual(value,focus.start))
                   return done(tuple(left,focus.start));
               //             value
               //                                  end
               //   <--     left         --> start               (split leftward diet recursively)
               return left.match(p->p.findLeftAndStartingPoint(value),leftNil->done(tuple(leftNil,value)));


        }
        @Override
        public Diet<T> add(Range<T> range) {
            Tuple2<Range<T>, Maybe<Range<T>>> t = focus.plusAll(range);
            return t.v2.visit(s-> t.v1==focus? cons(left,focus,right.add(s)) : cons(left.add(s),focus,right),()->{

                //create new expanded range and rebalance the trees
                Tuple2<Diet<T>,T> leftAndStart = left.match(l->l.findLeftAndStartingPoint(t.v1.start).get(),n->tuple(n,t.v1.start));
                Tuple2<Diet<T>,T> rightAndEnd = right.match(l->l.findRightAndEndingPoint(t.v1.end).get(),n->tuple(n,t.v1.start));

                return cons(leftAndStart.v1, Range.range(leftAndStart.v2, rightAndEnd.v2, focus.enumeration(), focus.ordering()), rightAndEnd.v1);

            });
        }


        public boolean isEmpty(){
            return false;
        }

        @Override
        public <R> R match(Function<? super Node<T>, ? extends R> fn1, Function<? super Nil<T>, ? extends R> fn2) {
            return fn1.apply(this);
        }

        @Override
        public Tuple3<Diet<T>, Range<T>, Diet<T>> unapply() {
            return tuple(left,focus,right);
        }
    }
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Nil<T> implements Diet<T>{
        public final static Nil INSTANCE = new Nil();



        @Override
        public Trampoline<Boolean> containsRec(T value) {
            return done(false);
        }

        @Override
        public Trampoline<Boolean> containsRec(Range<T> range) {
            return done(false);
        }

        @Override
        public Diet<T> add(Range<T> range) {
            return Diet.cons(range);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public <R> R match(Function<? super Node<T>, ? extends R> fn1, Function<? super Nil<T>, ? extends R> fn2) {
            return fn2.apply(this);
        }
    }
}
