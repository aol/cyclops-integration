package cyclops.collections.adt;


import com.aol.cyclops2.types.Filters;
import cyclops.patterns.Sealed2;
import cyclops.stream.ReactiveSeq;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class LazyString implements Sealed2<LazySeq.Cons<Character>,LazySeq.Nil>,Filters<Character> {
    private final LazySeq<Character> string;

    private static final LazyString Nil = fromLazyList(LazySeq.empty());
    public static LazyString fromLazyList(LazySeq<Character> string){
        return new LazyString(string);
    }
    public static LazyString of(CharSequence seq){
        return fromLazyList(LazySeq.fromStream( seq.chars().mapToObj(i -> (char) i)));
    }

    public static LazyString empty(){
        return Nil;
    }

    public LazyString op(Function<? super LazySeq<Character>, ? extends LazySeq<Character>> custom){
        return fromLazyList(custom.apply(string));
    }

    public LazyString substring(int start){
        return drop(start);
    }
    public LazyString substring(int start, int end){
        return drop(start).take(end-start);
    }
    public LazyString toUpperCase(){
        return fromLazyList(string.map(c->c.toString().toUpperCase().charAt(0)));
    }
    public LazyString toLowerCase(){
        return fromLazyList(string.map(c->c.toString().toLowerCase().charAt(0)));
    }
    public LazySeq<LazyString> words() {
        return string.split(t -> t.equals(' ')).map(l->fromLazyList(l));
    }
    public LazySeq<LazyString> lines() {
        return string.split(t -> t.equals('\n')).map(l->fromLazyList(l));
    }
    public LazyString map(Function<Character,Character> fn){
        return fromLazyList(string.map(fn));
    }
    public LazyString flatMap(Function<Character,LazyString> fn){
        return fromLazyList(string.flatMap(fn.andThen(s->s.string)));
    }
    @Override
    public LazyString filter(Predicate<? super Character> predicate) {
        return fromLazyList(string.filter(predicate));
    }
    public ReactiveSeq<Character> stream(){
        return string.stream();
    }
    public LazyString take(final int n) {
        return fromLazyList(string.take(n));

    }
    public LazyString  drop(final int num) {
        return fromLazyList(string.drop(num));
    }
    public LazyString  reverse() {
        return fromLazyList(string.reverse());
    }
    public Optional<Character> get(int pos){
        return string.get(pos);
    }
    public LazyString prepend(Character value){
        return fromLazyList(string.prepend(value));
    }
    public LazyString prependAll(LazyString value){
        return fromLazyList(string.prependAll(value.string));
    }
    public LazyString append(String s){
        return fromLazyList(string.appendAll(LazySeq.fromStream( s.chars().mapToObj(i -> (char) i))));
    }
    public int size(){
        return length();
    }
    public int length(){
        return string.size();
    }
    public String toString(){
        return string.stream().join("");
    }
    @Override
    public <R> R match(Function<? super LazySeq.Cons<Character>, ? extends R> fn1, Function<? super LazySeq.Nil, ? extends R> fn2) {
        return string.match(fn1,fn2);
    }
}
