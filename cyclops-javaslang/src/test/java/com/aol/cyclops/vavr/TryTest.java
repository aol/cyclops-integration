package com.aol.cyclops.vavr;

import cyclops.companion.vavr.Trys;
import org.junit.Test;



import javaslang.control.Try;

public class TryTest {

    /**
     * 
    def prepareCappuccino(): Try[Cappuccino] = for {
    ground <- Try(grind("arabica beans"))
    water <- Try(heatWater(Water(25)))
    espresso <- Try(brew(ground, water))
    foam <- Try(frothMilk("milk"))
    } yield combine(espresso, foam)
     */
    @Test
    public void futureTest() {

        Try<String> result = Trys.forEach4(grind("arabica beans"),
                                           ground -> heatWater(new Water(
                                                                        25)),
                                            (ground, water) -> brew(ground, water),
                                            (ground, water ,espresso) -> frothMilk("milk"),
                                            (ground , water ,espresso , foam) -> combine(espresso, foam));

        System.out.println(result.get());
    }

    Try<String> grind(String beans) {
        return Try.of(() -> "ground coffee of " + beans);
    }

    Try<Water> heatWater(Water water) {
        return Try.of(() -> water.withTemperature(85));

    }

    Try<String> frothMilk(String milk) {
        return Try.of(() -> "frothed " + milk);
    }

    Try<String> brew(String coffee, Water heatedWater) {
        return Try.of(() -> "espresso");
    }

    String combine(String espresso, String frothedMilk) {
        return "cappuccino";
    }
}
