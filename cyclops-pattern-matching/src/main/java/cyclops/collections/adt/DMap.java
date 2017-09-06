package cyclops.collections.adt;

import com.aol.cyclops2.types.foldable.To;
import cyclops.control.lazy.Either;
import cyclops.control.lazy.Either3;
import cyclops.stream.ReactiveSeq;
import lombok.AllArgsConstructor;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;
import java.util.function.Function;

public interface DMap{




    public static <K1,V1,K2,V2> Two<K1,V1,K2,V2> twoEmpty(){
        return new DMap2<>(HashMap.empty(), HashMap.empty());
    }
    public static <K1,V1,K2,V2,K3,V3> Three<K1,V1,K2,V2,K3,V3> threeEmpty(){
        return new DMap3<>(HashMap.empty(), HashMap.empty(), HashMap.empty());
    }


    static interface Two<K1,V1,K2,V2> extends DMap{

        Two<K1,V1,K2,V2> put1(K1 key, V1 value);
        Two<K1,V1,K2,V2> put2(K2 key, V2 value);
        Optional<V1> get1(K1 key);
        Optional<V2> get2(K2 key);
        int size();
        <K3,V3> Three<K1,V1,K2,V2,K3,V3> merge(ImmutableMap<K3,V3> one);
        ReactiveSeq<Either<Tuple2<K1,V1>,Tuple2<K2,V2>>> stream();
        ReactiveSeq<Either<V1, V2>> streamValues();
        ReactiveSeq<Either<K1, K2>> streamKeys();

         <KR1,VR1,KR2,VR2> Two<KR1,VR1,KR2,VR2> map(Function<? super K1,? extends KR1> keyMapper1,
                                                    Function<? super V1,? extends VR1> valueMapper1,
                                                    Function<? super K2,? extends KR2> keyMapper2,
                                                    Function<? super V2,? extends VR2> valueMapper2);

    }
    static interface Three<K1,V1,K2,V2,K3,V3> extends DMap, To<Three<K1,V1,K2,V2,K3,V3>>{

        Three<K1,V1,K2,V2,K3,V3> put1(K1 key, V1 value);
        Three<K1,V1,K2,V2,K3,V3> put2(K2 key, V2 value);
        Three<K1,V1,K2,V2,K3,V3> put3(K3 key, V3 value);
        Optional<V1> get1(K1 key);
        Optional<V2> get2(K2 key);
        Optional<V3> get3(K3 key);
        int size();
        ReactiveSeq<Either3<Tuple2<K1,V1>,Tuple2<K2,V2>,Tuple2<K3,V3>>> stream();
        ReactiveSeq<Either3<K1, K2, K3>> streamKeys();
        ReactiveSeq<Either3<V1, V2, V3>> streamValues();

        <KR1,VR1,KR2,VR2,KR3,VR3> Three<KR1,VR1,KR2,VR2,KR3,VR3> map(Function<? super K1,? extends KR1> keyMapper1,
                                                                     Function<? super V1,? extends VR1> valueMapper1,
                                                                     Function<? super K2,? extends KR2> keyMapper2,
                                                                     Function<? super V2,? extends VR2> valueMapper2,
                                                                     Function<? super K3,? extends KR3> keyMapper3,
                                                                     Function<? super V3,? extends VR3> valueMapper3);
    }

    @AllArgsConstructor
    static class DMap2<K1,V1,K2,V2> implements Two<K1,V1,K2,V2> {

        ImmutableMap<K1,V1> map1;
        ImmutableMap<K2,V2> map2;

        @Override
        public Two<K1, V1, K2, V2> put1(K1 key, V1 value) {

            return new DMap2<>(map1.put(key,value),map2);
        }

        @Override
        public Optional<V1> get1(K1 key) {
            return map1.get(key);
        }


        @Override
        public int size() {
            return map1.size() + map2.size();
        }

        @Override
        public < K3, V3> Three<K1, V1, K2, V2, K3, V3> merge(ImmutableMap<K3, V3> one) {
            return new DMap3<K1, V1, K2, V2, K3, V3>(map1,map2,one);
        }

        @Override
        public Two<K1, V1, K2, V2> put2(K2 key, V2 value) {
            return new DMap2<>(map1,map2.put(key,value));
        }

        @Override
        public Optional<V2> get2(K2 key) {
            return map2.get(key);
        }


        @Override
        public ReactiveSeq<Either<Tuple2<K1, V1>, Tuple2<K2, V2>>> stream() {
            ReactiveSeq<Either<Tuple2<K1, V1>, Tuple2<K2, V2>>> x = map1.stream().map(Either::left);
            return x.mergeP(map2.stream().map(Either::right));
        }
        @Override
        public ReactiveSeq<Either<K1, K2>> streamKeys() {
            ReactiveSeq<Either<K1, K2>> x = map1.stream().map(t->t.v1).map(Either::left);
            return x.mergeP(map2.stream().map(t->t.v1).map(Either::right));
        }

        @Override
        public <KR1, VR1, KR2, VR2> Two<KR1, VR1, KR2, VR2> map(Function<? super K1, ? extends KR1> keyMapper1, Function<? super V1, ? extends VR1> valueMapper1, Function<? super K2, ? extends KR2> keyMapper2, Function<? super V2, ? extends VR2> valueMapper2) {
            return new DMap2<>(map1.bimap(keyMapper1,valueMapper1),map2.bimap(keyMapper2,valueMapper2));
        }

        @Override
        public ReactiveSeq<Either<V1, V2>> streamValues() {
            ReactiveSeq<Either<V1, V2>> x = map1.stream().map(t->t.v2).map(Either::left);
            return x.mergeP(map2.stream().map(t->t.v2).map(Either::right));
        }

    }
    @AllArgsConstructor
    static class DMap3<K1,V1,K2,V2,K3,V3> implements Three<K1,V1,K2,V2,K3,V3> {

        ImmutableMap<K1,V1> map1;
        ImmutableMap<K2,V2> map2;
        ImmutableMap<K3,V3> map3;
        @Override
        public Three<K1, V1, K2, V2, K3, V3> put1(K1 key, V1 value) {

            return new DMap3<>(map1.put(key,value),map2,map3);
        }

        @Override
        public Optional<V1> get1(K1 key) {
            return map1.get(key);
        }


        @Override
        public int size() {
            return map1.size() + map2.size() + map3.size();
        }

        @Override
        public Three<K1, V1, K2, V2, K3, V3> put2(K2 key, V2 value) {
            return new DMap3<>(map1,map2.put(key,value),map3);
        }

        @Override
        public Optional<V2> get2(K2 key) {
            return map2.get(key);
        }

        @Override
        public Three<K1, V1, K2, V2, K3, V3> put3(K3 key, V3 value) {
            return new DMap3<>(map1,map2,map3.put(key,value));
        }

        @Override
        public Optional<V3> get3(K3 key) {
            return map3.get(key);
        }

        @Override
        public ReactiveSeq<Either3<Tuple2<K1, V1>, Tuple2<K2, V2>, Tuple2<K3, V3>>> stream() {
            ReactiveSeq<Either3<Tuple2<K1, V1>, Tuple2<K2, V2>, Tuple2<K3, V3>>> x = map1.stream().map(Either3::left1);
            return x.mergeP(map2.stream().map(Either3::left2), map3.stream().map(Either3::right));
        }
        @Override
        public ReactiveSeq<Either3<K1, K2, K3>> streamKeys() {
            ReactiveSeq<Either3<K1, K2, K3>> x = map1.stream().map(t->t.v1).map(Either3::left1);
            return x.mergeP(map2.stream().map(t->t.v1).map(Either3::left2), map3.stream().map(t->t.v1).map(Either3::right));
        }
        @Override
        public ReactiveSeq<Either3<V1, V2, V3>> streamValues() {
            ReactiveSeq<Either3<V1, V2, V3>> x = map1.stream().map(t->t.v2).map(Either3::left1);
            return x.mergeP(map2.stream().map(t->t.v2).map(Either3::left2), map3.stream().map(t->t.v2).map(Either3::right));
        }

        @Override
        public <KR1, VR1, KR2, VR2, KR3, VR3> Three<KR1, VR1, KR2, VR2, KR3, VR3> map(Function<? super K1, ? extends KR1> keyMapper1, Function<? super V1, ? extends VR1> valueMapper1, Function<? super K2, ? extends KR2> keyMapper2, Function<? super V2, ? extends VR2> valueMapper2, Function<? super K3, ? extends KR3> keyMapper3, Function<? super V3, ? extends VR3> valueMapper3) {
            return new DMap3<>(map1.bimap(keyMapper1,valueMapper1),map2.bimap(keyMapper2,valueMapper2),map3.bimap(keyMapper3,valueMapper3));
        }
    }

}
