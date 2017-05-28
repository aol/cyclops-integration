package cyclops.conversion.guava;

import java.util.Optional;


public class ToJDK {

    public static <T> Optional<T> optional(com.google.common.base.Optional<T> opt){
        if(opt.isPresent())
            return Optional.<T>of(opt.get());
        return Optional.empty();
    }
}
