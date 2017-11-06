package se.frusunnanbo.onlyforyou.current;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import se.frusunnanbo.onlyforyou.model.UserRating;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

@EqualsAndHashCode
@ToString
public class RatingsMatrix {

    private final int numRows;
    private final int numCols;
    private final Map<Index, Integer> ratings;

    private RatingsMatrix(int numRows, int numCols, Map<Index, Integer> ratings) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.ratings = ratings;
    }

    private RatingsMatrix(int numRows, int numCols) {
        this(numRows, numCols, ImmutableMap.of());
    }

    public static RatingsMatrix of(int numRows, int numCols, UserRating... elements) {
        return of(numRows, numCols, asList(elements));
    }

    public static RatingsMatrix of(int numRows, int numCols, Collection<UserRating> elements) {
        final Map<Index, Integer> ratingsMap = elements.stream()
                .collect(toMap(e -> Index.index(e.getUserIndex(), e.getItemIndex()), UserRating::getValue));
        return new RatingsMatrix(numRows, numCols, ratingsMap);
    }

    public static RatingsMatrix empty(int numRows, int numCols) {

        return new RatingsMatrix(numRows, numCols);
    }

    public RatingsMatrix add(int i, int j, int rating) {
        final ImmutableMap<Index, Integer> newRatings
                = ImmutableMap.<Index, Integer>builder().put(new Index(i, j), rating).putAll(ratings).build();
        return new RatingsMatrix(numRows, numCols, newRatings);
    }

    public double[][] raw() {
        final double[][] values = new double[numRows][numCols];
        ratings.entrySet().forEach(entry -> values[index(entry).i][index(entry).j] = entry.getValue());
        return values;
    }

    public double[][] known() {
        final double[][] markers = new double[numRows][numCols];
        ratings.entrySet().forEach(entry -> markers[index(entry).i][index(entry).j] = 1);
        return markers;
    }

    public double loss(double[][] estimations) {

        final double squaredErrors = ratings.entrySet().stream()
                .mapToDouble(entry -> error(entry, estimations))
                .map(error -> Math.pow(error, 2))
                .sum();

        final long numberOfElements = Arrays.stream(estimations).flatMapToDouble(Arrays::stream).count();

        return numberOfElements == 0 ? 0 : squaredErrors / numberOfElements;
    }

    public int numberOfRows() {
        return numRows;
    }

    public int numberOfColumns() {
        return numCols;
    }

    public boolean isEmpty() {
        return ratings.isEmpty();
    }

    public Collection<UserRating> knownElements() {
        return ratings.entrySet().stream()
                .map(entry -> UserRating.userRating(entry.getKey().i, entry.getKey().j, entry.getValue()))
                .collect(Collectors.toList());
    }

    private double error(Map.Entry<Index, Integer> entry, double[][] estimations) {
        final Index index = index(entry);
        return entry.getValue() - estimations[index.i][index.j];
    }

    private Index index(Map.Entry<Index, Integer> entry) {
        return entry.getKey();
    }

    @Value
    private static class Index {
        private final int i;
        private final int j;

        public static Index index(int i, int j) {
            return new Index(i, j);
        }
    }
}
