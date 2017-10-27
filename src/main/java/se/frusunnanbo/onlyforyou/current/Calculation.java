package se.frusunnanbo.onlyforyou.current;

import se.frusunnanbo.onlyforyou.ComputationConstants;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.stream.Collectors.toList;

public class Calculation {

    private final Collection<Collection<Optional<Double>>> ratings;
    private final Collection<Collection<Double>> estimations;

    private Calculation(Collection<Collection<Optional<Double>>> ratings, Collection<Collection<Double>> estimations) {
        this.ratings = ratings;
        this.estimations = estimations;
    }

    public static Calculation initial(Collection<Collection<Optional<Double>>> ratings) {
        long numberOfUsers = ratings.size();
        long numberOfItems = ratings.stream().flatMap(Collection::stream).count() / numberOfUsers;
        return new Calculation(copyOf(ratings), randomEstimations(numberOfUsers, numberOfItems));
    }

    public Collection<Collection<Double>> estimations() {
        return estimations;
    }

    public double loss() {
        return LossFunctions.mseLoss(ratings, estimations) + LossFunctions.regularizationTerm();
    }

    private static Collection<Collection<Double>> randomEstimations(long numberOfUsers, long numberOfItems) {

        return Stream.generate(
                () -> Stream.generate(() -> Math.random() * 10).limit(numberOfItems).collect(toList()))
                .limit(numberOfUsers)
                .collect(toList());
    }

    public Calculation next() {
        return new Calculation(copyOf(ratings), iterate(estimations));
    }

    private Collection<Collection<Double>> iterate(Collection<Collection<Double>> estimations) {
        return randomEstimations(ComputationConstants.NUMBER_OF_USERS, ComputationConstants.NUMBER_OF_ITEMS);
    }


}
