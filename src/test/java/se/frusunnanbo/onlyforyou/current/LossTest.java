package se.frusunnanbo.onlyforyou.current;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LossTest {

    private static final double[][] EMPTY = {{}};

    @Test
    public void loss_of_empty_matrices_is_0() {
        RatingsMatrix ratings = RatingsMatrix.empty(0, 0);
        assertThat(ratings.loss(EMPTY))
                .isEqualTo(0);
    }

    @Test
    public void loss_of_scalars_is_the_square_of_diff() {
        RatingsMatrix ratings = RatingsMatrix.empty(1, 1)
                .add(0, 0, 8);
        assertThat(ratings.loss(new double[][]{{6.0}}))
                .isEqualTo(4.0);

    }

    @Test
    public void averages_multiple_error_factors() {
        RatingsMatrix ratings = RatingsMatrix.empty(1, 2)
                .add(0, 0, 2)
                .add(0, 1, 3);
        assertThat(ratings.loss(new double[][]{{4, 3}}))
                .isEqualTo(2);
    }

    @Test
    public void empty_values_count_as_no_loss() {
        RatingsMatrix ratings = RatingsMatrix.empty(1, 1);

        assertThat(ratings.loss(new double[][]{{7}}))
                .isEqualTo(0);
    }

}