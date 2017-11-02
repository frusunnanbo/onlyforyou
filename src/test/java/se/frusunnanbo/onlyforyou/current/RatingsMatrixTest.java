package se.frusunnanbo.onlyforyou.current;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RatingsMatrixTest {

    @Test
    public void raw_returns_values_for_given_ratings() {
        RatingsMatrix ratings = RatingsMatrix.empty(2, 2)
                .add(0, 0, 4)
                .add(0, 1, 3)
                .add(1, 1, 1);
        final double[][] rawData = ratings.raw();

        assertThat(rawData[0][0]).isEqualTo(4);
    }
}