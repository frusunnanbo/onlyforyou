package se.frusunnanbo.onlyforyou.current;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

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
    private final Map<Index, Double> ratings;

    private RatingsMatrix(int numRows, int numCols, Map<Index, Double> ratings) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.ratings = ratings;
    }

    private RatingsMatrix(int numRows, int numCols) {
        this(numRows, numCols, ImmutableMap.of());
    }

    public static RatingsMatrix of(int numRows, int numCols, Element... elements) {
        return of(numRows, numCols, asList(elements));
    }

    public static RatingsMatrix of(int numRows, int numCols, Collection<Element> elements) {
        final Map<Index, Double> ratingsMap = elements.stream().collect(toMap(e -> Index.index(e.row, e.column), e -> e.value));
        return new RatingsMatrix(numRows, numCols, ratingsMap);
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

    public int numberOfRows() {
        return numRows;
    }

    public int numberOfColumns() {
        return numCols;
    }

    public boolean isEmpty() {
        return ratings.isEmpty();
    }

    public Collection<Element> knownElements() {
        return ratings.entrySet().stream()
                .map(entry -> Element.element(entry.getKey().i, entry.getKey().j, entry.getValue()))
                .collect(Collectors.toList());
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

        public static Index index(int i, int j) {
            return new Index(i, j);
        }
    }

    @Value
    public static class Element {
        private final int row;
        private final int column;
        private final double value;

        public static Element element(int row, int column, double value) {
            return new Element(row, column, value);
        }
    }

}
