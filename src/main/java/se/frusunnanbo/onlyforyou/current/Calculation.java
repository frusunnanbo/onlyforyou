package se.frusunnanbo.onlyforyou.current;

import se.frusunnanbo.onlyforyou.ComputationConstants;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Calculation {

    private final static int NUMBER_OF_FEATURES = 10;
    private final RatingsMatrix ratings;
    private final AtomicReference<OptimizationIteration> state;

    private Calculation(RatingsMatrix ratings) {

        this.ratings = ratings;
        this.state = new AtomicReference<>(OptimizationIteration.initial(NUMBER_OF_FEATURES, ratings));
    }

    public static Calculation create(Collection<Collection<Optional<Double>>> ratings) {
        return null;
    }

    public double[][] estimations() {
        return state.get().estimations();
    }

    public double loss() {
        return ratings.loss(state.get().estimations()) + LossFunctions.regularizationTerm();
    }

    private static Collection<Collection<Double>> randomEstimations(long numberOfUsers, long numberOfItems) {

        return Stream.generate(
                () -> Stream.generate(() -> Math.random() * 10).limit(numberOfItems).collect(toList()))
                .limit(numberOfUsers)
                .collect(toList());
    }

    public Calculation next() {
        state.getAndUpdate(oldState -> oldState.next());
        return this;
    }

    private Collection<Collection<Double>> iterate(Collection<Collection<Double>> estimations) {
        return randomEstimations(ComputationConstants.NUMBER_OF_USERS, ComputationConstants.NUMBER_OF_ITEMS);
    }

}
