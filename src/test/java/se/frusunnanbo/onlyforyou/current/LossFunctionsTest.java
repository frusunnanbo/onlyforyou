package se.frusunnanbo.onlyforyou.current;

import org.junit.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.frusunnanbo.onlyforyou.current.LossFunctions.mseLoss;

public class LossFunctionsTest {

    @Test
    public void loss_of_empty_matrices_is_0() {
        assertThat(mseLoss(emptyList(), emptyList()))
                .isEqualTo(0);
    }

    @Test
    public void loss_of_scalars_is_the_square_of_diff() {
        assertThat(mseLoss(singletonList(singletonList(Optional.of(8.0))), singletonList(singletonList(6.0))))
                .isEqualTo(4.0);

    }

    @Test
    public void averages_multiple_error_factors() {
        assertThat(mseLoss(
                singletonList(asList(Optional.of(2.0), Optional.of(3.0))),
                singletonList(asList(4.0, 3.0))))
                .isEqualTo(2.0);
    }

    @Test
    public void empty_values_count_as_no_loss() {
        assertThat(mseLoss(singletonList(singletonList(Optional.empty())), singletonList(singletonList(7.0))))
                .isEqualTo(0);
    }

}