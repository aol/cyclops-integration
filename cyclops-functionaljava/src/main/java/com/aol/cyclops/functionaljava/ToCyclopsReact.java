package com.aol.cyclops.functionaljava;

import cyclops.async.Future;
import cyclops.control.Try;
import cyclops.control.Xor;
import fj.data.Either;
import fj.data.Validation;


public class ToCyclopsReact {


    public static <L,R> Xor<L,R> xor(Either<L,R> either){
        return either.either(Xor::secondary,Xor::primary);
    }

    public static <L,R> Xor<L,R> xor(Validation<L,R> validation){
        return validation.validation(Xor::secondary,Xor::primary);
    }


}
