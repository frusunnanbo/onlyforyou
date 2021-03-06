package se.frusunnanbo.onlyforyou;

import org.assertj.core.api.Condition;
import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import java.util.Collection;
import java.util.Random;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class LeastSquareSolverTest {

    @Test
    public void simplest_possible_case() {
        final SimpleMatrix ratings = oneElementMatrix(1.0);
        final SimpleMatrix itemFeatures = oneElementMatrix(1.0);
        final SimpleMatrix solution = new LeastSquareSolver(ratings, fullConfidence(1), 0).solve(itemFeatures, true);
        assertThat(solution).has(numberOfRows(1));
        assertThat(solution).has(numberOfColumns(1));
        assertThat(solution).has(elements(1.0));
    }

    @Test
    public void one_nonone_element() {
        final SimpleMatrix ratings = oneElementMatrix(4.0);
        final SimpleMatrix itemFeatures = oneElementMatrix(2.0);
        final SimpleMatrix solution = new LeastSquareSolver(ratings, fullConfidence(1), 0).solve(itemFeatures, true);
        assertThat(solution).has(numberOfRows(1));
        assertThat(solution).has(numberOfColumns(1));
        assertThat(solution).has(elements(2.0));
    }

    @Test
    public void one_feature_two_users_two_items() {
        final SimpleMatrix ratings = twoByTwoMatrix(1, 1, 1, 1);
        final SimpleMatrix itemFeatures = vector(1, 1);
        final SimpleMatrix solution = new LeastSquareSolver(ratings, fullConfidence(2), 0).solve(itemFeatures, true);
        assertThat(solution).has(numberOfRows(2));
        assertThat(solution).has(numberOfColumns(1));
        assertThat(solution).has(elements(1.0, 1.0));
    }

    @Test
    public void two_features_three_users_three_items() {
        final SimpleMatrix ratings = new SimpleMatrix(new double[][]{
                {9, 19, 29},
                {12, 26, 40},
                {15, 33, 51}});
        final SimpleMatrix itemFeatures = new SimpleMatrix(new double[][]{
                {1, 2},
                {3, 4},
                {5, 6},
        });
        final SimpleMatrix solution = new LeastSquareSolver(ratings, fullConfidence(3), 0).solve(itemFeatures, true);
        assertThat(solution).has(numberOfRows(3));
        assertThat(solution).has(numberOfColumns(2));
        assertThat(solution).has(elements(1.0, 4.0, 2.0, 5.0, 3.0, 6.0));
    }

    @Test
    public void using_confidence() {
        final SimpleMatrix ratings = new SimpleMatrix(new double[][]{
                {9, 1, 29},
                {2, 26, 40},
                {15, 33, 3}});
        final SimpleMatrix itemFeatures = new SimpleMatrix(new double[][]{
                {1, 2},
                {3, 4},
                {5, 6},
        });
        final SimpleMatrix knowns = new SimpleMatrix(new double[][]{
                {1, 0, 1},
                {0, 1, 1},
                {1, 1, 0}
        });
        final SimpleMatrix solution = new LeastSquareSolver(ratings, knowns, 0).solve(itemFeatures, true);
        assertThat(solution).has(numberOfRows(3));
        assertThat(solution).has(numberOfColumns(2));
        assertThat(solution).has(elements(1.0, 4.0, 2.0, 5.0, 3.0, 6.0));
    }

    @Test
    public void using_regularisation_factor() {
        final SimpleMatrix ratings = new SimpleMatrix(new double[][]{
                {9, 1, 29},
                {2, 26, 40},
                {15, 33, 3}});
        final SimpleMatrix itemFeatures = new SimpleMatrix(new double[][]{
                {1, 2},
                {3, 4},
                {5, 6},
        });
        final SimpleMatrix knowns = new SimpleMatrix(new double[][]{
                {1, 0, 1},
                {0, 1, 1},
                {1, 1, 0}
        });
        final SimpleMatrix solution = new LeastSquareSolver(ratings, knowns, 0.01).solve(itemFeatures, true);
        assertThat(solution).has(numberOfRows(3));
        assertThat(solution).has(numberOfColumns(2));
        assertThat(solution).has(elementsCloseButNotEqualTo(1.0, 4.0, 2.0, 5.0, 3.0, 6.0));
    }


    private static SimpleMatrix fullConfidence(int dimension) {
        return SimpleMatrix.random(dimension, dimension, 1.0, 1.0, new Random());
    }


    private SimpleMatrix vector(double... elements) {
        final double[][] doubles = DoubleStream.of(elements)
                .mapToObj(e -> new double[]{e})
                .collect(toList())
                .toArray(new double[][]{{}});
        return new SimpleMatrix(doubles);
    }

    private SimpleMatrix twoByTwoMatrix(double e1, double e2, double e3, double e4) {
        return new SimpleMatrix(new double[][]{{e1, e2}, {e3, e4}});
    }

    private static SimpleMatrix oneElementMatrix(double value) {
        return new SimpleMatrix(new double[][]{{value}});
    }

    private Condition<SimpleMatrix> elements(double... expected) {
        final Collection<Double> boxed = DoubleStream.of(expected).boxed().collect(toList());
        return new Condition<>(matrix -> roughlyEquals(matrix, expected, 0.001), "elements %s", boxed);
    }

    private Condition<SimpleMatrix> elementsCloseButNotEqualTo(double... expected) {
        final Collection<Double> boxed = DoubleStream.of(expected).boxed().collect(toList());
        return new Condition<>(matrix -> !roughlyEquals(matrix, expected, 0.001)
                && roughlyEquals(matrix, expected, 0.5), "elements close but not equal to %s", boxed);
    }

    private boolean roughlyEquals(SimpleMatrix matrix, double[] expected, double allowedError) {
        final double[] actual = matrix.getMatrix().getData();
        for (int i = 0; i < actual.length; i++) {
            if (Math.abs(actual[i] - expected[i]) > allowedError) {
                return false;
            }
        }
        return true;
    }

    private Condition<SimpleMatrix> numberOfRows(int expected) {
        return new Condition<>(matrix -> matrix.getMatrix().getNumRows() == expected, "%d rows", expected);
    }

    private Condition<SimpleMatrix> numberOfColumns(int expected) {
        return new Condition<>(matrix -> matrix.getMatrix().getNumCols() == expected, "%d columns", expected);
    }
}
