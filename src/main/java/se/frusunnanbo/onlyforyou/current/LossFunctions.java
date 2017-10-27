package se.frusunnanbo.onlyforyou.current;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LossFunctions {


    public static double regularizationTerm() {
        return 0;
    }

    public static double mseLoss(Collection<Collection<Optional<Double>>> ratings, Collection<Collection<Double>> estimations) {
        final List<Optional<Double>> allRatings = ratings.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        final List<Double> allEstimations = estimations.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        double accumulatedError = 0;
        for (int i = 0; i < allEstimations.size(); i++) {
            final Double guess = allEstimations.get(i);
            final Double rating = allRatings.get(i).orElse(guess);
            accumulatedError += Math.pow(guess - rating, 2);
        }
        if (allRatings.isEmpty()) {
            return 0;
        }
        return accumulatedError / allRatings.size();
    }
}
