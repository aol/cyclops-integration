package cyclops.collections.adt;


import com.aol.cyclops2.types.Filters;
import com.aol.cyclops2.types.functor.Transformable;
import cyclops.collections.immutable.LinkedListX;
import cyclops.control.Maybe;
import cyclops.control.Trampoline;
import cyclops.patterns.CaseClass2;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of={"head,tail"})
public class NonEmptyList<T> implements CaseClass2<T,ImmutableList<T>>, ImmutableList<T>, ImmutableList.Some<T> {

    private final T head;
    private final ImmutableList<T> tail;

    public ReactiveSeq<T> stream(){
        return ReactiveSeq.fromIterable(this);
    }
    public LinkedListX<T> linkedListX(){
        return LinkedListX.fromIterable(this);
    }
    public static <T> NonEmptyList<T> of(T head, T... value){
        Seq<T> list = Seq.of(value);
        return cons(head,list);
    }
    public static <T> NonEmptyList<T> of(T head){
        Seq<T> list = Seq.empty();
        return cons(head,list);
    }
    public static <T> NonEmptyList<T> of(T head, ImmutableList<T> list){
        return cons(head,list);
    }


    public Maybe<T> get(int pos){
        if(pos==0)
            return Maybe.of(head);
        return tail.get(pos);

    }

    @Override
    public T getOrElse(int pos, T alt) {
        return get(pos).orElse(alt);
    }

    @Override
    public T getOrElseGet(int pos, Supplier<T> alt) {
        return get(pos).orElseGet(alt);
    }

    public LazySeq<T> asList(){
        return LazySeq.lazy(head,()->tail);
    }

    @Override
    public ImmutableList<T> drop(int num) {
        return tail.match(s ->  of(s.head(), s.tail()), n -> n);
    }

    @Override
    public ImmutableList<T> take(int num) {
        if(num==0){
            return LazySeq.empty();
        }
        return cons(head,tail.take(num-1));
    }

    public NonEmptyList<T> prepend(T value){
        return cons(value,asList());
    }

    @Override
    public NonEmptyList<T> prependAll(Iterable<T> value) {
        LazySeq<T> list = asList().prependAll(value);
        return cons(list.match(c->c.head(),nil->null),list.drop(1));
    }

    @Override
    public ImmutableList<T> append(T value) {
        return of(head,tail.append(value));
    }

    @Override
    public ImmutableList<T> appendAll(Iterable<T> value) {
        return of(head,tail.appendAll(value));
    }

    @Override
    public ImmutableList<T> tail() {
        return tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public NonEmptyList<T> reverse() {
        return of(head).prependAll(tail);
    }

    public NonEmptyList<T> prependAll(NonEmptyList<T> value){
        return value.prependAll(this);
    }

    public ImmutableList<T> filter(Predicate<? super T> pred){
        return asList().filter(pred);
    }

    @Override
    public <U> NonEmptyList<U> cast(Class<? extends U> type) {
        return (NonEmptyList<U>)ImmutableList.Some.super.cast(type);
    }

    public <R> NonEmptyList<R> map(Function<? super T, ? extends R> fn) {
        ImmutableList<R> list = asList().map(fn);
        return list.nonEmptyList().get();
    }

    @Override
    public NonEmptyList<T> peek(Consumer<? super T> c) {
        return (NonEmptyList<T>)ImmutableList.Some.super.peek(c);
    }

    @Override
    public <R> NonEmptyList<R> trampoline(Function<? super T, ? extends Trampoline<? extends R>> mapper) {
        return (NonEmptyList<R>)ImmutableList.Some.super.trampoline(mapper);
    }

    @Override
    public <R> NonEmptyList<R> retry(Function<? super T, ? extends R> fn) {
        return (NonEmptyList<R>)ImmutableList.Some.super.retry(fn);
    }

    @Override
    public <R> NonEmptyList<R> retry(Function<? super T, ? extends R> fn, int retries, long delay, TimeUnit timeUnit) {
        return (NonEmptyList<R>)ImmutableList.Some.super.retry(fn,retries,delay,timeUnit);
    }

    @Override
    public <R> R match(Function<? super Some<T>, ? extends R> fn1, Function<? super None<T>, ? extends R> fn2) {
        return fn1.apply(this);
    }

    public <R> NonEmptyList<R> flatMap(Function<? super T, ? extends NonEmptyList<R>> fn) {
        ImmutableList<R> l = asList().flatMap(fn.andThen(a -> a.asList()));
        return l.nonEmptyList().get();
    }

    public <R> R foldRight(R zero,BiFunction<? super T, ? super R, ? extends R> f) {
        return asList().foldRight(zero,f);

    }
    public <R> R foldLeft(R zero,BiFunction<R, ? super T, R> f) {
        return asList().foldLeft(zero,f);
    }

    public int size(){
        return 1+tail.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public static <T> NonEmptyList<T> cons(T value, ImmutableList<T> tail){
        return new NonEmptyList<>(value,tail);
    }



    @Override
    public Tuple2<T, ImmutableList<T>> unapply() {
        return Tuple.tuple(head,tail);
    }
}
