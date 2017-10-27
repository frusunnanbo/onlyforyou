package se.frusunnanbo.onlyforyou.current;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculationTest {

    @Test
    public void initial_estimations_has_ratings_dimensions() {
        assertThat(Calculation.initial(Collections.emptyList()).estimations())
                .isEmpty();
    }

}