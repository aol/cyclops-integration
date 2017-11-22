package cyclops.companion.functionaljava;

import cyclops.function.Function3;
import cyclops.function.Function4;
import fj.data.Reader;
import lombok.experimental.UtilityClass;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Utility class for working with JDK Optionals
 *
 * @author johnmcclean
 *
 */
@UtilityClass
public class Readers {


    public static <T,T1, T2, T3, R1, R2, R3, R> Reader<T,R> forEach4(Reader<T,? extends T1> value1,
                                                                     Function<? super T1, ? extends Reader<T,R1>> value2,
                                                                     BiFunction<? super T1, ? super R1, ? extends Reader<T,R2>> value3,
                                                                     Function3<? super T1, ? super R1, ? super R2, ? extends Reader<T,R3>> value4,
                                                                     Function4<? super T1, ? super R1, ? super R2, ? super R3, ? extends R> yieldingFunction) {

        return value1.bind(in -> {

            Reader<T,R1> a = value2.apply(in);
            return a.bind(ina -> {
                Reader<T,R2> b = value3.apply(in,ina);
                return b.bind(inb -> {
                    Reader<T,R3> c = value4.apply(in,ina,inb);
                    return c.map(in2 -> yieldingFunction.apply(in, ina, inb, in2));
                });

            });

        });

    }



    public static <T,T1, T2, R1, R2, R> Reader<T,R> forEach3(Reader<T,? extends T1> value1,
                                                         Function<? super T1, ? extends Reader<T,R1>> value2,
                                                         BiFunction<? super T1, ? super R1, ? extends Reader<T,R2>> value3,
                                                         Function3<? super T1, ? super R1, ? super R2, ? extends R> yieldingFunction) {

        return value1.bind(in -> {

            Reader<T,R1> a = value2.apply(in);
            return a.bind(ina -> {
                Reader<T,R2> b = value3.apply(in,ina);
                return b.map(in2 -> yieldingFunction.apply(in, ina, in2));
            });


        });

    }




    public static <T0,T, R1, R> Reader<T0,R> forEach2(Reader<T0,? extends T> value1,
                                                  Function<? super T, Reader<T0,R1>> value2,
                                                BiFunction<? super T, ? super R1, ? extends R> yieldingFunction) {

        return value1.bind(in -> {

            Reader<T0,R1> a = value2.apply(in);
            return a.map(in2 -> yieldingFunction.apply(in,  in2));
        });



    }






}
