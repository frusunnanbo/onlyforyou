package se.frusunnanbo.onlyforyou.current;

import java.util.concurrent.atomic.AtomicReference;

public class Calculation {

    private final static int NUMBER_OF_FEATURES = 10;
    private final RatingsMatrix ratings;
    private final AtomicReference<OptimizationIteration> state;

    private Calculation(RatingsMatrix ratings) {

        this.ratings = ratings;
        this.state = new AtomicReference<>(OptimizationIteration.initial(NUMBER_OF_FEATURES, ratings));
    }

    public static Calculation create(RatingsMatrix ratings) {
        return new Calculation(ratings);
    }

    public double[][] estimations() {
        return state.get().estimations();
    }

    public double loss() {
        return ratings.loss(state.get().estimations()) + LossFunctions.regularizationTerm();
    }

    public Calculation next() {
        state.getAndUpdate(OptimizationIteration::next);
        return this;
    }

}
