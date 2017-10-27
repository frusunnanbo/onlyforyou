package se.frusunnanbo.onlyforyou.current;

import se.frusunnanbo.onlyforyou.ComputationConstants;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.stream.Collectors.toList;

public class Iteration {

    private final Collection<Collection<Optional<Double>>> ratings;
    private final Collection<Collection<Double>> estimations;

    private Iteration(Collection<Collection<Optional<Double>>> ratings, Collection<Collection<Double>> estimations) {
        this.ratings = ratings;
        this.estimations = estimations;
    }

    public static Iteration initial(Collection<Collection<Optional<Double>>> ratings) {
        return new Iteration(copyOf(ratings), randomEstimations(ComputationConstants.NUMBER_OF_USERS, ComputationConstants.NUMBER_OF_ITEMS));
    }

    public Collection<Collection<Double>> estimations() {
        return estimations;
    }

    public double loss() {
        return 0.5;
    }

    private static Collection<Collection<Double>> randomEstimations(int numberOfUsers, int numberOfItems) {

        return Stream.generate(
                () -> Stream.generate(() -> Math.random() * 10).limit(numberOfItems).collect(toList()))
                .limit(numberOfUsers)
                .collect(toList());
    }

    public Iteration next() {
        return new Iteration(copyOf(ratings), iterate(estimations));
    }

    private Collection<Collection<Double>> iterate(Collection<Collection<Double>> estimations) {
        return randomEstimations(ComputationConstants.NUMBER_OF_USERS, ComputationConstants.NUMBER_OF_ITEMS);
    }


}
