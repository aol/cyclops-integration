
package cyclops.conversion.guava;


import com.google.common.base.Optional;
import cyclops.control.Maybe;
import cyclops.stream.ReactiveSeq;

public class ToCyclopsReact {

    public static <T> Maybe<T> maybe(Optional<T> option) {
        return option.isPresent() ? Maybe.just(option.get()) : Maybe.none();
    }

    public static <T> ReactiveSeq<T> reactiveSeq(Iterable<T> it) {
        return ReactiveSeq.fromIterable(it);
    }








}
