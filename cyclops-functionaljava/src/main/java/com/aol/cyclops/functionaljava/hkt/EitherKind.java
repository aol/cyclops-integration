package com.aol.cyclops.functionaljava.hkt;


import com.aol.cyclops2.hkt.Higher;
import com.aol.cyclops2.hkt.Higher2;
import com.aol.cyclops2.types.foldable.To;
import cyclops.companion.functionaljava.Eithers;
import cyclops.control.Xor;
import cyclops.conversion.functionaljava.ToCyclopsReact;
import cyclops.monads.FJWitness;
import cyclops.monads.FJWitness.either;
import cyclops.monads.Witness;
import cyclops.monads.WitnessType;
import cyclops.monads.transformers.XorT;
import cyclops.typeclasses.Active;
import cyclops.typeclasses.InstanceDefinitions;
import cyclops.typeclasses.Nested;
import fj.F;
import fj.F0;
import fj.data.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.function.Function;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public class EitherKind<L,R> implements To<EitherKind<L,R>>,
                                        Higher2<either,L,R> {

    private final Either<L,R> boxed;

    public <R2> EitherKind<L,R2> fold(Function<? super Either<? super L,? super R>,? extends Either<L,R2>> op){
        return widen(op.apply(boxed));
    }
    public static <L,R> EitherKind<L,R> widen(final Either<L,R> either) {
        return new EitherKind<>(either);
    }
    public static <L,R> EitherKind<L,R> right(final R right) {
        return new EitherKind<>(Either.right(right));
    }
    public static <L, R> Either<L, R> narrowK2(final Higher2<either, L, R> xor) {
        return (Either<L, R>)xor;
    }
    public static <L, R> Either<L, R> narrowK(final Higher<Higher<either, L>, R> either) {
        return (Either<L, R>)either;
    }
    public static <L,T> Higher<Higher<either,L>,T> widenK(final Either<L,T> completableList) {

        return new EitherKind<>(
                completableList);
    }
    public Active<Higher<either,L>,R> allTypeclasses(){
        return Active.of(this, Eithers.Instances.definitions());
    }
    public <W2,R2> Nested<Higher<either,L>,W2,R2> mapM(Function<? super R,? extends Higher<W2,R2>> fn, InstanceDefinitions<W2> defs){
        return Nested.of(widen(bimap(l->l,r->fn.apply(r))), Eithers.Instances.definitions(), defs);
    }
    public <W extends WitnessType<W>> XorT<W, L,R> liftM(W witness) {
        return XorT.of(witness.adapter().unit(ToCyclopsReact.xor(boxed)));
    }

    public Either<L, R>.LeftProjection<L, R> left() {
        return boxed.left();
    }

    public Either<L, R>.RightProjection<L, R> right() {
        return boxed.right();
    }

    public boolean isLeft() {
        return boxed.isLeft();
    }

    public boolean isRight() {
        return boxed.isRight();
    }

    public <X> X either(F<L, X> left, F<R, X> right) {
        return boxed.either(left, right);
    }

    public <X, Y> Either<X, Y> bimap(F<L, X> left, F<R, Y> right) {
        return boxed.bimap(left, right);
    }

    @Override
    public boolean equals(Object other) {
        return boxed.equals(other);
    }

    @Override
    public int hashCode() {
        return boxed.hashCode();
    }

    public Either<R, L> swap() {
        return boxed.swap();
    }


    public static <A, B, X> F<F<A, X>, F<Either<A, B>, Either<X, B>>> leftMap_() {
        return Either.leftMap_();
    }

    public static <A, B, X> F<F<B, X>, F<Either<A, B>, Either<A, X>>> rightMap_() {
        return Either.rightMap_();
    }

    public static <A, B> Either<A, B> joinLeft(Either<Either<A, B>, B> e) {
        return Either.joinLeft(e);
    }

    public static <A, B> Either<A, B> joinRight(Either<A, Either<A, B>> e) {
        return Either.joinRight(e);
    }

    public static <A, X> Either<List<A>, X> sequenceLeft(List<Either<A, X>> a) {
        return Either.sequenceLeft(a);
    }

    public static <B, X> Either<X, List<B>> sequenceRight(List<Either<X, B>> a) {
        return Either.sequenceRight(a);
    }

    public <C> List<Either<L, C>> traverseListRight(F<R, List<C>> f) {
        return boxed.traverseListRight(f);
    }

    public <C> List<Either<C, R>> traverseListLeft(F<L, List<C>> f) {
        return boxed.traverseListLeft(f);
    }

    public <C> IO<Either<L, C>> traverseIORight(F<R, IO<C>> f) {
        return boxed.traverseIORight(f);
    }

    public <C> IO<Either<C, R>> traverseIOLeft(F<L, IO<C>> f) {
        return boxed.traverseIOLeft(f);
    }

    public <C> Option<Either<L, C>> traverseOptionRight(F<R, Option<C>> f) {
        return boxed.traverseOptionRight(f);
    }

    public <C> Option<Either<C, R>> traverseOptionLeft(F<L, Option<C>> f) {
        return boxed.traverseOptionLeft(f);
    }

    public <C> Stream<Either<L, C>> traverseStreamRight(F<R, Stream<C>> f) {
        return boxed.traverseStreamRight(f);
    }

    public <C> Stream<Either<C, R>> traverseStreamLeft(F<L, Stream<C>> f) {
        return boxed.traverseStreamLeft(f);
    }

    public static <A> A reduce(Either<A, A> e) {
        return Either.reduce(e);
    }

    public static <A, B> Either<A, B> iif(boolean c, F0<B> right, F0<A> left) {
        return Either.iif(c, right, left);
    }

    public static <A, B> List<A> lefts(List<Either<A, B>> es) {
        return Either.lefts(es);
    }

    public static <A, B> List<B> rights(List<Either<A, B>> es) {
        return Either.rights(es);
    }

    @Override
    public String toString() {
        return boxed.toString();
    }

    public static <L,R> EitherKind<L,R> left(final L left) {
        return new EitherKind<>(Either.left(left));
    }

    public static <A, B> F<A, Either<A, B>> left_() {
        return Either.left_();
    }

    public static <A, B> F<B, Either<A, B>> right_() {
        return Either.right_();
    }




}
