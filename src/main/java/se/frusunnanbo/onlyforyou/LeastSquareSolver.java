package se.frusunnanbo.onlyforyou;

import org.ejml.simple.SimpleMatrix;

import java.util.Random;

public class LeastSquareSolver {

    private final SimpleMatrix responses;
    private final SimpleMatrix confidence;
    private final double lambda;

    public LeastSquareSolver(SimpleMatrix responses, SimpleMatrix confidence, double lambda) {

        this.responses = responses;
        this.confidence = confidence;
        this.lambda = lambda;
    }

    public SimpleMatrix solve(SimpleMatrix regressors, boolean regressorsAreItems) {
        final SimpleMatrix result = SimpleMatrix.random(numRows(regressorsAreItems), regressors.numCols(), 0, 0, new Random());
        for (int i = 0; i < result.numRows(); i++) {
            final SimpleMatrix rowConfidence
                    = SimpleMatrix.diag(confidence.extractVector(regressorsAreItems, i).getMatrix().getData());
            final SimpleMatrix rowResponses = verticalRowResponses(regressorsAreItems, i);
            result.insertIntoThis(i, 0, solveRow(regressors, rowResponses, rowConfidence));
        }
        return result;
    }

    private SimpleMatrix verticalRowResponses(boolean regressorsAreItems, int i) {
        final SimpleMatrix rowResponses = responses.extractVector(regressorsAreItems, i);
        if (regressorsAreItems) {
            return rowResponses.transpose();
        }
        return rowResponses;
    }

    private int numRows(boolean regressorsAreItems) {
        if (regressorsAreItems) {
            return responses.numRows();
        }
        return responses.numCols();
    }

    private SimpleMatrix solveRow(SimpleMatrix regressors, SimpleMatrix rowResponses, SimpleMatrix rowConfidence) {
        return (regressors.transpose()
                .mult(rowConfidence)
                .mult(regressors)
                .plus(lambda, SimpleMatrix.identity(regressors.numCols())))
                .invert()
                .mult(regressors.transpose())
                .mult(rowConfidence)
                .mult(rowResponses)
                .transpose();
    }
}
