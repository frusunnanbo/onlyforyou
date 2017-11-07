package se.frusunnanbo.onlyforyou.current;

import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static se.frusunnanbo.onlyforyou.input.UserInput.userInput;

public class OptimizationIterationTest {

    private static final RatingsMatrix RATINGS = RatingsMatrix.empty(3, 3)
            .add(0, 0, 4)
            .add(0, 1, 3)
            .add(1, 1, 1)
            .add(1, 2, 5)
            .add(2, 1, 3)
            .add(2, 2, 4);
    private static final OptimizationIteration FIRST_ITERATION = OptimizationIteration.initial(2, RATINGS);

    @Test
    public void first_fixed_feature_set_is_user() {
        assertThat(FIRST_ITERATION.getFixedFeatures()).isEqualTo(OptimizationIteration.FixedFeatures.USER);
    }

    @Test
    public void next_fixed_feature_set_is_item() {
        assertThat(FIRST_ITERATION.next().getFixedFeatures()).isEqualTo(OptimizationIteration.FixedFeatures.ITEM);
    }

    @Test
    public void fixes_user_on_next() {
        final SimpleMatrix userFeatures = FIRST_ITERATION.getUserFeatures();
        assertThat(FIRST_ITERATION.next().getUserFeatures()).isEqualTo(userFeatures);
    }

    @Test
    public void changes_items_on_next() {
        final SimpleMatrix itemFeatures = FIRST_ITERATION.getItemFeatures();
        assertThat(FIRST_ITERATION.next().getItemFeatures()).isNotEqualTo(itemFeatures);
    }

    @Test
    public void fixes_items_on_next_next() {
        final OptimizationIteration next = FIRST_ITERATION.next();
        final SimpleMatrix itemFeatures = next.getItemFeatures();
        assertThat(next.next().getItemFeatures()).isEqualTo(itemFeatures);
    }

    @Test
    public void loss_decreases() {
        assertThat(loss(FIRST_ITERATION))
                .isGreaterThan(loss(FIRST_ITERATION.next()));
    }

    @Test
    public void loss_converges() {
        OptimizationIteration iteration = FIRST_ITERATION;
        for (int i = 0; i < 30; i++) {
            System.out.println("LOSS: " + loss(iteration));
            System.out.println(new SimpleMatrix(iteration.estimations()));
            iteration = iteration.next();
        }
        assertThat(loss(iteration))
                .isCloseTo(loss(iteration.next()), offset(0.001));
    }

    @Test
    public void loss_converges_with_demo_data() {
        OptimizationIteration iteration = OptimizationIteration.initial(10, userInput().toRatingsMatrix());
        for (int i = 0; i < 30; i++) {
            System.out.println("LOSS: " + loss(iteration));
            System.out.println(new SimpleMatrix(iteration.estimations()));
            iteration = iteration.next();
        }
        assertThat(loss(iteration))
                .isCloseTo(loss(iteration.next()), offset(0.01));
    }

    private double loss(OptimizationIteration iteration) {
        return RATINGS.loss(iteration.estimations());
    }
}
