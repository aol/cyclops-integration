package cyclops.conversion.functionaljava;

import cyclops.async.Future;
import cyclops.control.Maybe;
import cyclops.control.Try;
import cyclops.control.Either;
import fj.data.Either;
import fj.data.Option;
import fj.data.Validation;



public class ToCyclopsReact {


    public static <L,R> cyclops.control.Either<L,R> xor(fj.data.Either<L,R> either){
        return either.either(cyclops.control.Either::left,cyclops.control.Either::right);
    }

    public static <L,R> cyclops.control.Either<L,R> xor(Validation<L,R> validation){
        return validation.validation(cyclops.control.Either::left,cyclops.control.Either::right);
    }
    public static <T> Maybe<T> maybe(Option<T> opt){
        return opt.isSome() ? Maybe.just(opt.some()) : Maybe.nothing();
    }

}
