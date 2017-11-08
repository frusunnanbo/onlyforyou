package se.frusunnanbo.onlyforyou.current;

import se.frusunnanbo.onlyforyou.model.State;

import java.util.concurrent.atomic.AtomicReference;

public class Calculation {

    private final static int NUMBER_OF_FEATURES = 5;
    private final RatingsMatrix ratings;
    private final AtomicReference<OptimizationIteration> state;

    private Calculation(RatingsMatrix ratings) {

        this.ratings = ratings;
        this.state = new AtomicReference<>(OptimizationIteration.initial(NUMBER_OF_FEATURES, ratings));
    }

    public static Calculation create(RatingsMatrix ratings) {
        return new Calculation(ratings);
    }

    private double loss(OptimizationIteration iteration) {
        // TODO fixme
        return ratings.loss(iteration.estimations()) + LossFunctions.regularizationTerm();
    }

    public State state() {
        final OptimizationIteration current = state.get();
        return new State(
                loss(current),
                current.userFeatures(),
                current.itemFeatures(),
                current.estimations());
    }

    public Calculation next() {
        state.getAndUpdate(OptimizationIteration::next);
        return this;
    }

}
