package se.frusunnanbo.onlyforyou.current;

import lombok.Value;
import org.ejml.simple.SimpleMatrix;
import se.frusunnanbo.onlyforyou.LeastSquareSolver;

import java.util.Random;

import static se.frusunnanbo.onlyforyou.current.OptimizationIteration.FixedFeatures.ITEM;
import static se.frusunnanbo.onlyforyou.current.OptimizationIteration.FixedFeatures.USER;

@Value
public class OptimizationIteration {

    enum FixedFeatures {USER, ITEM}

    private final SimpleMatrix userFeatures;
    private final SimpleMatrix itemFeatures;
    private final FixedFeatures fixedFeatures;
    private final LeastSquareSolver leastSquareSolver;

    public static OptimizationIteration initial(int numberOfFeatures, RatingsMatrix ratings) {
        SimpleMatrix ratingsMatrix = new SimpleMatrix(ratings.raw());
        SimpleMatrix userFeatures
                = generateRandomFeatures(numberOfFeatures, ratingsMatrix.numRows());
        SimpleMatrix itemFeatures
                = generateRandomFeatures(numberOfFeatures, ratingsMatrix.numCols());

        final LeastSquareSolver leastSquareSolver
                = new LeastSquareSolver(ratingsMatrix, new SimpleMatrix(ratings.known()), 0.001);

        return new OptimizationIteration(userFeatures, itemFeatures, USER, leastSquareSolver);
    }

    public OptimizationIteration next() {
        return new OptimizationIteration(nextUserFeatures(), nextItemFeatures(), nextFixedFeatures(), leastSquareSolver);
    }

    public double[][] estimations() {

        final SimpleMatrix estimations = userFeatures.mult(itemFeatures.transpose());

        final double[][] rawEstimations = new double[estimations.numRows()][];
        for (int i = 0; i < estimations.numRows(); i++) {
            final SimpleMatrix row = estimations.extractVector(true, i);
            rawEstimations[i] = row.getMatrix().getData();
        }
        return rawEstimations;
    }

    private SimpleMatrix nextUserFeatures() {
        if (fixedFeatures.equals(USER)) {
            return userFeatures;
        }
        return leastSquareSolver.solve(itemFeatures, true);
    }

    private SimpleMatrix nextItemFeatures() {
        if (fixedFeatures.equals(ITEM)) {
            return itemFeatures;
        }
        return leastSquareSolver.solve(userFeatures, false);
    }

    private FixedFeatures nextFixedFeatures() {
        return fixedFeatures.equals(USER) ? ITEM : USER;
    }

    private static SimpleMatrix generateRandomFeatures(int numberOfFeatures, int numberOfRows) {
        return SimpleMatrix.random(numberOfRows, numberOfFeatures, 0, 10, new Random());
    }


}
