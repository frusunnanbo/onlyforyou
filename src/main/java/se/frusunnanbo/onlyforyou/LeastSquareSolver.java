package se.frusunnanbo.onlyforyou;

import org.ejml.simple.SimpleMatrix;

public class LeastSquareSolver {

    private final SimpleMatrix responses;
    private final SimpleMatrix confidence;
    private final double lambda;

    public LeastSquareSolver(SimpleMatrix responses, SimpleMatrix confidence, double lambda) {

        this.responses = responses;
        this.confidence = confidence;
        this.lambda = lambda;
    }

    public SimpleMatrix solve(SimpleMatrix regressors) {
        final SimpleMatrix result = regressors.copy();
        for (int i = 0; i < result.numRows(); i++) {
            result.insertIntoThis(i, 0, solveRow(regressors, i));
        }
        return result;
    }

    public SimpleMatrix solveRow(SimpleMatrix regressors, int row) {
        SimpleMatrix rowConfidence = SimpleMatrix.diag(confidence.extractVector(true, row).getMatrix().getData());
        SimpleMatrix rowResponses = responses.extractVector(true, row);
        return (regressors.transpose().mult(rowConfidence).mult(regressors).plus(lambda, SimpleMatrix.identity(regressors.numCols()))).invert()
                .mult(regressors.transpose()).mult(rowConfidence).mult(rowResponses.transpose())
                .transpose();
    }
}
