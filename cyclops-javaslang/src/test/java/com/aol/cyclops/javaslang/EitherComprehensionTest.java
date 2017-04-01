package com.aol.cyclops.javaslang;

import com.aol.cyclops.control.For;
import javaslang.control.Either;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class EitherComprehensionTest {

    /*
        def prepareCappuccino(): Either[Exception, String] = for {
            ground <- grind("arabica beans")
            water <- heatWater(Water(25))
            espresso <- brew(ground, water)
            foam <- frothMilk("milk")
        } yield combine(espresso, foam)
     */

    private static final Exception AN_EXCEPTION = new RuntimeException();

    @Test
    public void shouldComprehendEitherExpressions() throws Exception {
        // when
        final Either<Exception, String> wrapsCappuccino = For
                .iterable(grind("arabica beans"))
                .iterable(ground -> heatWater(new Water(25)))
                .iterable(ground -> water -> brew(ground, water))
                .iterable(ground -> water -> espresso -> frothMilk("milk"))
                .yield(ground -> water -> espresso -> foam -> combine(espresso, foam))
                .unwrap();

        // then
        assertThat(
                wrapsCappuccino.get(),
                equalTo("cappuccino")
        );
    }

    @Test
    public void shouldComprehendEitherExpressionsWithFailedOne() throws Exception {
        // when
        final Either<Exception, String> wrapsException = For
                .iterable(grind("arabica beans"))
                .iterable(ground -> failToHeatWater(new Water(25)))
                .iterable(ground -> water -> brew(ground, water))
                .iterable(ground -> water -> espresso -> frothMilk("milk"))
                .yield(ground -> water -> espresso -> foam -> combine(espresso, foam))
                .unwrap();

        // then
        assertThat(
                wrapsException.getLeft(),
                equalTo(AN_EXCEPTION)
        );
    }

    @Test
    public void shouldComprehendEitherProjectionExpressions() throws Exception {
        // when
        final Either.RightProjection<Exception, String> wrapsCappuccino = For
                .iterable(rightProjectionOf(grind("arabica beans")))
                .iterable(ground -> rightProjectionOf(heatWater(new Water(25))))
                .iterable(ground -> water -> rightProjectionOf(brew(ground, water)))
                .iterable(ground -> water -> espresso -> rightProjectionOf(frothMilk("milk")))
                .yield(ground -> water -> espresso -> foam -> combine(espresso, foam))
                .unwrap();

        // then
        assertThat(
                wrapsCappuccino.get(),
                equalTo("cappuccino")
        );
    }

    @Test
    public void shouldComprehendEitherExpressionsMixedWithEitherProjectionExpressions() throws Exception {
        // when
        final Either<Exception, String> wrapsCappuccino = For
                .iterable(grind("arabica beans"))
                .iterable(ground -> heatWater(new Water(25)))
                .iterable(ground -> water -> brew(ground, water))
                .iterable(ground -> water -> espresso -> rightProjectionOf(frothMilk("milk")))
                .yield(ground -> water -> espresso -> foam -> combine(espresso, foam))
                .unwrap();

        // then
        assertThat(
                wrapsCappuccino.get(),
                equalTo("cappuccino")
        );
    }


    Either<Exception, String> grind(String beans) {
        return Either.right("ground " + beans);
    }

    Either<Exception, Water> heatWater(Water water) {
        return Either.right(water.withTemperature(85));
    }

    Either<Exception, Water> failToHeatWater(Water water) {
        return Either.left(AN_EXCEPTION);
    }

    Either<Exception, String> brew(String coffee, Water heatedWater) {
        return Either.right("espresso");
    }

    Either<Exception, String> frothMilk(String milk) {
        return Either.right("frothed " + milk);
    }

    String combine(String espresso, String frothedMilk) {
        return "cappuccino";
    }

    private static <L, R> Either.RightProjection<L, R> rightProjectionOf(Either<L, R> either) {
        return either.right();
    }
}
