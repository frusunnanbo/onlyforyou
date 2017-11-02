package se.frusunnanbo.onlyforyou.current;

import com.google.common.collect.ImmutableMap;
import lombok.Value;

import java.util.Arrays;
import java.util.Map;

public class RatingsMatrix {

    private final int numRows;
    private final int numCols;
    private final Map<Index, Double> ratings;

    private RatingsMatrix(int numRows, int numCols, Map<Index, Double> ratings) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.ratings = ratings;
    }

    private RatingsMatrix(int numRows, int numCols) {
        this(numRows, numCols, ImmutableMap.of());
    }

    public static RatingsMatrix empty(int numRows, int numCols) {
        return new RatingsMatrix(numRows, numCols);
    }

    public RatingsMatrix add(int i, int j, double rating) {
        final ImmutableMap<Index, Double> newRatings
                = ImmutableMap.<Index, Double>builder().put(new Index(i, j), rating).putAll(ratings).build();
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

    private double error(Map.Entry<Index, Double> entry, double[][] estimations) {
        final Index index = index(entry);
        return entry.getValue() - estimations[index.i][index.j];
    }

    private Index index(Map.Entry<Index, Double> entry) {
        return entry.getKey();
    }

    @Value
    private static class Index {
        private final int i;
        private final int j;
    }

}
