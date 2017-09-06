package cyclops.collections.adt;

import cyclops.control.Maybe;
import cyclops.stream.ReactiveSeq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Wither;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor
@Getter
@Wither
public class Zipper<T> {

    private final LazyList<T> left;
    private final T point;
    private final LazyList<T> right;


    public static <T> Zipper<T> of(LazyList<T> left, T value, LazyList<T> right){
        return new Zipper<>(left,value,right);
    }
    public static <T> Zipper of(ReactiveSeq<T> left, T value, ReactiveSeq<T> right){
        return new Zipper<>(LazyList.fromStream(left),value,LazyList.fromStream(right));
    }

    public boolean isStart(){
        return left.isEmpty();
    }
    public boolean isEnd(){
        return right.isEmpty();
    }
    public <R> Zipper<R> map(Function<? super T, ? extends R> fn){
        return of(left.map(fn),fn.apply(point),right.map(fn));
    }
    public <R> Zipper<R> zip(Zipper<T> zipper, BiFunction<? super T, ? super T, ? extends R> fn){
        ReactiveSeq<R> newLeft = left.stream().zip(zipper.left.stream(),fn);
        R newPoint = fn.apply(point, zipper.point);
        ReactiveSeq<R> newRight = right.stream().zip(zipper.right.stream(),fn);
        return of(newLeft,newPoint,newRight);
    }

    public  Zipper<Tuple2<T,T>> zip(Zipper<T> zipper){
        return zip(zipper, Tuple::tuple);
    }

    public Zipper<T> start() {
        Maybe<Zipper<T>> result = Maybe.just(this);
        Maybe<Zipper<T>> next = result;
        while(next.isPresent()){
                next = result.flatMap(p->p.previous());
                if(next.isPresent())
                    result = next;

        }
        return result.orElse(this);
    }
    public Zipper<T> end() {
        Maybe<Zipper<T>> result = Maybe.just(this);
        Maybe<Zipper<T>> next = result;
        while(next.isPresent()){
            next = result.flatMap(p->p.next());
            if(next.isPresent())
                result = next;
        }
        return result.orElse(this);
    }
    public int index(){
        return left.size();
    }
    public Maybe<Zipper<T>> position(int index) {
        Zipper<T> result = this;
        while (index != result.index()) {
            if (result.index() < index && !result.isEnd()) {
                result = result.next(result);
            } else if (result.index() > index && !result.isStart()) {
                result = result.previous(result);
            } else {
                return Maybe.none();
            }
        }
        return Maybe.just(result);
    }
    public <R> Maybe<Zipper<T>>  next(){
        return right.match(c->Maybe.just(new Zipper(left.prepend(point), c.hashCode(), c.tail.get())), nil->Maybe.none());
    }
    public <R> Zipper<T> next(Zipper<T> alt){
        return next().orElse(alt);
    }
    public <R> Zipper<T> previous(Zipper<T> alt){
        return previous().orElse(alt);
    }
    public Zipper<T> cycleNext() {
        return left.match(cons->right.match(c->next().orElse(this),nil->{
            LazyList.Cons<T> reversed = cons.reverse();
            return of(LazyList.empty(),reversed.head,reversed.tail.get().append(point));
        }),nil->this);

    }
    public Zipper<T> cyclePrevious() {
        return right.match(cons->left.match(c->next().orElse(this),nil->{
            LazyList.Cons<T> reversed = cons.reverse();
            return of(reversed.tail.get().append(point),reversed.head,LazyList.empty());
        }),nil->this);
    }
    public <R> Maybe<Zipper<T>>  previous(){
        return left.match(c->Maybe.just(new Zipper(c.tail.get(),c.head ,right.prepend(point))), nil->Maybe.none());
    }

    public Zipper<T> left(T value){
        return new Zipper<>(left,value,right.prepend(value));
    }
    public Zipper<T> right(T value){
        return new Zipper<>(left.prepend(value),value,right);
    }
    public Zipper<T> deleteLeftAndRight() {
        return new Zipper<>(LazyList.empty(), point, LazyList.empty());
    }
    public Maybe<Zipper<T>> deleteLeft() {

        return left.match(c->right.match(c2->Maybe.just(of(c.tail.get(),c.head,right)),n->Maybe.just(of(c.tail.get(),c.head,right))),
                n->right.match(c->Maybe.just(of(left,c.head,c.tail.get())),n2->Maybe.none()));
    }
    public Maybe<Zipper<T>> deleteRight() {

        return right.match(c->left.match(c2->Maybe.just(of(left,c.head,c.tail.get())),n->Maybe.just(of(left,c.head,c.tail.get()))),
                n->left.match(c->Maybe.just(of(c.tail.get(),c.head,right)),n2->Maybe.none()));
    }



    public Zipper<T> filterLeft(Predicate<? super T> predicate) {
        return of(left.filter(predicate),point,right);
    }


    public Zipper<T> filterRight(Predicate<? super T> predicate) {
        return of(left,point,right.filter(predicate));
    }


    public Tuple2<LazyList<T>, LazyList<T>> split() {
        return Tuple.tuple(left, right);
    }
    public LazyList<T> lazyList(){
        return right.prepend(point).prependAll(left);
    }
}
